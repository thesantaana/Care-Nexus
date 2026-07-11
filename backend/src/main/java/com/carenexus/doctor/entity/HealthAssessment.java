package com.carenexus.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("health_assessment")
public class HealthAssessment {
    private Long id;
    private Long elderId;
    private Long doctorId;
    private String riskLevel;
    private String conclusion;
    private String suggestion;
    private String assessmentStatus;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getElderId() { return elderId; }

    public void setElderId(Long value) { this.elderId = value; }

    public Long getDoctorId() { return doctorId; }

    public void setDoctorId(Long value) { this.doctorId = value; }

    public String getRiskLevel() { return riskLevel; }

    public void setRiskLevel(String value) { this.riskLevel = value; }

    public String getConclusion() { return conclusion; }

    public void setConclusion(String value) { this.conclusion = value; }

    public String getSuggestion() { return suggestion; }

    public void setSuggestion(String value) { this.suggestion = value; }

    public String getAssessmentStatus() { return assessmentStatus; }

    public void setAssessmentStatus(String value) { this.assessmentStatus = value; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }

    public void setConfirmedAt(LocalDateTime value) { this.confirmedAt = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime value) { this.updatedAt = value; }
}
