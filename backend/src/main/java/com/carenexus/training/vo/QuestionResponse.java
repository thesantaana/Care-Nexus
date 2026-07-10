package com.carenexus.training.vo;

import java.util.ArrayList;
import java.util.List;

public class QuestionResponse {

    private Long id;
    private Long resourceId;
    private String questionType;
    private String questionContent;
    private String standardAnswer;
    private String analysis;
    private String status;
    private List<QuestionOptionResponse> options = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<QuestionOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionResponse> options) {
        this.options = options;
    }
}
