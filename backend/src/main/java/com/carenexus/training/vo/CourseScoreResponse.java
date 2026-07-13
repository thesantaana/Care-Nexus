package com.carenexus.training.vo;

import java.math.BigDecimal;

public class CourseScoreResponse {

    private Long resourceId;
    private String resourceTitle;
    private Long examId;
    private BigDecimal passScore;
    private BigDecimal bestScore;
    private Boolean passed;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public BigDecimal getPassScore() {
        return passScore;
    }

    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }

    public BigDecimal getBestScore() {
        return bestScore;
    }

    public void setBestScore(BigDecimal bestScore) {
        this.bestScore = bestScore;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }
}
