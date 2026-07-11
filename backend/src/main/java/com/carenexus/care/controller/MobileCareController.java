package com.carenexus.care.controller;

import com.carenexus.care.address.service.CareAddressService;
import com.carenexus.care.binding.service.FamilyBindingService;
import com.carenexus.care.catalog.service.CareServiceItemService;
import com.carenexus.care.dto.AddressDefaultRequest;
import com.carenexus.care.dto.AddressRequest;
import com.carenexus.care.dto.AddressUpdateRequest;
import com.carenexus.care.dto.BindingRequest;
import com.carenexus.care.dto.ComplaintRequest;
import com.carenexus.care.dto.EvaluationRequest;
import com.carenexus.care.dto.OrderCreateRequest;
import com.carenexus.care.dto.ReasonRequest;
import com.carenexus.care.order.service.CareFeedbackService;
import com.carenexus.care.order.service.CareOrderCommandService;
import com.carenexus.care.order.service.CareOrderQueryService;
import com.carenexus.care.vo.AddressResponse;
import com.carenexus.care.vo.BindingResponse;
import com.carenexus.care.vo.CareOrderResponse;
import com.carenexus.care.vo.ComplaintResponse;
import com.carenexus.care.vo.ElderResponse;
import com.carenexus.care.vo.EvaluationResponse;
import com.carenexus.care.vo.ServiceItemResponse;
import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
import java.util.List;
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
@RequestMapping("/api/v1/mobile")
public class MobileCareController {

    private final FamilyBindingService bindingService;
    private final CareServiceItemService serviceItemService;
    private final CareAddressService addressService;
    private final CareOrderCommandService orderCommandService;
    private final CareOrderQueryService orderQueryService;
    private final CareFeedbackService feedbackService;

    public MobileCareController(FamilyBindingService bindingService, CareServiceItemService serviceItemService,
            CareAddressService addressService, CareOrderCommandService orderCommandService,
            CareOrderQueryService orderQueryService, CareFeedbackService feedbackService) {
        this.bindingService = bindingService;
        this.serviceItemService = serviceItemService;
        this.addressService = addressService;
        this.orderCommandService = orderCommandService;
        this.orderQueryService = orderQueryService;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/elder-bindings")
    public ApiResponse<BindingResponse> bind(@Valid @RequestBody BindingRequest request) {
        return ApiResponse.success(bindingService.bind(request));
    }

    @PutMapping("/elder-bindings/{id}/cancel")
    public ApiResponse<BindingResponse> cancelBinding(@PathVariable Long id,
            @Valid @RequestBody ReasonRequest request) {
        return ApiResponse.success(bindingService.cancel(id, request));
    }

    @GetMapping("/elders")
    public ApiResponse<List<ElderResponse>> myElders() {
        return ApiResponse.success(bindingService.myElders());
    }

    @GetMapping("/service-items")
    public ApiResponse<PageResponse<ServiceItemResponse>> serviceItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(serviceItemService.pageMobile(keyword, category, pageNo, pageSize));
    }

    @GetMapping("/service-items/{id}")
    public ApiResponse<ServiceItemResponse> serviceItem(@PathVariable Long id) {
        return ApiResponse.success(serviceItemService.mobileDetail(id));
    }

    @GetMapping("/addresses")
    public ApiResponse<List<AddressResponse>> addresses(@RequestParam Long elderId) {
        return ApiResponse.success(addressService.list(elderId));
    }

    @PostMapping("/addresses")
    public ApiResponse<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.create(request));
    }

    @PutMapping("/addresses/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable Long id,
            @Valid @RequestBody AddressUpdateRequest request) {
        return ApiResponse.success(addressService.update(id, request));
    }

    @PutMapping("/addresses/{id}/disable")
    public ApiResponse<AddressResponse> disableAddress(@PathVariable Long id,
            @Valid @RequestBody ReasonRequest request) {
        return ApiResponse.success(addressService.disable(id, request));
    }

    @PutMapping("/addresses/{id}/default")
    public ApiResponse<AddressResponse> defaultAddress(@PathVariable Long id,
            @Valid @RequestBody AddressDefaultRequest request) {
        return ApiResponse.success(addressService.setDefault(id, request));
    }

    @PostMapping("/orders")
    public ApiResponse<CareOrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success(orderCommandService.create(request));
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<CareOrderResponse>> orders(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(orderQueryService.mobileOrders(status, pageNo, pageSize));
    }

    @GetMapping("/orders/{id}")
    public ApiResponse<CareOrderResponse> order(@PathVariable Long id) {
        return ApiResponse.success(orderQueryService.mobileDetail(id));
    }

    @PutMapping("/orders/{id}/cancel")
    public ApiResponse<CareOrderResponse> cancelOrder(@PathVariable Long id,
            @Valid @RequestBody ReasonRequest request) {
        return ApiResponse.success(orderCommandService.cancel(id, request));
    }

    @PostMapping("/orders/{id}/evaluation")
    public ApiResponse<EvaluationResponse> evaluate(@PathVariable Long id,
            @Valid @RequestBody EvaluationRequest request) {
        return ApiResponse.success(feedbackService.evaluate(id, request));
    }

    @PostMapping("/orders/{id}/complaints")
    public ApiResponse<ComplaintResponse> complain(@PathVariable Long id,
            @Valid @RequestBody ComplaintRequest request) {
        return ApiResponse.success(feedbackService.complain(id, request));
    }

    @GetMapping("/complaints/{id}")
    public ApiResponse<ComplaintResponse> complaint(@PathVariable Long id) {
        return ApiResponse.success(feedbackService.mobileComplaint(id));
    }

    @GetMapping("/orders/{id}/complaints/status")
    public ApiResponse<ComplaintResponse> complaintStatus(@PathVariable Long id) {
        return ApiResponse.success(feedbackService.complaintStatus(id));
    }
}
