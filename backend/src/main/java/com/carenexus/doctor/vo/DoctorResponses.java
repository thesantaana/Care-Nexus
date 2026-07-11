package com.carenexus.doctor.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class DoctorResponses {

    private DoctorResponses() {
    }

    public static class AuthorizationResponse {
        public Long id;
        public Long doctorUserId;
        public Long elderId;
        public String authStatus;
        public Long authorizedBy;
        public LocalDateTime authorizedAt;
        public Long cancelledBy;
        public LocalDateTime cancelledAt;
    }

    public static class ElderProfileResponse {
        public Long id;
        public String elderName;
        public String gender;
        public LocalDate birthDate;
        public String mobileMasked;
        public String healthSummary;
        public HealthRecordResponse latestHealthRecord;
    }

    public static class HealthRecordResponse {
        public Long id;
        public Long elderId;
        public Long recorderId;
        public Integer systolicPressure;
        public Integer diastolicPressure;
        public BigDecimal bloodGlucose;
        public Integer heartRate;
        public BigDecimal bodyTemperature;
        public LocalDateTime recordTime;
        public String remark;
        public List<HealthAlertResponse> alerts = new ArrayList<>();
    }

    public static class HealthAlertResponse {
        public Long id;
        public Long elderId;
        public Long healthRecordId;
        public String alertSource;
        public String alertLevel;
        public String alertStatus;
        public String alertContent;
        public Long handledBy;
        public String handledComment;
        public LocalDateTime handledAt;
        public LocalDateTime createdAt;
    }

    public static class FollowUpResponse {
        public Long id;
        public Long elderId;
        public Long doctorId;
        public String method;
        public String result;
        public String recordStatus;
        public LocalDateTime confirmedAt;
    }

    public static class InterventionResponse {
        public Long id;
        public Long elderId;
        public Long doctorId;
        public String content;
        public String recordStatus;
        public LocalDateTime confirmedAt;
    }

    public static class AssessmentResponse {
        public Long id;
        public Long elderId;
        public Long doctorId;
        public String riskLevel;
        public String conclusion;
        public String suggestion;
        public String assessmentStatus;
        public LocalDateTime confirmedAt;
    }
}
