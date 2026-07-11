package com.carenexus.care.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.auth.CurrentUser;
import com.carenexus.care.CareOrderAccessService;
import com.carenexus.care.binding.service.FamilyBindingService;
import com.carenexus.care.constant.CarePermissions;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.vo.CareOrderResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CareOrderQueryService {

    private final CareAccessPolicy accessPolicy;
    private final FamilyBindingService bindingService;
    private final CareOrderAccessService orderAccessService;
    private final CareOrderMapper orderMapper;
    private final CareAssembler assembler;

    public CareOrderQueryService(CareAccessPolicy accessPolicy, FamilyBindingService bindingService,
            CareOrderAccessService orderAccessService, CareOrderMapper orderMapper, CareAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.bindingService = bindingService;
        this.orderAccessService = orderAccessService;
        this.orderMapper = orderMapper;
        this.assembler = assembler;
    }

    public PageResponse<CareOrderResponse> mobileOrders(String status, int pageNo, int pageSize) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_VIEW);
        List<Long> elderIds = bindingService.accessibleElderIds(currentUser);
        QueryWrapper<CareOrder> wrapper = new QueryWrapper<CareOrder>()
                .and(scope -> {
                    scope.eq("order_user_id", currentUser.getUserId());
                    if (!elderIds.isEmpty()) {
                        scope.or().in("elder_id", elderIds);
                    }
                });
        return page(wrapper, status, pageNo, pageSize);
    }

    public CareOrderResponse mobileDetail(Long orderId) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ORDER_VIEW);
        return requireViewable(currentUser, orderId);
    }

    public PageResponse<CareOrderResponse> caregiverOrders(String status, int pageNo, int pageSize) {
        CurrentUser caregiver = accessPolicy.requirePermission(CarePermissions.ORDER_EXECUTE);
        QueryWrapper<CareOrder> wrapper = new QueryWrapper<CareOrder>()
                .eq("assigned_caregiver_id", caregiver.getUserId());
        return page(wrapper, status, pageNo, pageSize);
    }

    public CareOrderResponse caregiverDetail(Long orderId) {
        CurrentUser caregiver = accessPolicy.requirePermission(CarePermissions.ORDER_EXECUTE);
        if (!orderAccessService.canOperateAssignedOrder(caregiver, orderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Order is outside current caregiver scope");
        }
        return assembler.toOrder(requireOrder(orderId));
    }

    public PageResponse<CareOrderResponse> pendingAssignment(int pageNo, int pageSize) {
        accessPolicy.requirePermission(CarePermissions.ORDER_ASSIGN);
        QueryWrapper<CareOrder> wrapper = new QueryWrapper<CareOrder>()
                .eq("order_status", CareStatuses.PENDING_ASSIGN);
        return page(wrapper, null, pageNo, pageSize);
    }

    private PageResponse<CareOrderResponse> page(QueryWrapper<CareOrder> wrapper, String status,
            int pageNo, int pageSize) {
        if (StringUtils.hasText(status)) {
            wrapper.eq("order_status", status.trim().toUpperCase());
        }
        wrapper.orderByDesc("id");
        Page<CareOrder> page = orderMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.from(page, assembler.toOrders(page.getRecords()));
    }

    private CareOrderResponse requireViewable(CurrentUser currentUser, Long orderId) {
        if (!orderAccessService.canViewOrder(currentUser, orderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Order is outside current user scope");
        }
        return assembler.toOrder(requireOrder(orderId));
    }

    private CareOrder requireOrder(Long orderId) {
        CareOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Order not found");
        }
        return order;
    }
}
