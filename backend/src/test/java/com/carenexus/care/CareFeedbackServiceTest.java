package com.carenexus.care;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.care.constant.CareRoles;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.EvaluationRequest;
import com.carenexus.care.entity.CareOrder;
import com.carenexus.care.mapper.CareOrderComplaintMapper;
import com.carenexus.care.mapper.CareOrderEvaluationMapper;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.order.service.CareFeedbackService;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.common.exception.BusinessException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareFeedbackServiceTest {

    @Mock private CareAccessPolicy accessPolicy;
    @Mock private CareOrderAccessService orderAccessService;
    @Mock private CareOrderMapper orderMapper;
    @Mock private CareOrderEvaluationMapper evaluationMapper;
    @Mock private CareOrderComplaintMapper complaintMapper;
    @Mock private OperationLogService operationLogService;
    @Mock private CareAssembler assembler;

    private CareFeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new CareFeedbackService(accessPolicy, orderAccessService, orderMapper,
                evaluationMapper, complaintMapper, operationLogService, assembler);
    }

    @Test
    void shouldRejectEvaluationBeforeOrderCompleted() {
        CurrentUser family = user(8L, CareRoles.FAMILY);
        CareOrder order = new CareOrder();
        order.setId(10L);
        order.setOrderStatus(CareStatuses.CONFIRMED);
        when(accessPolicy.requirePermission(any())).thenReturn(family);
        when(orderMapper.selectById(10L)).thenReturn(order);
        when(orderAccessService.canViewOrder(family, 10L)).thenReturn(true);
        EvaluationRequest request = new EvaluationRequest();
        request.setRating(5);

        assertThrows(BusinessException.class, () -> feedbackService.evaluate(10L, request));
    }

    @Test
    void shouldRejectDuplicateEvaluation() {
        CurrentUser family = user(8L, CareRoles.FAMILY);
        CareOrder order = new CareOrder();
        order.setId(10L);
        order.setOrderStatus(CareStatuses.COMPLETED);
        when(accessPolicy.requirePermission(any())).thenReturn(family);
        when(orderMapper.selectById(10L)).thenReturn(order);
        when(orderAccessService.canViewOrder(family, 10L)).thenReturn(true);
        when(evaluationMapper.selectCount(any())).thenReturn(1L);
        EvaluationRequest request = new EvaluationRequest();
        request.setRating(5);

        assertThrows(BusinessException.class, () -> feedbackService.evaluate(10L, request));
    }

    private CurrentUser user(Long id, String role) {
        return new CurrentUser(id, "user" + id, "User", role, role, "NORMAL", Collections.emptySet());
    }
}
