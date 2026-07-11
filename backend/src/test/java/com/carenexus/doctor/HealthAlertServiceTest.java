package com.carenexus.doctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.carenexus.audit.OperationLogService;
import com.carenexus.doctor.entity.HealthRecord;
import com.carenexus.doctor.health.HealthAlertService;
import com.carenexus.doctor.health.HealthThresholdProperties;
import com.carenexus.doctor.mapper.HealthAlertMapper;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.support.DoctorAssembler;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class HealthAlertServiceTest {
    @Test
    void createsAlertsForIndicatorsOutsideConfiguredRange() {
        HealthAlertMapper mapper = mock(HealthAlertMapper.class);
        HealthAlertService service = new HealthAlertService(mapper, new HealthThresholdProperties(),
                mock(DoctorAccessPolicy.class), mock(OperationLogService.class), new DoctorAssembler());
        HealthRecord record = new HealthRecord();
        record.setId(8L);
        record.setElderId(3L);
        record.setSystolicPressure(160);
        record.setDiastolicPressure(95);
        record.setBloodGlucose(new BigDecimal("8.2"));
        record.setHeartRate(75);
        record.setBodyTemperature(new BigDecimal("36.5"));

        assertEquals(3, service.createThresholdAlerts(record).size());
        verify(mapper, org.mockito.Mockito.times(3)).insert(any());
    }
}
