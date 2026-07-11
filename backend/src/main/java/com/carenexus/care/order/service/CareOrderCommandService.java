package com.carenexus.care.order.service;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.care.CareOrderAccessService;
import com.carenexus.care.FamilyElderAccessService;
import com.carenexus.care.address.service.CareAddressService;
import com.carenexus.care.catalog.service.CareServiceItemService;
import com.carenexus.care.constant.CarePermissions;
import com.carenexus.care.constant.CareRoles;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.AssignOrderRequest;
import com.carenexus.care.dto.CompleteOrderRequest;
import com.carenexus.care.dto.OrderCreateRequest;
import com.carenexus.care.dto.ReasonRequest;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.entity.CareServiceRecord;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.mapper.CareServiceRecordMapper;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.support.CareText;
import com.carenexus.care.vo.CareOrderResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CareOrderCommandService {

    private static final Set<String> CANCELLABLE_STATUSES = new HashSet<>(Arrays.asList(
            CareStatuses.PENDING_ASSIGN, CareStatuses.PENDING_CONFIRM, CareStatuses.CONFIRMED));

    private final CareAccessPolicy accessPolicy;
    private final FamilyElderAccessService elderAccessService;
    private final CareOrderAccessService orderAccessService;
    private final CurrentUserService currentUserService;
    private final CareServiceItemService serviceItemService;
    private final CareAddressService addressService;
    private final CareOrderMapper orderMapper;
    private final CareServiceRecordMapper serviceRecordMapper;
    private final CareOrderStateService stateService;
    private final OperationLogService operationLogService;
    private final CareAssembler assembler;

    public CareOrderCommandService(CareAccessPolicy accessPolicy, FamilyElderAccessService elderAccessService,
            CareOrderAccessService orderAccessService, CurrentUserService currentUserService,
            CareServiceItemService serviceItemService, CareAddressService addressService,
            CareOrderMapper orderMapper, CareServiceRecordMapper serviceRecordMapper,
            CareOrderStateService stateService, OperationLogService operationLogService,
            CareAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.elderAccessService = elderAccessService;
        this.orderAccessService = orderAccessService;
        this.currentUserService = currentUserService;
        this.serviceItemService = serviceItemService;
        this.addressService = addressService;
        this.orderMapper = orderMapper;
        this.serviceRecordMapper = serviceRecordMapper;
        this.stateService = stateService;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    @Transactional
    public CareOrderResponse create(OrderCreateRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_CREATE);
        if (!elderAccessService.canAccessElder(currentUser, request.getElderId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Elder is outside current user scope");
        }
        serviceItemService.requireEnabled(request.getServiceItemId());
        addressService.requireUsableAddress(currentUser, request.getAddressId(), request.getElderId());
        CareOrder order = new CareOrder();
        order.setElderId(request.getElderId());
        order.setOrderUserId(currentUser.getUserId());
        order.setServiceItemId(request.getServiceItemId());
        order.setAddressId(request.getAddressId());
        order.setAppointmentTime(request.getAppointmentTime());
        order.setOrderStatus(CareStatuses.PENDING_ASSIGN);
        orderMapper.insert(order);
        stateService.recordCreated(order, currentUser.getUserId());
        operationLogService.record(currentUser, "CARE_ORDER_CREATE", "CARE_ORDER", order.getId(), "SUCCESS");
        return assembler.toOrder(order);
    }

    @Transactional
    public CareOrderResponse cancel(Long orderId, ReasonRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_CREATE);
        CareOrder order = requireOrder(orderId);
        requireView(currentUser, orderId);
        if (!CANCELLABLE_STATUSES.contains(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order cannot be cancelled in current status");
        }
        String reason = CareText.required(request.getReason(), "Cancel reason is required");
        String fromStatus = order.getOrderStatus();
        order.setCancelReason(reason);
        stateService.transition(order, fromStatus, CareStatuses.CANCELLED, currentUser.getUserId(), reason);
        operationLogService.record(currentUser, "CARE_ORDER_CANCEL", "CARE_ORDER", order.getId(), "SUCCESS");
        return assembler.toOrder(order);
    }

    @Transactional
    public CareOrderResponse assign(Long orderId, AssignOrderRequest request) {
        CurrentUser operator = accessPolicy.requirePermission(CarePermissions.ORDER_ASSIGN);
        CareOrder order = requireOrder(orderId);
        CurrentUser caregiver = currentUserService.loadActiveUser(request.getCaregiverId());
        if (!CareRoles.CAREGIVER.equals(caregiver.getMainRoleCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Assigned user is not a caregiver");
        }
        order.setAssignedCaregiverId(caregiver.getUserId());
        stateService.transition(order, CareStatuses.PENDING_ASSIGN, CareStatuses.PENDING_CONFIRM,
                operator.getUserId(), null);
        operationLogService.record(operator, "CARE_ORDER_ASSIGN", "CARE_ORDER", order.getId(), "SUCCESS");
        return assembler.toOrder(order);
    }

    @Transactional
    public CareOrderResponse confirm(Long orderId) {
        return caregiverTransition(orderId, CareStatuses.PENDING_CONFIRM, CareStatuses.CONFIRMED,
                "CARE_ORDER_CONFIRM");
    }

    @Transactional
    public CareOrderResponse start(Long orderId) {
        return caregiverTransition(orderId, CareStatuses.CONFIRMED, CareStatuses.IN_SERVICE,
                "CARE_ORDER_START");
    }

    @Transactional
    public CareOrderResponse complete(Long orderId, CompleteOrderRequest request) {
        CurrentUser caregiver = accessPolicy.requirePermission(CarePermissions.ORDER_EXECUTE);
        CareOrder order = requireAssignedOrder(caregiver, orderId);
        if (request.getCompletedAt().isBefore(order.getAppointmentTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Completion time cannot precede appointment time");
        }
        CareServiceRecord record = new CareServiceRecord();
        record.setOrderId(order.getId());
        record.setCaregiverId(caregiver.getUserId());
        record.setServiceContent(CareText.required(request.getServiceContent(), "Service content is required"));
        record.setCompletedAt(request.getCompletedAt());
        serviceRecordMapper.insert(record);
        stateService.transition(order, CareStatuses.IN_SERVICE, CareStatuses.COMPLETED,
                caregiver.getUserId(), null);
        operationLogService.record(caregiver, "CARE_ORDER_COMPLETE", "CARE_ORDER", order.getId(), "SUCCESS");
        return assembler.toOrder(order);
    }

    private CareOrderResponse caregiverTransition(Long orderId, String fromStatus,
            String targetStatus, String action) {
        CurrentUser caregiver = accessPolicy.requirePermission(CarePermissions.ORDER_EXECUTE);
        CareOrder order = requireAssignedOrder(caregiver, orderId);
        stateService.transition(order, fromStatus, targetStatus, caregiver.getUserId(), null);
        operationLogService.record(caregiver, action, "CARE_ORDER", order.getId(), "SUCCESS");
        return assembler.toOrder(order);
    }

    private CareOrder requireAssignedOrder(CurrentUser caregiver, Long orderId) {
        CareOrder order = requireOrder(orderId);
        if (!orderAccessService.canOperateAssignedOrder(caregiver, orderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Order is not assigned to current caregiver");
        }
        return order;
    }

    private void requireView(CurrentUser currentUser, Long orderId) {
        if (!orderAccessService.canViewOrder(currentUser, orderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Order is outside current user scope");
        }
    }

    private CareOrder requireOrder(Long id) {
        CareOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Order not found");
        }
        return order;
    }
}
