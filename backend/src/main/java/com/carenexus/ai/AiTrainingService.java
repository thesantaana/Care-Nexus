package com.carenexus.ai;

public interface AiTrainingService {

    TrainingAiResult answerQuestion(TrainingAiRequest request);

    TrainingAiResult summarize(TrainingAiRequest request);

    TrainingAiResult suggestLearning(TrainingAiRequest request);

    TrainingAiResult generateQuestionDraft(TrainingAiRequest request);
}
