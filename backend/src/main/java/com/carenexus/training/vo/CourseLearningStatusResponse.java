package com.carenexus.training.vo;

public class CourseLearningStatusResponse {

    private Long resourceId;
    private Integer learnedSeconds;
    private Integer requiredSeconds;
    private Boolean completed;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getLearnedSeconds() {
        return learnedSeconds;
    }

    public void setLearnedSeconds(Integer learnedSeconds) {
        this.learnedSeconds = learnedSeconds;
    }

    public Integer getRequiredSeconds() {
        return requiredSeconds;
    }

    public void setRequiredSeconds(Integer requiredSeconds) {
        this.requiredSeconds = requiredSeconds;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
