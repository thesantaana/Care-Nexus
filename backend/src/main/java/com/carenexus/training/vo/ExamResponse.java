package com.carenexus.training.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExamResponse {

    private Long id;
    private Long resourceId;
    private String examName;
    private BigDecimal passScore;
    private Integer maxAttempts;
    private String status;
    private List<ExamQuestionResponse> questions = new ArrayList<>();

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

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public BigDecimal getPassScore() {
        return passScore;
    }

    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExamQuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ExamQuestionResponse> questions) {
        this.questions = questions;
    }
}
