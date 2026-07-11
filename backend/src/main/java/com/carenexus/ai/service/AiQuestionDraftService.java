package com.carenexus.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.ai.AiTrainingService;
import com.carenexus.ai.TrainingAiRequest;
import com.carenexus.ai.TrainingAiSource;
import com.carenexus.ai.dto.TrainingAiRequests.QuestionDraftRequest;
import com.carenexus.ai.dto.TrainingAiRequests.ReviewRequest;
import com.carenexus.ai.entity.AiDraft;
import com.carenexus.ai.entity.AiDraftSourceResource;
import com.carenexus.ai.mapper.AiDraftMapper;
import com.carenexus.ai.mapper.AiDraftSourceResourceMapper;
import com.carenexus.ai.vo.TrainingAiResponses;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.training.constant.ExamQuestionType;
import com.carenexus.training.constant.ExamStatus;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.ExamQuestionOption;
import com.carenexus.training.mapper.ExamQuestionMapper;
import com.carenexus.training.mapper.ExamQuestionOptionMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AiQuestionDraftService {
    private static final String DRAFT = "DRAFT";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final String DRAFT_TYPE = "QUESTION";

    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingAiSourceService sourceService;
    private final AiTrainingService aiTrainingService;
    private final AiDraftMapper draftMapper;
    private final AiDraftSourceResourceMapper sourceMapper;
    private final ExamQuestionMapper questionMapper;
    private final ExamQuestionOptionMapper optionMapper;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    public AiQuestionDraftService(TrainingResourceAccessPolicy accessPolicy,
            TrainingAiSourceService sourceService, AiTrainingService aiTrainingService,
            AiDraftMapper draftMapper, AiDraftSourceResourceMapper sourceMapper,
            ExamQuestionMapper questionMapper, ExamQuestionOptionMapper optionMapper,
            OperationLogService operationLogService, ObjectMapper objectMapper) {
        this.accessPolicy = accessPolicy;
        this.sourceService = sourceService;
        this.aiTrainingService = aiTrainingService;
        this.draftMapper = draftMapper;
        this.sourceMapper = sourceMapper;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
        this.operationLogService = operationLogService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TrainingAiResponses.DraftBatchResponse generate(QuestionDraftRequest request) {
        CurrentUser currentUser = accessPolicy.requireManage();
        String questionType = normalizeQuestionType(request.getQuestionType());
        List<TrainingAiSource> sources = sourceService.load(request.getSourceResourceIds());
        TrainingAiResponses.DraftBatchResponse response = new TrainingAiResponses.DraftBatchResponse();
        for (int index = 0; index < request.getCount(); index++) {
            TrainingAiRequest aiRequest = aiRequest(sources, questionType, index);
            String generatedContent = aiTrainingService.generateQuestionDraft(aiRequest).getContent();
            QuestionDraftPayload payload = payload(questionType, generatedContent, sources.get(0), index);
            AiDraft draft = saveDraft(aiRequest.getPrompt(), payload, sources);
            response.draftIds.add(draft.getId());
            operationLogService.record(currentUser, "AI_QUESTION_DRAFT_CREATE", "AI_DRAFT",
                    draft.getId(), "SUCCESS");
        }
        return response;
    }

    public PageResponse<TrainingAiResponses.DraftResponse> page(String status, int pageNo, int pageSize) {
        accessPolicy.requireManage();
        QueryWrapper<AiDraft> wrapper = new QueryWrapper<AiDraft>()
                .eq("draft_type", DRAFT_TYPE).orderByDesc("created_at").orderByDesc("id");
        if (StringUtils.hasText(status)) {
            wrapper.eq("draft_status", normalizeDraftStatus(status));
        }
        Page<AiDraft> page = draftMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<TrainingAiResponses.DraftResponse> records = page.getRecords().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return PageResponse.from(page, records);
    }

    public TrainingAiResponses.DraftResponse detail(Long id) {
        accessPolicy.requireManage();
        return toResponse(requireDraft(id));
    }

    @Transactional
    public TrainingAiResponses.DraftResponse review(Long id, ReviewRequest request) {
        CurrentUser reviewer = accessPolicy.requireManage();
        AiDraft draft = requireDraft(id);
        if (!DRAFT.equals(draft.getDraftStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "AI draft has already been reviewed");
        }
        String result = normalizeReviewResult(request.getReviewResult());
        draft.setDraftStatus(result);
        draft.setReviewedBy(reviewer.getUserId());
        draft.setReviewedAt(LocalDateTime.now());
        draft.setReviewComment(trim(request.getComment()));
        int updated = draftMapper.update(draft, new UpdateWrapper<AiDraft>()
                .eq("id", id).eq("draft_status", DRAFT));
        if (updated != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "AI draft status changed concurrently");
        }
        Long questionId = null;
        if (APPROVED.equals(result)) {
            questionId = createQuestion(draft, parse(draft.getDraftContent()));
        }
        operationLogService.record(reviewer, "AI_QUESTION_DRAFT_" + result,
                "AI_DRAFT", draft.getId(), "SUCCESS");
        TrainingAiResponses.DraftResponse response = toResponse(draft);
        response.questionId = questionId;
        return response;
    }

    private AiDraft saveDraft(String prompt, QuestionDraftPayload payload, List<TrainingAiSource> sources) {
        AiDraft draft = new AiDraft();
        draft.setDraftType(DRAFT_TYPE);
        draft.setPrompt(prompt);
        draft.setDraftContent(write(payload));
        draft.setDraftStatus(DRAFT);
        draftMapper.insert(draft);
        for (TrainingAiSource source : sources) {
            AiDraftSourceResource relation = new AiDraftSourceResource();
            relation.setDraftId(draft.getId());
            relation.setResourceId(source.getResourceId());
            sourceMapper.insert(relation);
        }
        return draft;
    }

    private Long createQuestion(AiDraft draft, QuestionDraftPayload payload) {
        List<Long> sourceIds = sourceIds(draft.getId());
        if (sourceIds.isEmpty()) {
            throw new BusinessException(ErrorCode.CONFLICT, "AI draft has no source resource");
        }
        ExamQuestion question = new ExamQuestion();
        question.setResourceId(sourceIds.get(0));
        question.setQuestionType(payload.questionType);
        question.setQuestionContent(payload.questionContent);
        question.setStandardAnswer(payload.standardAnswer);
        question.setAnalysis(payload.analysis);
        question.setQuestionStatus(ExamStatus.DRAFT);
        question.setSourceAiDraftId(draft.getId());
        questionMapper.insert(question);
        if (ExamQuestionType.SINGLE_CHOICE.equals(payload.questionType)) {
            int sortNo = 1;
            for (QuestionDraftPayload.OptionPayload payloadOption : payload.options) {
                ExamQuestionOption option = new ExamQuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel(payloadOption.label);
                option.setOptionContent(payloadOption.content);
                option.setIsCorrect(payloadOption.correct ? 1 : 0);
                option.setSortNo(sortNo++);
                optionMapper.insert(option);
            }
        }
        return question.getId();
    }

    private TrainingAiResponses.DraftResponse toResponse(AiDraft draft) {
        QuestionDraftPayload payload = parse(draft.getDraftContent());
        TrainingAiResponses.DraftResponse response = new TrainingAiResponses.DraftResponse();
        response.id = draft.getId();
        response.questionType = payload.questionType;
        response.questionContent = payload.questionContent;
        response.standardAnswer = payload.standardAnswer;
        response.analysis = payload.analysis;
        response.options = payload.options.stream().map(this::toOption).collect(Collectors.toList());
        response.draftStatus = draft.getDraftStatus();
        response.sourceResources = sourceService.load(sourceIds(draft.getId())).stream()
                .map(this::toReference).collect(Collectors.toList());
        response.reviewedBy = draft.getReviewedBy();
        response.reviewedAt = draft.getReviewedAt();
        response.reviewComment = draft.getReviewComment();
        response.createdAt = draft.getCreatedAt();
        ExamQuestion question = questionMapper.selectOne(new QueryWrapper<ExamQuestion>()
                .eq("source_ai_draft_id", draft.getId()).eq("is_deleted", 0));
        response.questionId = question == null ? null : question.getId();
        return response;
    }

    private QuestionDraftPayload payload(String type, String content, TrainingAiSource source, int index) {
        QuestionDraftPayload payload = new QuestionDraftPayload();
        payload.questionType = type;
        payload.questionContent = content + (index == 0 ? "" : "（草稿" + (index + 1) + "）");
        payload.analysis = "依据《" + source.getTitle() + "》生成，发布前须由培训管理员审核。";
        if (ExamQuestionType.TRUE_FALSE.equals(type)) {
            payload.standardAnswer = "TRUE";
            return payload;
        }
        payload.standardAnswer = "A";
        payload.options.add(new QuestionDraftPayload.OptionPayload("A", excerpt(source.getContent()), true));
        payload.options.add(new QuestionDraftPayload.OptionPayload("B", "忽略培训资料中的规范要求", false));
        payload.options.add(new QuestionDraftPayload.OptionPayload("C", "未经确认自行改变护理流程", false));
        payload.options.add(new QuestionDraftPayload.OptionPayload("D", "用医疗诊断替代护理培训", false));
        return payload;
    }

    private TrainingAiRequest aiRequest(List<TrainingAiSource> sources, String type, int index) {
        TrainingAiRequest request = new TrainingAiRequest();
        request.setSources(sources);
        request.setSourceResourceIds(sources.stream().map(TrainingAiSource::getResourceId)
                .collect(Collectors.toList()));
        request.setQuestionType(type);
        request.setPrompt("生成第" + (index + 1) + "道" + type + "护理培训题目草稿");
        return request;
    }

    private List<Long> sourceIds(Long draftId) {
        return sourceMapper.selectList(new QueryWrapper<AiDraftSourceResource>()
                        .eq("draft_id", draftId).orderByAsc("id"))
                .stream().map(AiDraftSourceResource::getResourceId).collect(Collectors.toList());
    }

    private AiDraft requireDraft(Long id) {
        AiDraft draft = draftMapper.selectById(id);
        if (draft == null || !DRAFT_TYPE.equals(draft.getDraftType())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "AI question draft not found");
        }
        return draft;
    }

    private String normalizeQuestionType(String value) {
        String type = value == null ? "" : value.trim().toUpperCase();
        if (!Arrays.asList(ExamQuestionType.SINGLE_CHOICE, ExamQuestionType.TRUE_FALSE).contains(type)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "Only SINGLE_CHOICE and TRUE_FALSE question drafts are supported");
        }
        return type;
    }

    private String normalizeDraftStatus(String value) {
        String status = value.trim().toUpperCase();
        if (!Arrays.asList(DRAFT, APPROVED, REJECTED).contains(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported AI draft status");
        }
        return status;
    }

    private String normalizeReviewResult(String value) {
        String result = value.trim().toUpperCase();
        if (!APPROVED.equals(result) && !REJECTED.equals(result)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Review result must be APPROVED or REJECTED");
        }
        return result;
    }

    private TrainingAiResponses.Option toOption(QuestionDraftPayload.OptionPayload source) {
        TrainingAiResponses.Option option = new TrainingAiResponses.Option();
        option.label = source.label;
        option.content = source.content;
        option.correct = source.correct;
        return option;
    }

    private TrainingAiResponses.Reference toReference(TrainingAiSource source) {
        TrainingAiResponses.Reference reference = new TrainingAiResponses.Reference();
        reference.resourceId = source.getResourceId();
        reference.title = source.getTitle();
        return reference;
    }

    private String write(QuestionDraftPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to serialize AI draft");
        }
    }

    private QuestionDraftPayload parse(String content) {
        try {
            return objectMapper.readValue(content, QuestionDraftPayload.class);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI draft content is invalid");
        }
    }

    private String excerpt(String content) {
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 100 ? normalized : normalized.substring(0, 100) + "...";
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
