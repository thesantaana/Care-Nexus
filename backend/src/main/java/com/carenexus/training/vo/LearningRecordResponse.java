package com.carenexus.training.vo;

import java.time.LocalDateTime;

public class LearningRecordResponse {

    private Long userId;
    private String trainingScope;
    private Integer totalLearningSeconds;
    private LocalDateTime latestLearningTime;
    private String trainingStatus;
    private Integer visitedResourceCount;
    private Integer requiredResourceCount;
    private Integer requiredLearningSeconds;
    private Boolean examAllowed;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTrainingScope() {
        return trainingScope;
    }

    public void setTrainingScope(String trainingScope) {
        this.trainingScope = trainingScope;
    }

    public Integer getTotalLearningSeconds() {
        return totalLearningSeconds;
    }

    public void setTotalLearningSeconds(Integer totalLearningSeconds) {
        this.totalLearningSeconds = totalLearningSeconds;
    }

    public LocalDateTime getLatestLearningTime() {
        return latestLearningTime;
    }

    public void setLatestLearningTime(LocalDateTime latestLearningTime) {
        this.latestLearningTime = latestLearningTime;
    }

    public String getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(String trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    public Integer getVisitedResourceCount() {
        return visitedResourceCount;
    }

    public void setVisitedResourceCount(Integer visitedResourceCount) {
        this.visitedResourceCount = visitedResourceCount;
    }

    public Integer getRequiredResourceCount() {
        return requiredResourceCount;
    }

    public void setRequiredResourceCount(Integer requiredResourceCount) {
        this.requiredResourceCount = requiredResourceCount;
    }

    public Integer getRequiredLearningSeconds() {
        return requiredLearningSeconds;
    }

    public void setRequiredLearningSeconds(Integer requiredLearningSeconds) {
        this.requiredLearningSeconds = requiredLearningSeconds;
    }

    public Boolean getExamAllowed() {
        return examAllowed;
    }

    public void setExamAllowed(Boolean examAllowed) {
        this.examAllowed = examAllowed;
    }
}
