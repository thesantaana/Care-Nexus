package com.carenexus.doctor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

public final class DoctorRequests {

    private DoctorRequests() {
    }

    public static class AuthorizationRequest {
        @NotNull private Long doctorUserId;
        @NotNull private Long elderId;
        @NotBlank private String authStatus;

        public Long getDoctorUserId() { return doctorUserId; }

        public void setDoctorUserId(Long value) { this.doctorUserId = value; }

        public Long getElderId() { return elderId; }

        public void setElderId(Long value) { this.elderId = value; }

        public String getAuthStatus() { return authStatus; }

        public void setAuthStatus(String value) { this.authStatus = value; }
    }

    public static class HealthRecordRequest {
        @Min(40) @Max(260) private Integer systolicPressure;
        @Min(30) @Max(180) private Integer diastolicPressure;
        @DecimalMin("1.0") @DecimalMax("40.0") private BigDecimal bloodGlucose;
        @Min(20) @Max(250) private Integer heartRate;
        @DecimalMin("30.0") @DecimalMax("45.0") private BigDecimal bodyTemperature;
        @NotNull @PastOrPresent private LocalDateTime recordTime;
        @Size(max = 500) private String remark;

        public Integer getSystolicPressure() { return systolicPressure; }

        public void setSystolicPressure(Integer value) { this.systolicPressure = value; }

        public Integer getDiastolicPressure() { return diastolicPressure; }

        public void setDiastolicPressure(Integer value) { this.diastolicPressure = value; }

        public BigDecimal getBloodGlucose() { return bloodGlucose; }

        public void setBloodGlucose(BigDecimal value) { this.bloodGlucose = value; }

        public Integer getHeartRate() { return heartRate; }

        public void setHeartRate(Integer value) { this.heartRate = value; }

        public BigDecimal getBodyTemperature() { return bodyTemperature; }

        public void setBodyTemperature(BigDecimal value) { this.bodyTemperature = value; }

        public LocalDateTime getRecordTime() { return recordTime; }

        public void setRecordTime(LocalDateTime value) { this.recordTime = value; }

        public String getRemark() { return remark; }

        public void setRemark(String value) { this.remark = value; }
    }

    public static class AlertCreateRequest {
        @NotBlank @Size(max = 500) private String alertContent;
        @Size(max = 32) private String alertLevel;

        public String getAlertContent() { return alertContent; }

        public void setAlertContent(String value) { this.alertContent = value; }

        public String getAlertLevel() { return alertLevel; }

        public void setAlertLevel(String value) { this.alertLevel = value; }
    }

    public static class AlertStatusRequest {
        @NotBlank private String alertStatus;
        @NotBlank @Size(max = 500) private String comment;

        public String getAlertStatus() { return alertStatus; }

        public void setAlertStatus(String value) { this.alertStatus = value; }

        public String getComment() { return comment; }

        public void setComment(String value) { this.comment = value; }
    }

    public static class FollowUpRequest {
        @NotBlank @Size(max = 32) private String method;
        @Size(max = 1000) private String result;
        @NotBlank private String recordStatus;

        public String getMethod() { return method; }

        public void setMethod(String value) { this.method = value; }

        public String getResult() { return result; }

        public void setResult(String value) { this.result = value; }

        public String getRecordStatus() { return recordStatus; }

        public void setRecordStatus(String value) { this.recordStatus = value; }
    }

    public static class InterventionRequest {
        @NotBlank @Size(max = 1000) private String content;
        @NotBlank private String recordStatus;

        public String getContent() { return content; }

        public void setContent(String value) { this.content = value; }

        public String getRecordStatus() { return recordStatus; }

        public void setRecordStatus(String value) { this.recordStatus = value; }
    }

    public static class AssessmentRequest {
        @NotBlank @Size(max = 32) private String riskLevel;
        @Size(max = 1000) private String conclusion;
        @Size(max = 1000) private String suggestion;
        @NotBlank private String assessmentStatus;

        public String getRiskLevel() { return riskLevel; }

        public void setRiskLevel(String value) { this.riskLevel = value; }

        public String getConclusion() { return conclusion; }

        public void setConclusion(String value) { this.conclusion = value; }

        public String getSuggestion() { return suggestion; }

        public void setSuggestion(String value) { this.suggestion = value; }

        public String getAssessmentStatus() { return assessmentStatus; }

        public void setAssessmentStatus(String value) { this.assessmentStatus = value; }
    }
}
