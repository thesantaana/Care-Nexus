package com.carenexus.care.controller;

import com.carenexus.care.catalog.service.CareServiceItemService;
import com.carenexus.care.dto.ServiceItemRequest;
import com.carenexus.care.dto.ServiceStatusRequest;
import com.carenexus.care.vo.ServiceItemResponse;
import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@RequestMapping("/api/v1/admin/service-items")
public class AdminCareController {

    private final CareServiceItemService serviceItemService;

    public AdminCareController(CareServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ServiceItemResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(serviceItemService.pageAdmin(keyword, category, status, pageNo, pageSize));
    }

    @PostMapping
    public ApiResponse<ServiceItemResponse> create(@Valid @RequestBody ServiceItemRequest request) {
        return ApiResponse.success(serviceItemService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceItemResponse> update(@PathVariable Long id,
            @Valid @RequestBody ServiceItemRequest request) {
        return ApiResponse.success(serviceItemService.update(id, request));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ServiceItemResponse> updateStatus(@PathVariable Long id,
            @Valid @RequestBody ServiceStatusRequest request) {
        return ApiResponse.success(serviceItemService.updateStatus(id, request));
    }
}
