package com.carenexus.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "care-nexus.ai.mode", havingValue = "mock", matchIfMissing = true)
public class MockAiTrainingService implements AiTrainingService {

    @Override
    public TrainingAiResult answerQuestion(TrainingAiRequest request) {
        return new TrainingAiResult("Mock AI answer based on selected training resource.", false);
    }

    @Override
    public TrainingAiResult summarize(TrainingAiRequest request) {
        return new TrainingAiResult("Mock AI summary based on selected training resource.", false);
    }

    @Override
    public TrainingAiResult suggestLearning(TrainingAiRequest request) {
        return new TrainingAiResult("Mock AI learning suggestion based on selected training resource.", false);
    }

    @Override
    public TrainingAiResult generateQuestionDraft(TrainingAiRequest request) {
        return new TrainingAiResult("Mock AI question draft. It must be reviewed before publishing.", true);
    }
}
