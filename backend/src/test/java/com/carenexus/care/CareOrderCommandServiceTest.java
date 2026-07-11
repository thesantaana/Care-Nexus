package com.carenexus.care;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.care.address.service.CareAddressService;
import com.carenexus.care.catalog.service.CareServiceItemService;
import com.carenexus.care.constant.CareRoles;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.AssignOrderRequest;
import com.carenexus.care.dto.OrderCreateRequest;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.mapper.CareServiceRecordMapper;
import com.carenexus.care.order.service.CareOrderCommandService;
import com.carenexus.care.order.service.CareOrderStateService;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.vo.CareOrderResponse;
import com.carenexus.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareOrderCommandServiceTest {

    @Mock private CareAccessPolicy accessPolicy;
    @Mock private FamilyElderAccessService elderAccessService;
    @Mock private CareOrderAccessService orderAccessService;
    @Mock private CurrentUserService currentUserService;
    @Mock private CareServiceItemService serviceItemService;
    @Mock private CareAddressService addressService;
    @Mock private CareOrderMapper orderMapper;
    @Mock private CareServiceRecordMapper serviceRecordMapper;
    @Mock private CareOrderStateService stateService;
    @Mock private OperationLogService operationLogService;
    @Mock private CareAssembler assembler;

    private CareOrderCommandService commandService;

    @BeforeEach
    void setUp() {
        commandService = new CareOrderCommandService(accessPolicy, elderAccessService, orderAccessService,
                currentUserService, serviceItemService, addressService, orderMapper, serviceRecordMapper,
                stateService, operationLogService, assembler);
    }

    @Test
    void shouldCreatePendingAssignmentOrder() {
        CurrentUser family = user(8L, CareRoles.FAMILY);
        OrderCreateRequest request = new OrderCreateRequest();
        request.setElderId(1L);
        request.setServiceItemId(2L);
        request.setAddressId(3L);
        request.setAppointmentTime(LocalDateTime.now().plusDays(1));
        when(accessPolicy.requirePermission(any())).thenReturn(family);
        when(elderAccessService.canAccessElder(family, 1L)).thenReturn(true);
        when(orderMapper.insert(any())).thenAnswer(invocation -> {
            CareOrder order = invocation.getArgument(0);
            order.setId(10L);
            return 1;
        });
        when(assembler.toOrder(any())).thenReturn(new CareOrderResponse());

        commandService.create(request);

        verify(orderMapper).insert(any());
        verify(stateService).recordCreated(any(), any());
    }

    @Test
    void shouldRejectAssignmentToNonCaregiver() {
        CareOrder order = order(10L, CareStatuses.PENDING_ASSIGN);
        AssignOrderRequest request = new AssignOrderRequest();
        request.setCaregiverId(7L);
        when(accessPolicy.requirePermission(any())).thenReturn(user(2L, CareRoles.OPERATOR));
        when(orderMapper.selectById(10L)).thenReturn(order);
        when(currentUserService.loadActiveUser(7L)).thenReturn(user(7L, CareRoles.ELDER));

        assertThrows(BusinessException.class, () -> commandService.assign(10L, request));
    }

    private CurrentUser user(Long id, String role) {
        return new CurrentUser(id, "user" + id, "User", role, role, "NORMAL", Collections.emptySet());
    }

    private CareOrder order(Long id, String status) {
        CareOrder order = new CareOrder();
        order.setId(id);
        order.setOrderStatus(status);
        return order;
    }
}
