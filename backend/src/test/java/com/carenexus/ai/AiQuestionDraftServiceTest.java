package com.carenexus.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carenexus.ai.dto.TrainingAiRequests.QuestionDraftRequest;
import com.carenexus.ai.dto.TrainingAiRequests.ReviewRequest;
import com.carenexus.ai.entity.AiDraft;
import com.carenexus.ai.entity.AiDraftSourceResource;
import com.carenexus.ai.mapper.AiDraftMapper;
import com.carenexus.ai.mapper.AiDraftSourceResourceMapper;
import com.carenexus.ai.service.AiQuestionDraftService;
import com.carenexus.ai.service.QuestionDraftPayload;
import com.carenexus.ai.service.TrainingAiSourceService;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.mapper.ExamQuestionMapper;
import com.carenexus.training.mapper.ExamQuestionOptionMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.resource.service.TrainingResourceQueryService;
import com.carenexus.training.vo.ResourceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiQuestionDraftServiceTest {
    private AiDraftMapper draftMapper;
    private AiDraftSourceResourceMapper sourceMapper;
    private ExamQuestionMapper questionMapper;
    private ExamQuestionOptionMapper optionMapper;
    private TrainingAiSourceService sourceService;
    private AiQuestionDraftService service;

    @BeforeEach
    void setUp() {
        TrainingResourceAccessPolicy accessPolicy = mock(TrainingResourceAccessPolicy.class);
        when(accessPolicy.requireManage()).thenReturn(currentUser());
        draftMapper = mock(AiDraftMapper.class);
        sourceMapper = mock(AiDraftSourceResourceMapper.class);
        questionMapper = mock(ExamQuestionMapper.class);
        optionMapper = mock(ExamQuestionOptionMapper.class);
        sourceService = mock(TrainingAiSourceService.class);
        when(draftMapper.update(any(), any())).thenReturn(1);
        service = new AiQuestionDraftService(accessPolicy, sourceService, new MockAiTrainingService(),
                draftMapper, sourceMapper, questionMapper, optionMapper,
                mock(OperationLogService.class), new ObjectMapper());
    }

    @Test
    void rejectsUnsupportedShortAnswerDraft() {
        QuestionDraftRequest request = request("SHORT_ANSWER");
        assertThrows(BusinessException.class, () -> service.generate(request));
    }

    @Test
    void approvedDraftCreatesDraftQuestionAndOptions() throws Exception {
        AiDraft draft = draft("DRAFT", "SINGLE_CHOICE");
        when(draftMapper.selectById(9L)).thenReturn(draft);
        when(sourceMapper.selectList(any())).thenReturn(Collections.singletonList(sourceRelation()));
        when(sourceService.load(any())).thenReturn(Collections.singletonList(source()));
        when(questionMapper.insert(any())).thenAnswer(invocation -> {
            com.carenexus.training.entity.ExamQuestion question = invocation.getArgument(0);
            question.setId(99L);
            return 1;
        });

        assertEquals(Long.valueOf(99L), service.review(9L, review("APPROVED")).questionId);
        verify(questionMapper).insert(any());
        verify(optionMapper, org.mockito.Mockito.times(4)).insert(any());
    }

    @Test
    void rejectedDraftDoesNotCreateQuestion() throws Exception {
        when(draftMapper.selectById(9L)).thenReturn(draft("DRAFT", "TRUE_FALSE"));
        when(sourceMapper.selectList(any())).thenReturn(Collections.singletonList(sourceRelation()));
        when(sourceService.load(any())).thenReturn(Collections.singletonList(source()));

        assertEquals("REJECTED", service.review(9L, review("REJECTED")).draftStatus);
        verify(questionMapper, never()).insert(any());
    }

    @Test
    void repeatedReviewReturnsConflict() throws Exception {
        when(draftMapper.selectById(9L)).thenReturn(draft("APPROVED", "TRUE_FALSE"));
        assertThrows(BusinessException.class, () -> service.review(9L, review("REJECTED")));
    }

    @Test
    void missingSourceResourceReturnsNotFound() {
        TrainingResourceQueryService queryService = mock(TrainingResourceQueryService.class);
        when(queryService.getResource(404L))
                .thenThrow(new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found"));
        TrainingAiSourceService loader = new TrainingAiSourceService(queryService);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> loader.load(Collections.singletonList(404L)));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void externalVideoUsesCourseSummaryAsReadableSource() {
        TrainingResourceQueryService queryService = mock(TrainingResourceQueryService.class);
        ResourceResponse resource = new ResourceResponse();
        resource.setId(2L);
        resource.setTitle("跌倒预防视频");
        resource.setStorageMode("EXTERNAL_LINK");
        resource.setSummary("介绍跌倒风险、环境整理和跌倒后应急处理原则。");
        when(queryService.getResource(2L)).thenReturn(resource);

        TrainingAiSource source = new TrainingAiSourceService(queryService)
                .load(Collections.singletonList(2L)).get(0);

        assertTrue(source.getContent().contains("跌倒预防视频"));
        assertTrue(source.getContent().contains("跌倒风险"));
    }

    private QuestionDraftRequest request(String type) {
        QuestionDraftRequest request = new QuestionDraftRequest();
        request.setSourceResourceIds(Collections.singletonList(1L));
        request.setQuestionType(type);
        request.setCount(1);
        return request;
    }

    private ReviewRequest review(String result) {
        ReviewRequest request = new ReviewRequest();
        request.setReviewResult(result);
        request.setComment("reviewed");
        return request;
    }

    private AiDraft draft(String status, String type) throws Exception {
        QuestionDraftPayload payload = new QuestionDraftPayload();
        payload.questionType = type;
        payload.questionContent = "测试题目";
        payload.standardAnswer = "SINGLE_CHOICE".equals(type) ? "A" : "TRUE";
        payload.analysis = "依据测试培训资料";
        if ("SINGLE_CHOICE".equals(type)) {
            payload.options.add(new QuestionDraftPayload.OptionPayload("A", "正确", true));
            payload.options.add(new QuestionDraftPayload.OptionPayload("B", "错误1", false));
            payload.options.add(new QuestionDraftPayload.OptionPayload("C", "错误2", false));
            payload.options.add(new QuestionDraftPayload.OptionPayload("D", "错误3", false));
        }
        AiDraft draft = new AiDraft();
        draft.setId(9L);
        draft.setDraftType("QUESTION");
        draft.setDraftStatus(status);
        draft.setDraftContent(new ObjectMapper().writeValueAsString(payload));
        return draft;
    }

    private AiDraftSourceResource sourceRelation() {
        AiDraftSourceResource relation = new AiDraftSourceResource();
        relation.setDraftId(9L);
        relation.setResourceId(1L);
        return relation;
    }

    private TrainingAiSource source() {
        return new TrainingAiSource(1L, "TEST护理资料", "护理操作应遵循培训资料中的规范流程。");
    }

    private CurrentUser currentUser() {
        return new CurrentUser(10L, "admin", "Admin", "ADMIN", "管理员",
                "NORMAL", Collections.singleton("training:resource:manage"));
    }
}
