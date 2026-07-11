package com.carenexus.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "care-nexus.ai.mode", havingValue = "mock", matchIfMissing = true)
public class MockAiTrainingService implements AiTrainingService {

    @Override
    public TrainingAiResult answerQuestion(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("根据《" + source.getTitle() + "》：" + excerpt(source.getContent())
                + "。此内容仅用于护理培训资料学习。", false);
    }

    @Override
    public TrainingAiResult summarize(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("《" + source.getTitle() + "》要点：" + excerpt(source.getContent())
                + "。请以已发布培训资料为准。", false);
    }

    @Override
    public TrainingAiResult suggestLearning(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("建议复习《" + source.getTitle() + "》，重点回顾："
                + excerpt(source.getContent()) + "。完成资料学习后再参加客观题考核。", false);
    }

    @Override
    public TrainingAiResult generateQuestionDraft(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        String content = "TRUE_FALSE".equals(request.getQuestionType())
                ? "判断：护理操作应遵循《" + source.getTitle() + "》中的培训要求。"
                : "根据《" + source.getTitle() + "》，以下哪项描述符合培训资料？";
        return new TrainingAiResult(content, true);
    }

    private TrainingAiSource firstSource(TrainingAiRequest request) {
        if (request == null || request.getSources() == null || request.getSources().isEmpty()) {
            throw new IllegalArgumentException("Validated training source is required");
        }
        return request.getSources().get(0);
    }

    private String excerpt(String content) {
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 120 ? normalized : normalized.substring(0, 120) + "...";
    }
}
