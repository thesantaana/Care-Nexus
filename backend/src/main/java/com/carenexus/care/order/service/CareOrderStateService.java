package com.carenexus.care.order.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.entity.CareOrderStatusLog;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.mapper.CareOrderStatusLogMapper;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class CareOrderStateService {

    private final CareOrderMapper orderMapper;
    private final CareOrderStatusLogMapper statusLogMapper;

    public CareOrderStateService(CareOrderMapper orderMapper, CareOrderStatusLogMapper statusLogMapper) {
        this.orderMapper = orderMapper;
        this.statusLogMapper = statusLogMapper;
    }

    public void transition(CareOrder order, String expectedStatus, String targetStatus,
            Long operatorId, String reason) {
        if (!expectedStatus.equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order status does not allow this operation");
        }
        String fromStatus = order.getOrderStatus();
        order.setOrderStatus(targetStatus);
        int updated = orderMapper.update(order, new UpdateWrapper<CareOrder>()
                .eq("id", order.getId()).eq("order_status", expectedStatus));
        if (updated != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order status changed concurrently");
        }
        record(order.getId(), fromStatus, targetStatus, operatorId, reason);
    }

    public void recordCreated(CareOrder order, Long operatorId) {
        record(order.getId(), null, order.getOrderStatus(), operatorId, null);
    }

    private void record(Long orderId, String fromStatus, String targetStatus, Long operatorId, String reason) {
        CareOrderStatusLog log = new CareOrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus(targetStatus);
        log.setOperatedBy(operatorId);
        log.setReason(reason);
        statusLogMapper.insert(log);
    }
}
