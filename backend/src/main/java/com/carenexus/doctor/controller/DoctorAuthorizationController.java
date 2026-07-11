package com.carenexus.doctor.controller;

import com.carenexus.auth.CurrentUser;
import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
import com.carenexus.doctor.dto.DoctorRequests.AuthorizationRequest;
import com.carenexus.doctor.service.DoctorAuthorizationServiceImpl;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.vo.DoctorResponses;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/doctor/authorizations")
public class DoctorAuthorizationController {
    private final DoctorAuthorizationServiceImpl authorizationService;
    private final DoctorAccessPolicy accessPolicy;

    public DoctorAuthorizationController(DoctorAuthorizationServiceImpl authorizationService,
            DoctorAccessPolicy accessPolicy) {
        this.authorizationService = authorizationService;
        this.accessPolicy = accessPolicy;
    }

    @PutMapping
    public ApiResponse<DoctorResponses.AuthorizationResponse> maintain(
            @Valid @RequestBody AuthorizationRequest request) {
        CurrentUser operator = accessPolicy.requireAuthorize();
        return ApiResponse.success(authorizationService.maintain(operator, request));
    }

    @GetMapping("/doctors/{doctorUserId}/elders")
    public ApiResponse<PageResponse<DoctorResponses.ElderProfileResponse>> doctorElders(
            @PathVariable Long doctorUserId, @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        CurrentUser requester = accessPolicy.requireView();
        return ApiResponse.success(authorizationService.doctorElders(
                requester, doctorUserId, keyword, pageNo, pageSize));
    }

    @GetMapping("/elders/{elderId}/doctors")
    public ApiResponse<PageResponse<DoctorResponses.AuthorizationResponse>> elderDoctors(
            @PathVariable Long elderId,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        accessPolicy.requireAuthorize();
        return ApiResponse.success(authorizationService.elderDoctors(elderId, pageNo, pageSize));
    }
}
