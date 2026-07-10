package com.carenexus.training.vo;

import java.time.LocalDateTime;

public class LearningAccessResponse {

    private Long resourceId;
    private Integer accessSeconds;
    private Integer totalLearningSeconds;
    private String trainingStatus;
    private LocalDateTime latestLearningTime;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAccessSeconds() {
        return accessSeconds;
    }

    public void setAccessSeconds(Integer accessSeconds) {
        this.accessSeconds = accessSeconds;
    }

    public Integer getTotalLearningSeconds() {
        return totalLearningSeconds;
    }

    public void setTotalLearningSeconds(Integer totalLearningSeconds) {
        this.totalLearningSeconds = totalLearningSeconds;
    }

    public String getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(String trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    public LocalDateTime getLatestLearningTime() {
        return latestLearningTime;
    }

    public void setLatestLearningTime(LocalDateTime latestLearningTime) {
        this.latestLearningTime = latestLearningTime;
    }
}
