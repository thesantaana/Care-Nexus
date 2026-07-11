package com.carenexus.care.order.service;

import com.carenexus.auth.CurrentUser;
import com.carenexus.care.CareOrderAccessService;
import com.carenexus.care.FamilyElderAccessService;
import com.carenexus.care.constant.CareRoles;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.mapper.CareOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class CareOrderAccessServiceImpl implements CareOrderAccessService {

    private final CareOrderMapper orderMapper;
    private final FamilyElderAccessService elderAccessService;

    public CareOrderAccessServiceImpl(CareOrderMapper orderMapper, FamilyElderAccessService elderAccessService) {
        this.orderMapper = orderMapper;
        this.elderAccessService = elderAccessService;
    }

    @Override
    public boolean canViewOrder(CurrentUser currentUser, Long orderId) {
        CareOrder order = orderMapper.selectById(orderId);
        if (currentUser == null || order == null) {
            return false;
        }
        if (CareRoles.ADMIN.equals(currentUser.getMainRoleCode())
                || CareRoles.OPERATOR.equals(currentUser.getMainRoleCode())) {
            return true;
        }
        if (CareRoles.CAREGIVER.equals(currentUser.getMainRoleCode())) {
            return currentUser.getUserId().equals(order.getAssignedCaregiverId());
        }
        return currentUser.getUserId().equals(order.getOrderUserId())
                || elderAccessService.canAccessElder(currentUser, order.getElderId());
    }

    @Override
    public boolean canOperateAssignedOrder(CurrentUser currentUser, Long orderId) {
        CareOrder order = orderMapper.selectById(orderId);
        return currentUser != null && order != null
                && CareRoles.CAREGIVER.equals(currentUser.getMainRoleCode())
                && currentUser.getUserId().equals(order.getAssignedCaregiverId());
    }
}
