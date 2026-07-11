package com.carenexus.doctor.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
import com.carenexus.doctor.dto.DoctorRequests.AlertCreateRequest;
import com.carenexus.doctor.dto.DoctorRequests.AlertStatusRequest;
import com.carenexus.doctor.dto.DoctorRequests.AssessmentRequest;
import com.carenexus.doctor.dto.DoctorRequests.FollowUpRequest;
import com.carenexus.doctor.dto.DoctorRequests.HealthRecordRequest;
import com.carenexus.doctor.dto.DoctorRequests.InterventionRequest;
import com.carenexus.doctor.health.HealthAlertService;
import com.carenexus.doctor.health.HealthCareRecordService;
import com.carenexus.doctor.health.HealthRecordService;
import com.carenexus.doctor.service.DoctorElderService;
import com.carenexus.doctor.vo.DoctorResponses;
import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorHealthController {
    private final DoctorElderService elderService;
    private final HealthRecordService healthRecordService;
    private final HealthAlertService alertService;
    private final HealthCareRecordService careRecordService;

    public DoctorHealthController(DoctorElderService elderService, HealthRecordService healthRecordService,
            HealthAlertService alertService, HealthCareRecordService careRecordService) {
        this.elderService = elderService;
        this.healthRecordService = healthRecordService;
        this.alertService = alertService;
        this.careRecordService = careRecordService;
    }

    @GetMapping("/elders")
    public ApiResponse<PageResponse<DoctorResponses.ElderProfileResponse>> elders(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(elderService.myElders(keyword, pageNo, pageSize));
    }

    @GetMapping("/elders/{elderId}")
    public ApiResponse<DoctorResponses.ElderProfileResponse> elder(@PathVariable Long elderId) {
        return ApiResponse.success(elderService.detail(elderId));
    }

    @GetMapping("/elders/{elderId}/health-records")
    public ApiResponse<PageResponse<DoctorResponses.HealthRecordResponse>> healthRecords(
            @PathVariable Long elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime endTime,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(healthRecordService.page(elderId, startTime, endTime, pageNo, pageSize));
    }

    @PostMapping("/elders/{elderId}/health-records")
    public ApiResponse<DoctorResponses.HealthRecordResponse> createHealthRecord(@PathVariable Long elderId,
            @Valid @RequestBody HealthRecordRequest request) {
        return ApiResponse.success(healthRecordService.create(elderId, request));
    }

    @GetMapping("/elders/{elderId}/alerts")
    public ApiResponse<PageResponse<DoctorResponses.HealthAlertResponse>> alerts(@PathVariable Long elderId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(alertService.page(elderId, status, pageNo, pageSize));
    }

    @PostMapping("/elders/{elderId}/alerts")
    public ApiResponse<DoctorResponses.HealthAlertResponse> createAlert(@PathVariable Long elderId,
            @Valid @RequestBody AlertCreateRequest request) {
        return ApiResponse.success(alertService.createManual(elderId, request));
    }

    @PutMapping("/alerts/{id}/status")
    public ApiResponse<DoctorResponses.HealthAlertResponse> updateAlert(@PathVariable Long id,
            @Valid @RequestBody AlertStatusRequest request) {
        return ApiResponse.success(alertService.updateStatus(id, request));
    }

    @PostMapping("/elders/{elderId}/follow-ups")
    public ApiResponse<DoctorResponses.FollowUpResponse> followUp(@PathVariable Long elderId,
            @Valid @RequestBody FollowUpRequest request) {
        return ApiResponse.success(careRecordService.createFollowUp(elderId, request));
    }

    @PutMapping("/follow-ups/{id}/confirm")
    public ApiResponse<DoctorResponses.FollowUpResponse> confirmFollowUp(@PathVariable Long id) {
        return ApiResponse.success(careRecordService.confirmFollowUp(id));
    }

    @PostMapping("/elders/{elderId}/interventions")
    public ApiResponse<DoctorResponses.InterventionResponse> intervention(@PathVariable Long elderId,
            @Valid @RequestBody InterventionRequest request) {
        return ApiResponse.success(careRecordService.createIntervention(elderId, request));
    }

    @PutMapping("/interventions/{id}/confirm")
    public ApiResponse<DoctorResponses.InterventionResponse> confirmIntervention(@PathVariable Long id) {
        return ApiResponse.success(careRecordService.confirmIntervention(id));
    }

    @PostMapping("/elders/{elderId}/assessments")
    public ApiResponse<DoctorResponses.AssessmentResponse> assessment(@PathVariable Long elderId,
            @Valid @RequestBody AssessmentRequest request) {
        return ApiResponse.success(careRecordService.createAssessment(elderId, request));
    }

    @PutMapping("/assessments/{id}/confirm")
    public ApiResponse<DoctorResponses.AssessmentResponse> confirmAssessment(@PathVariable Long id) {
        return ApiResponse.success(careRecordService.confirmAssessment(id));
    }
}
