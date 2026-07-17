package com.carenexus.ai;

import java.util.ArrayList;
import java.util.List;

public class TrainingAiRequest {

    private List<Long> sourceResourceIds = new ArrayList<>();
    private List<TrainingAiSource> sources = new ArrayList<>();
    private String prompt;
    private String questionType;
    private String userAnswer;
    private String standardAnswer;

    public List<Long> getSourceResourceIds() {
        return sourceResourceIds;
    }

    public void setSourceResourceIds(List<Long> sourceResourceIds) {
        this.sourceResourceIds = sourceResourceIds;
    }

    public List<TrainingAiSource> getSources() {
        return sources;
    }

    public void setSources(List<TrainingAiSource> sources) {
        this.sources = sources;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }
}
