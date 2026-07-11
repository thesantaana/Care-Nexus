package com.carenexus.ai.service;

import com.carenexus.ai.AiTrainingService;
import com.carenexus.ai.TrainingAiRequest;
import com.carenexus.ai.TrainingAiResult;
import com.carenexus.ai.TrainingAiSource;
import com.carenexus.ai.dto.TrainingAiRequests.ContentRequest;
import com.carenexus.ai.vo.TrainingAiResponses;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.exam.service.TrainingLearningService;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.LearningRecordResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TrainingAiAssistanceService {
    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingAiSourceService sourceService;
    private final AiTrainingService aiTrainingService;
    private final TrainingLearningService learningService;

    public TrainingAiAssistanceService(TrainingResourceAccessPolicy accessPolicy,
            TrainingAiSourceService sourceService, AiTrainingService aiTrainingService,
            TrainingLearningService learningService) {
        this.accessPolicy = accessPolicy;
        this.sourceService = sourceService;
        this.aiTrainingService = aiTrainingService;
        this.learningService = learningService;
    }

    public TrainingAiResponses.ContentResponse answer(ContentRequest request) {
        accessPolicy.requireViewOrManage();
        if (!StringUtils.hasText(request.getQuestion())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Question is required");
        }
        TrainingAiRequest aiRequest = request(request.getSourceResourceIds(), request.getQuestion());
        return response(aiTrainingService.answerQuestion(aiRequest), aiRequest.getSources());
    }

    public TrainingAiResponses.ContentResponse summarize(ContentRequest request) {
        accessPolicy.requireViewOrManage();
        TrainingAiRequest aiRequest = request(request.getSourceResourceIds(), null);
        return response(aiTrainingService.summarize(aiRequest), aiRequest.getSources());
    }

    public TrainingAiResponses.ContentResponse suggestions(ContentRequest request) {
        accessPolicy.requireViewOrManage();
        LearningRecordResponse learning = learningService.myLearningRecord();
        String context = "学习状态=" + learning.getTrainingStatus()
                + ",累计学习秒数=" + learning.getTotalLearningSeconds();
        TrainingAiRequest aiRequest = request(request.getSourceResourceIds(), context);
        return response(aiTrainingService.suggestLearning(aiRequest), aiRequest.getSources());
    }

    private TrainingAiRequest request(List<Long> sourceIds, String prompt) {
        List<TrainingAiSource> sources = sourceService.load(sourceIds);
        TrainingAiRequest request = new TrainingAiRequest();
        request.setSourceResourceIds(sources.stream().map(TrainingAiSource::getResourceId)
                .collect(Collectors.toList()));
        request.setSources(sources);
        request.setPrompt(prompt);
        return request;
    }

    private TrainingAiResponses.ContentResponse response(TrainingAiResult result,
            List<TrainingAiSource> sources) {
        TrainingAiResponses.ContentResponse response = new TrainingAiResponses.ContentResponse();
        response.content = result.getContent();
        response.references = sources.stream().map(this::reference).collect(Collectors.toList());
        return response;
    }

    private TrainingAiResponses.Reference reference(TrainingAiSource source) {
        TrainingAiResponses.Reference reference = new TrainingAiResponses.Reference();
        reference.resourceId = source.getResourceId();
        reference.title = source.getTitle();
        return reference;
    }
}
