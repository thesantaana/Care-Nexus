package com.carenexus.doctor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.doctor.mapper.DoctorElderAuthorizationMapper;
import com.carenexus.doctor.mapper.ElderHealthProfileMapper;
import com.carenexus.doctor.service.DoctorAuthorizationServiceImpl;
import com.carenexus.doctor.support.DoctorAssembler;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class DoctorAuthorizationServiceTest {
    @Test
    void doctorCanOnlyAccessActiveAuthorizedElder() {
        DoctorElderAuthorizationMapper authorizationMapper = mock(DoctorElderAuthorizationMapper.class);
        when(authorizationMapper.selectCount(any())).thenReturn(1L, 0L);
        DoctorAuthorizationServiceImpl service = new DoctorAuthorizationServiceImpl(authorizationMapper,
                mock(ElderHealthProfileMapper.class), mock(CurrentUserService.class),
                mock(OperationLogService.class), new DoctorAssembler());
        CurrentUser doctor = new CurrentUser(7L, "doctor", "Doctor", "DOCTOR", "医生",
                "NORMAL", Collections.singleton("doctor:elder:view"));

        assertTrue(service.canAccessElder(doctor, 1L));
        assertFalse(service.canAccessElder(doctor, 2L));
    }
}
