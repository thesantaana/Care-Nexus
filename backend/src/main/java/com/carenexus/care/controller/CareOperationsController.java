package com.carenexus.care.controller;

import com.carenexus.care.dto.AssignOrderRequest;
import com.carenexus.care.dto.CompleteOrderRequest;
import com.carenexus.care.dto.HandleComplaintRequest;
import com.carenexus.care.order.service.CareFeedbackService;
import com.carenexus.care.order.service.CareOrderCommandService;
import com.carenexus.care.order.service.CareOrderQueryService;
import com.carenexus.care.vo.CareOrderResponse;
import com.carenexus.care.vo.ComplaintResponse;
import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
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
@RequestMapping("/api/v1/care")
public class CareOperationsController {

    private final CareOrderQueryService queryService;
    private final CareOrderCommandService commandService;
    private final CareFeedbackService feedbackService;

    public CareOperationsController(CareOrderQueryService queryService, CareOrderCommandService commandService,
            CareFeedbackService feedbackService) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/orders/pending-assignment")
    public ApiResponse<PageResponse<CareOrderResponse>> pendingAssignment(
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(queryService.pendingAssignment(pageNo, pageSize));
    }

    @PutMapping("/orders/{id}/assign")
    public ApiResponse<CareOrderResponse> assign(@PathVariable Long id,
            @Valid @RequestBody AssignOrderRequest request) {
        return ApiResponse.success(commandService.assign(id, request));
    }

    @GetMapping("/my-orders")
    public ApiResponse<PageResponse<CareOrderResponse>> myOrders(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(queryService.caregiverOrders(status, pageNo, pageSize));
    }

    @GetMapping("/my-orders/{id}")
    public ApiResponse<CareOrderResponse> myOrder(@PathVariable Long id) {
        return ApiResponse.success(queryService.caregiverDetail(id));
    }

    @PutMapping("/orders/{id}/confirm")
    public ApiResponse<CareOrderResponse> confirm(@PathVariable Long id) {
        return ApiResponse.success(commandService.confirm(id));
    }

    @PutMapping("/orders/{id}/start")
    public ApiResponse<CareOrderResponse> start(@PathVariable Long id) {
        return ApiResponse.success(commandService.start(id));
    }

    @PutMapping("/orders/{id}/complete")
    public ApiResponse<CareOrderResponse> complete(@PathVariable Long id,
            @Valid @RequestBody CompleteOrderRequest request) {
        return ApiResponse.success(commandService.complete(id, request));
    }

    @GetMapping("/complaints")
    public ApiResponse<PageResponse<ComplaintResponse>> complaints(
            @RequestParam(required = false) String complaintStatus,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(feedbackService.pageComplaints(complaintStatus, pageNo, pageSize));
    }

    @GetMapping("/complaints/{id}")
    public ApiResponse<ComplaintResponse> complaint(@PathVariable Long id) {
        return ApiResponse.success(feedbackService.complaintDetail(id));
    }

    @PutMapping("/complaints/{id}/handle")
    public ApiResponse<ComplaintResponse> handleComplaint(@PathVariable Long id,
            @Valid @RequestBody HandleComplaintRequest request) {
        return ApiResponse.success(feedbackService.handle(id, request));
    }
}
