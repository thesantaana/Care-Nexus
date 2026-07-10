package com.carenexus.training.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.constant.ExamStatus;
import com.carenexus.training.constant.ExamQuestionType;
import com.carenexus.training.dto.QuestionOptionRequest;
import com.carenexus.training.dto.QuestionOptionsRequest;
import com.carenexus.training.dto.QuestionRequest;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.ExamQuestionOption;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.ExamQuestionMapper;
import com.carenexus.training.mapper.ExamQuestionOptionMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.support.TrainingText;
import com.carenexus.training.vo.QuestionResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TrainingQuestionService {

    private static final int ACTIVE_FLAG = 0;

    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingExamSupport support;
    private final TrainingExamAssembler assembler;
    private final ExamQuestionMapper questionMapper;
    private final ExamQuestionOptionMapper optionMapper;

    public TrainingQuestionService(TrainingResourceAccessPolicy accessPolicy, TrainingExamSupport support,
            TrainingExamAssembler assembler, ExamQuestionMapper questionMapper, ExamQuestionOptionMapper optionMapper) {
        this.accessPolicy = accessPolicy;
        this.support = support;
        this.assembler = assembler;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
    }

    public QuestionResponse createQuestion(QuestionRequest request) {
        accessPolicy.requireManage();
        ExamQuestion question = new ExamQuestion();
        applyQuestionRequest(question, request);
        question.setQuestionStatus(ExamStatus.DRAFT);
        question.setIsDeleted(ACTIVE_FLAG);
        questionMapper.insert(question);
        return assembler.toQuestionResponse(question, true);
    }

    public QuestionResponse updateQuestion(Long id, QuestionRequest request) {
        accessPolicy.requireManage();
        ExamQuestion question = support.requireQuestion(id);
        applyQuestionRequest(question, request);
        String status = TrainingText.optional(request.getStatus());
        if (StringUtils.hasText(status)) {
            support.validatePublishedDraft(status, "Invalid question status");
            question.setQuestionStatus(status);
        }
        questionMapper.updateById(question);
        return assembler.toQuestionResponse(question, true);
    }

    @Transactional
    public QuestionResponse replaceQuestionOptions(Long id, QuestionOptionsRequest request) {
        accessPolicy.requireManage();
        ExamQuestion question = support.requireQuestion(id);
        if (ExamQuestionType.TRUE_FALSE.equals(question.getQuestionType())) {
            throw new BusinessException(ErrorCode.CONFLICT, "True/false question does not use options");
        }
        validateSingleChoiceOptions(request.getOptions());
        optionMapper.delete(new QueryWrapper<ExamQuestionOption>().eq("question_id", id));
        for (QuestionOptionRequest optionRequest : request.getOptions()) {
            ExamQuestionOption option = new ExamQuestionOption();
            option.setQuestionId(id);
            option.setOptionLabel(TrainingText.required(optionRequest.getOptionLabel(), "Option label is required"));
            option.setOptionContent(TrainingText.required(optionRequest.getOptionContent(),
                    "Option content is required"));
            option.setIsCorrect(Boolean.TRUE.equals(optionRequest.getCorrect()) ? 1 : 0);
            option.setSortNo(optionRequest.getSortNo());
            optionMapper.insert(option);
        }
        return assembler.toQuestionResponse(question, true);
    }

    private void applyQuestionRequest(ExamQuestion question, QuestionRequest request) {
        String type = TrainingText.required(request.getQuestionType(), "Question type is required");
        support.validateQuestionType(type);
        TrainingResource resource = support.requirePublishedResource(request.getResourceId());
        String answer = TrainingText.required(request.getStandardAnswer(), "Standard answer is required");
        question.setResourceId(resource.getId());
        question.setQuestionType(type);
        question.setQuestionContent(TrainingText.required(request.getQuestionContent(),
                "Question content is required"));
        question.setStandardAnswer(ExamQuestionType.TRUE_FALSE.equals(type) ? normalizeBoolean(answer) : answer);
        question.setAnalysis(TrainingText.optional(request.getAnalysis()));
    }

    private void validateSingleChoiceOptions(java.util.List<QuestionOptionRequest> options) {
        int correctCount = 0;
        Set<String> labels = new LinkedHashSet<>();
        for (QuestionOptionRequest option : options) {
            String label = TrainingText.required(option.getOptionLabel(), "Option label is required").toUpperCase();
            if (!labels.add(label)) {
                throw new BusinessException(ErrorCode.CONFLICT, "Duplicate option label");
            }
            if (Boolean.TRUE.equals(option.getCorrect())) {
                correctCount++;
            }
        }
        if (correctCount != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Single choice question must have one correct option");
        }
    }

    private String normalizeBoolean(String value) {
        String normalized = TrainingText.required(value, "Boolean answer is required").toLowerCase();
        if ("true".equals(normalized) || "正确".equals(normalized)) {
            return "true";
        }
        if ("false".equals(normalized) || "错误".equals(normalized)) {
            return "false";
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "Boolean answer must be true or false");
    }
}
