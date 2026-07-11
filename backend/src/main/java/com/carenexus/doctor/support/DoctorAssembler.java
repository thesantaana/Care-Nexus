package com.carenexus.doctor.support;

import com.carenexus.doctor.entity.DoctorElderAuthorization;
import com.carenexus.doctor.entity.ElderHealthProfile;
import com.carenexus.doctor.entity.FollowUpRecord;
import com.carenexus.doctor.entity.HealthAlert;
import com.carenexus.doctor.entity.HealthAssessment;
import com.carenexus.doctor.entity.HealthRecord;
import com.carenexus.doctor.entity.InterventionRecord;
import com.carenexus.doctor.vo.DoctorResponses;
import org.springframework.stereotype.Component;

@Component
public class DoctorAssembler {

    public DoctorResponses.AuthorizationResponse toAuthorization(DoctorElderAuthorization source) {
        DoctorResponses.AuthorizationResponse target = new DoctorResponses.AuthorizationResponse();
        target.id = source.getId();
        target.doctorUserId = source.getDoctorUserId();
        target.elderId = source.getElderId();
        target.authStatus = source.getAuthStatus();
        target.authorizedBy = source.getAuthorizedBy();
        target.authorizedAt = source.getAuthorizedAt();
        target.cancelledBy = source.getCancelledBy();
        target.cancelledAt = source.getCancelledAt();
        return target;
    }

    public DoctorResponses.ElderProfileResponse toElder(ElderHealthProfile source) {
        DoctorResponses.ElderProfileResponse target = new DoctorResponses.ElderProfileResponse();
        target.id = source.getId();
        target.elderName = source.getElderName();
        target.gender = source.getGender();
        target.birthDate = source.getBirthDate();
        target.mobileMasked = source.getMobileLast4() == null ? null : "*******" + source.getMobileLast4();
        target.healthSummary = source.getHealthSummary();
        return target;
    }

    public DoctorResponses.HealthRecordResponse toRecord(HealthRecord source) {
        DoctorResponses.HealthRecordResponse target = new DoctorResponses.HealthRecordResponse();
        target.id = source.getId();
        target.elderId = source.getElderId();
        target.recorderId = source.getRecorderId();
        target.systolicPressure = source.getSystolicPressure();
        target.diastolicPressure = source.getDiastolicPressure();
        target.bloodGlucose = source.getBloodGlucose();
        target.heartRate = source.getHeartRate();
        target.bodyTemperature = source.getBodyTemperature();
        target.recordTime = source.getRecordTime();
        target.remark = source.getRemark();
        return target;
    }

    public DoctorResponses.HealthAlertResponse toAlert(HealthAlert source) {
        DoctorResponses.HealthAlertResponse target = new DoctorResponses.HealthAlertResponse();
        target.id = source.getId();
        target.elderId = source.getElderId();
        target.healthRecordId = source.getHealthRecordId();
        target.alertSource = source.getAlertSource();
        target.alertLevel = source.getAlertLevel();
        target.alertStatus = source.getAlertStatus();
        target.alertContent = source.getAlertContent();
        target.handledBy = source.getHandledBy();
        target.handledComment = source.getHandledComment();
        target.handledAt = source.getHandledAt();
        target.createdAt = source.getCreatedAt();
        return target;
    }

    public DoctorResponses.FollowUpResponse toFollowUp(FollowUpRecord source) {
        DoctorResponses.FollowUpResponse target = new DoctorResponses.FollowUpResponse();
        target.id = source.getId();
        target.elderId = source.getElderId();
        target.doctorId = source.getDoctorId();
        target.method = source.getFollowUpMethod();
        target.result = source.getFollowUpResult();
        target.recordStatus = source.getRecordStatus();
        target.confirmedAt = source.getConfirmedAt();
        return target;
    }

    public DoctorResponses.InterventionResponse toIntervention(InterventionRecord source) {
        DoctorResponses.InterventionResponse target = new DoctorResponses.InterventionResponse();
        target.id = source.getId();
        target.elderId = source.getElderId();
        target.doctorId = source.getDoctorId();
        target.content = source.getInterventionContent();
        target.recordStatus = source.getRecordStatus();
        target.confirmedAt = source.getConfirmedAt();
        return target;
    }

    public DoctorResponses.AssessmentResponse toAssessment(HealthAssessment source) {
        DoctorResponses.AssessmentResponse target = new DoctorResponses.AssessmentResponse();
        target.id = source.getId();
        target.elderId = source.getElderId();
        target.doctorId = source.getDoctorId();
        target.riskLevel = source.getRiskLevel();
        target.conclusion = source.getConclusion();
        target.suggestion = source.getSuggestion();
        target.assessmentStatus = source.getAssessmentStatus();
        target.confirmedAt = source.getConfirmedAt();
        return target;
    }
}
