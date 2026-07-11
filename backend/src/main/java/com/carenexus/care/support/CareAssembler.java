package com.carenexus.care.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.care.entity.CareAddress;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.entity.CareOrderComplaint;
import com.carenexus.care.entity.CareOrderEvaluation;
import com.carenexus.care.entity.CareServiceItem;
import com.carenexus.care.entity.CareServiceRecord;
import com.carenexus.care.entity.ElderFamilyBinding;
import com.carenexus.care.entity.ElderProfile;
import com.carenexus.care.mapper.CareOrderComplaintMapper;
import com.carenexus.care.mapper.CareOrderEvaluationMapper;
import com.carenexus.care.mapper.CareServiceRecordMapper;
import com.carenexus.care.vo.AddressResponse;
import com.carenexus.care.vo.BindingResponse;
import com.carenexus.care.vo.CareOrderResponse;
import com.carenexus.care.vo.ComplaintResponse;
import com.carenexus.care.vo.ElderResponse;
import com.carenexus.care.vo.EvaluationResponse;
import com.carenexus.care.vo.ServiceItemResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CareAssembler {

    private final ContactProtectionService contactProtectionService;
    private final CareOrderEvaluationMapper evaluationMapper;
    private final CareOrderComplaintMapper complaintMapper;
    private final CareServiceRecordMapper serviceRecordMapper;

    public CareAssembler(ContactProtectionService contactProtectionService,
            CareOrderEvaluationMapper evaluationMapper, CareOrderComplaintMapper complaintMapper,
            CareServiceRecordMapper serviceRecordMapper) {
        this.contactProtectionService = contactProtectionService;
        this.evaluationMapper = evaluationMapper;
        this.complaintMapper = complaintMapper;
        this.serviceRecordMapper = serviceRecordMapper;
    }

    public ElderResponse toElder(ElderProfile elder, Long bindingId) {
        ElderResponse response = new ElderResponse();
        response.setElderId(elder.getId());
        response.setElderName(elder.getElderName());
        response.setBindingId(bindingId);
        return response;
    }

    public BindingResponse toBinding(ElderFamilyBinding binding, ElderProfile elder) {
        BindingResponse response = new BindingResponse();
        response.setId(binding.getId());
        response.setElderId(binding.getElderId());
        response.setElderName(elder == null ? null : elder.getElderName());
        response.setStatus(binding.getBindingStatus());
        response.setVerifyType(binding.getVerifyType());
        response.setVerifiedAt(binding.getVerifiedAt());
        return response;
    }

    public ServiceItemResponse toServiceItem(CareServiceItem item) {
        ServiceItemResponse response = new ServiceItemResponse();
        response.setId(item.getId());
        response.setServiceName(item.getServiceName());
        response.setCategory(item.getServiceCategory());
        response.setDescription(item.getDescription());
        response.setServiceStatus(item.getServiceStatus());
        return response;
    }

    public AddressResponse toAddress(CareAddress address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setElderId(address.getElderId());
        response.setContactName(address.getContactName());
        response.setMaskedMobile(contactProtectionService.maskLast4(address.getContactMobileLast4()));
        response.setAddressDetail(address.getAddressDetail());
        response.setDefaultAddress(Integer.valueOf(1).equals(address.getIsDefault()));
        response.setAddressStatus(address.getAddressStatus());
        return response;
    }

    public List<CareOrderResponse> toOrders(List<CareOrder> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> orderIds = orders.stream().map(CareOrder::getId).collect(Collectors.toList());
        Map<Long, CareOrderEvaluation> evaluations = indexEvaluations(orderIds);
        Map<Long, CareOrderComplaint> complaints = indexComplaints(orderIds);
        Map<Long, CareServiceRecord> records = indexServiceRecords(orderIds);
        List<CareOrderResponse> result = new ArrayList<>();
        for (CareOrder order : orders) {
            result.add(toOrder(order, evaluations.get(order.getId()), complaints.get(order.getId()),
                    records.get(order.getId())));
        }
        return result;
    }

    public CareOrderResponse toOrder(CareOrder order) {
        return toOrders(Collections.singletonList(order)).get(0);
    }

    public EvaluationResponse toEvaluation(CareOrderEvaluation evaluation) {
        EvaluationResponse response = new EvaluationResponse();
        response.setId(evaluation.getId());
        response.setOrderId(evaluation.getOrderId());
        response.setRating(evaluation.getRating());
        response.setContent(evaluation.getContent());
        response.setCreatedAt(evaluation.getCreatedAt());
        return response;
    }

    public ComplaintResponse toComplaint(CareOrderComplaint complaint) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setOrderId(complaint.getOrderId());
        response.setComplaintContent(complaint.getComplaintContent());
        response.setComplaintStatus(complaint.getComplaintStatus());
        response.setHandledResult(complaint.getHandledResult());
        response.setHandledAt(complaint.getHandledAt());
        response.setCreatedAt(complaint.getCreatedAt());
        return response;
    }

    private CareOrderResponse toOrder(CareOrder order, CareOrderEvaluation evaluation,
            CareOrderComplaint complaint, CareServiceRecord record) {
        CareOrderResponse response = new CareOrderResponse();
        response.setId(order.getId());
        response.setElderId(order.getElderId());
        response.setOrderUserId(order.getOrderUserId());
        response.setServiceItemId(order.getServiceItemId());
        response.setAddressId(order.getAddressId());
        response.setAssignedCaregiverId(order.getAssignedCaregiverId());
        response.setAppointmentTime(order.getAppointmentTime());
        response.setOrderStatus(order.getOrderStatus());
        response.setCancelReason(order.getCancelReason());
        response.setEvaluationStatus(evaluation == null ? "NOT_EVALUATED" : "EVALUATED");
        response.setComplaintStatus(complaint == null ? "NOT_COMPLAINED" : complaint.getComplaintStatus());
        if (record != null) {
            response.setServiceContent(record.getServiceContent());
            response.setCompletedAt(record.getCompletedAt());
        }
        return response;
    }

    private Map<Long, CareOrderEvaluation> indexEvaluations(List<Long> orderIds) {
        Map<Long, CareOrderEvaluation> result = new LinkedHashMap<>();
        for (CareOrderEvaluation value : evaluationMapper.selectList(new QueryWrapper<CareOrderEvaluation>()
                .in("order_id", orderIds))) {
            result.put(value.getOrderId(), value);
        }
        return result;
    }

    private Map<Long, CareOrderComplaint> indexComplaints(List<Long> orderIds) {
        Map<Long, CareOrderComplaint> result = new LinkedHashMap<>();
        for (CareOrderComplaint value : complaintMapper.selectList(new QueryWrapper<CareOrderComplaint>()
                .in("order_id", orderIds))) {
            result.put(value.getOrderId(), value);
        }
        return result;
    }

    private Map<Long, CareServiceRecord> indexServiceRecords(List<Long> orderIds) {
        Map<Long, CareServiceRecord> result = new LinkedHashMap<>();
        for (CareServiceRecord value : serviceRecordMapper.selectList(new QueryWrapper<CareServiceRecord>()
                .in("order_id", orderIds))) {
            result.put(value.getOrderId(), value);
        }
        return result;
    }
}
