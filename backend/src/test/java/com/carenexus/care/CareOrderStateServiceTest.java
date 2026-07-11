package com.carenexus.care;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.mapper.CareOrderStatusLogMapper;
import com.carenexus.care.order.service.CareOrderStateService;
import com.carenexus.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareOrderStateServiceTest {

    @Mock
    private CareOrderMapper orderMapper;
    @Mock
    private CareOrderStatusLogMapper statusLogMapper;

    private CareOrderStateService stateService;

    @BeforeEach
    void setUp() {
        stateService = new CareOrderStateService(orderMapper, statusLogMapper);
    }

    @Test
    void shouldMovePendingConfirmOrderToConfirmed() {
        CareOrder order = order(1L, CareStatuses.PENDING_CONFIRM);
        when(orderMapper.update(any(), any())).thenReturn(1);
        when(statusLogMapper.insert(any())).thenReturn(1);

        stateService.transition(order, CareStatuses.PENDING_CONFIRM, CareStatuses.CONFIRMED, 6L, null);

        assertEquals(CareStatuses.CONFIRMED, order.getOrderStatus());
        verify(orderMapper).update(any(), any());
        verify(statusLogMapper).insert(any());
    }

    @Test
    void shouldRejectIllegalStatusTransition() {
        CareOrder order = order(1L, CareStatuses.PENDING_ASSIGN);

        assertThrows(BusinessException.class, () -> stateService.transition(order,
                CareStatuses.CONFIRMED, CareStatuses.IN_SERVICE, 6L, null));
    }

    private CareOrder order(Long id, String status) {
        CareOrder order = new CareOrder();
        order.setId(id);
        order.setOrderStatus(status);
        return order;
    }
}
