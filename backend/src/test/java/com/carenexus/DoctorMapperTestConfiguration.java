package com.carenexus;

import static org.mockito.Mockito.mock;

import com.carenexus.doctor.mapper.DoctorElderAuthorizationMapper;
import com.carenexus.doctor.mapper.ElderHealthProfileMapper;
import com.carenexus.doctor.mapper.FollowUpRecordMapper;
import com.carenexus.doctor.mapper.HealthAlertMapper;
import com.carenexus.doctor.mapper.HealthAssessmentMapper;
import com.carenexus.doctor.mapper.HealthRecordMapper;
import com.carenexus.doctor.mapper.InterventionRecordMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DoctorMapperTestConfiguration {
    @Bean public DoctorElderAuthorizationMapper doctorElderAuthorizationMapper() {
        return mock(DoctorElderAuthorizationMapper.class);
    }
    @Bean public ElderHealthProfileMapper elderHealthProfileMapper() {
        return mock(ElderHealthProfileMapper.class);
    }
    @Bean public HealthRecordMapper healthRecordMapper() {
        return mock(HealthRecordMapper.class);
    }
    @Bean public HealthAlertMapper healthAlertMapper() {
        return mock(HealthAlertMapper.class);
    }
    @Bean public FollowUpRecordMapper followUpRecordMapper() {
        return mock(FollowUpRecordMapper.class);
    }
    @Bean public InterventionRecordMapper interventionRecordMapper() {
        return mock(InterventionRecordMapper.class);
    }
    @Bean public HealthAssessmentMapper healthAssessmentMapper() {
        return mock(HealthAssessmentMapper.class);
    }
}
