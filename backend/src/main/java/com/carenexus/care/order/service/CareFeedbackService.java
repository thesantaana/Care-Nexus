package com.carenexus.care.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.care.CareOrderAccessService;
import com.carenexus.care.constant.CarePermissions;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.ComplaintRequest;
import com.carenexus.care.dto.EvaluationRequest;
import com.carenexus.care.dto.HandleComplaintRequest;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.entity.CareOrderComplaint;
import com.carenexus.care.entity.CareOrderEvaluation;
import com.carenexus.care.mapper.CareOrderComplaintMapper;
import com.carenexus.care.mapper.CareOrderEvaluationMapper;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.support.CareText;
import com.carenexus.care.vo.ComplaintResponse;
import com.carenexus.care.vo.EvaluationResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CareFeedbackService {

    private final CareAccessPolicy accessPolicy;
    private final CareOrderAccessService orderAccessService;
    private final CareOrderMapper orderMapper;
    private final CareOrderEvaluationMapper evaluationMapper;
    private final CareOrderComplaintMapper complaintMapper;
    private final OperationLogService operationLogService;
    private final CareAssembler assembler;

    public CareFeedbackService(CareAccessPolicy accessPolicy, CareOrderAccessService orderAccessService,
            CareOrderMapper orderMapper, CareOrderEvaluationMapper evaluationMapper,
            CareOrderComplaintMapper complaintMapper, OperationLogService operationLogService,
            CareAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.orderAccessService = orderAccessService;
        this.orderMapper = orderMapper;
        this.evaluationMapper = evaluationMapper;
        this.complaintMapper = complaintMapper;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    @Transactional
    public EvaluationResponse evaluate(Long orderId, EvaluationRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_EVALUATE);
        CareOrder order = requireCompletedAndViewable(currentUser, orderId);
        if (evaluationMapper.selectCount(new QueryWrapper<CareOrderEvaluation>().eq("order_id", orderId)) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order has already been evaluated");
        }
        CareOrderEvaluation evaluation = new CareOrderEvaluation();
        evaluation.setOrderId(order.getId());
        evaluation.setEvaluatorId(currentUser.getUserId());
        evaluation.setRating(request.getRating());
        evaluation.setContent(CareText.optional(request.getContent()));
        evaluationMapper.insert(evaluation);
        operationLogService.record(currentUser, "CARE_ORDER_EVALUATE", "CARE_ORDER", orderId, "SUCCESS");
        return assembler.toEvaluation(evaluation);
    }

    @Transactional
    public ComplaintResponse complain(Long orderId, ComplaintRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_EVALUATE);
        CareOrder order = requireCompletedAndViewable(currentUser, orderId);
        if (complaintMapper.selectCount(new QueryWrapper<CareOrderComplaint>().eq("order_id", orderId)) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order has already been complained");
        }
        CareOrderComplaint complaint = new CareOrderComplaint();
        complaint.setOrderId(order.getId());
        complaint.setComplainantId(currentUser.getUserId());
        complaint.setComplaintContent(CareText.required(request.getComplaintContent(),
                "Complaint content is required"));
        complaint.setComplaintStatus(CareStatuses.PROCESSING);
        complaintMapper.insert(complaint);
        operationLogService.record(currentUser, "CARE_COMPLAINT_CREATE", "CARE_COMPLAINT",
                complaint.getId(), "SUCCESS");
        return assembler.toComplaint(complaint);
    }

    public ComplaintResponse mobileComplaint(Long complaintId) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_VIEW);
        CareOrderComplaint complaint = requireComplaint(complaintId);
        requireOrderView(currentUser, complaint.getOrderId());
        return assembler.toComplaint(complaint);
    }

    public ComplaintResponse complaintStatus(Long orderId) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_VIEW);
        requireOrderView(currentUser, orderId);
        CareOrderComplaint complaint = complaintMapper.selectOne(new QueryWrapper<CareOrderComplaint>()
                .eq("order_id", orderId));
        if (complaint == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Complaint not found");
        }
        return assembler.toComplaint(complaint);
    }

    public PageResponse<ComplaintResponse> pageComplaints(String status, int pageNo, int pageSize) {
        accessPolicy.requirePermission(CarePermissions.COMPLAINT_HANDLE);
        QueryWrapper<CareOrderComplaint> wrapper = new QueryWrapper<CareOrderComplaint>().orderByDesc("id");
        if (StringUtils.hasText(status)) {
            wrapper.eq("complaint_status", status.trim().toUpperCase());
        }
        Page<CareOrderComplaint> page = complaintMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<ComplaintResponse> records = page.getRecords().stream()
                .map(assembler::toComplaint).collect(Collectors.toList());
        return PageResponse.from(page, records);
    }

    public ComplaintResponse complaintDetail(Long complaintId) {
        accessPolicy.requirePermission(CarePermissions.COMPLAINT_HANDLE);
        return assembler.toComplaint(requireComplaint(complaintId));
    }

    @Transactional
    public ComplaintResponse handle(Long complaintId, HandleComplaintRequest request) {
        CurrentUser operator = accessPolicy.requirePermission(CarePermissions.COMPLAINT_HANDLE);
        CareOrderComplaint complaint = requireComplaint(complaintId);
        if (!CareStatuses.PROCESSING.equals(complaint.getComplaintStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Complaint has already been handled");
        }
        complaint.setComplaintStatus(CareStatuses.PROCESSED);
        complaint.setHandledBy(operator.getUserId());
        complaint.setHandledResult(CareText.required(request.getHandledResult(), "Handled result is required"));
        complaint.setHandledAt(LocalDateTime.now());
        complaintMapper.updateById(complaint);
        operationLogService.record(operator, "CARE_COMPLAINT_HANDLE", "CARE_COMPLAINT",
                complaint.getId(), "SUCCESS");
        return assembler.toComplaint(complaint);
    }

    private CareOrder requireCompletedAndViewable(CurrentUser currentUser, Long orderId) {
        CareOrder order = requireOrderView(currentUser, orderId);
        if (!CareStatuses.COMPLETED.equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only completed orders can be evaluated or complained");
        }
        return order;
    }

    private CareOrder requireOrderView(CurrentUser currentUser, Long orderId) {
        CareOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Order not found");
        }
        if (!orderAccessService.canViewOrder(currentUser, orderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Order is outside current user scope");
        }
        return order;
    }

    private CareOrderComplaint requireComplaint(Long complaintId) {
        CareOrderComplaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Complaint not found");
        }
        return complaint;
    }
}
