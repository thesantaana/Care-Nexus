package com.carenexus.doctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.doctor.dto.DoctorRequests.HealthRecordRequest;
import com.carenexus.doctor.health.HealthAlertService;
import com.carenexus.doctor.health.HealthRecordService;
import com.carenexus.doctor.mapper.HealthRecordMapper;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.support.DoctorAssembler;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class HealthRecordServiceTest {
    @Test
    void rejectsRecordWithoutAnyHealthIndicator() {
        DoctorAccessPolicy accessPolicy = mock(DoctorAccessPolicy.class);
        when(accessPolicy.requireHealthAccess(1L)).thenReturn(new CurrentUser(7L, "doctor", "Doctor",
                "DOCTOR", "医生", "NORMAL", Collections.singleton("doctor:health:manage")));
        HealthRecordService service = new HealthRecordService(mock(HealthRecordMapper.class),
                mock(HealthAlertService.class), accessPolicy, mock(OperationLogService.class),
                new DoctorAssembler());
        HealthRecordRequest request = new HealthRecordRequest();
        request.setRecordTime(LocalDateTime.now());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.create(1L, request));
        assertEquals("At least one health indicator is required", exception.getMessage());
    }
}
