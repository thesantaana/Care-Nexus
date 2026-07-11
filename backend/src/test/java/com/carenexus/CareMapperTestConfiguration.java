package com.carenexus;

import com.carenexus.care.mapper.CareAddressMapper;
import com.carenexus.care.mapper.CareOrderComplaintMapper;
import com.carenexus.care.mapper.CareOrderEvaluationMapper;
import com.carenexus.care.mapper.CareOrderMapper;
import com.carenexus.care.mapper.CareOrderStatusLogMapper;
import com.carenexus.care.mapper.CareServiceItemMapper;
import com.carenexus.care.mapper.CareServiceRecordMapper;
import com.carenexus.care.mapper.ElderFamilyBindingMapper;
import com.carenexus.care.mapper.ElderProfileMapper;
import com.carenexus.doctor.mapper.DoctorElderAuthorizationMapper;
import com.carenexus.doctor.mapper.ElderHealthProfileMapper;
import com.carenexus.doctor.mapper.FollowUpRecordMapper;
import com.carenexus.doctor.mapper.HealthAlertMapper;
import com.carenexus.doctor.mapper.HealthAssessmentMapper;
import com.carenexus.doctor.mapper.HealthRecordMapper;
import com.carenexus.doctor.mapper.InterventionRecordMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CareMapperTestConfiguration {

    @Bean
    public ElderProfileMapper elderProfileMapper() {
        return Mockito.mock(ElderProfileMapper.class);
    }

    @Bean
    public ElderFamilyBindingMapper elderFamilyBindingMapper() {
        return Mockito.mock(ElderFamilyBindingMapper.class);
    }

    @Bean
    public CareServiceItemMapper careServiceItemMapper() {
        return Mockito.mock(CareServiceItemMapper.class);
    }

    @Bean
    public CareAddressMapper careAddressMapper() {
        return Mockito.mock(CareAddressMapper.class);
    }

    @Bean
    public CareOrderMapper careOrderMapper() {
        return Mockito.mock(CareOrderMapper.class);
    }

    @Bean
    public CareOrderStatusLogMapper careOrderStatusLogMapper() {
        return Mockito.mock(CareOrderStatusLogMapper.class);
    }

    @Bean
    public CareServiceRecordMapper careServiceRecordMapper() {
        return Mockito.mock(CareServiceRecordMapper.class);
    }

    @Bean
    public CareOrderEvaluationMapper careOrderEvaluationMapper() {
        return Mockito.mock(CareOrderEvaluationMapper.class);
    }

    @Bean
    public CareOrderComplaintMapper careOrderComplaintMapper() {
        return Mockito.mock(CareOrderComplaintMapper.class);
    }

    @Bean
    public DoctorElderAuthorizationMapper doctorElderAuthorizationMapper() {
        return Mockito.mock(DoctorElderAuthorizationMapper.class);
    }

    @Bean
    public ElderHealthProfileMapper elderHealthProfileMapper() {
        return Mockito.mock(ElderHealthProfileMapper.class);
    }

    @Bean
    public HealthRecordMapper healthRecordMapper() {
        return Mockito.mock(HealthRecordMapper.class);
    }

    @Bean
    public HealthAlertMapper healthAlertMapper() {
        return Mockito.mock(HealthAlertMapper.class);
    }

    @Bean
    public FollowUpRecordMapper followUpRecordMapper() {
        return Mockito.mock(FollowUpRecordMapper.class);
    }

    @Bean
    public InterventionRecordMapper interventionRecordMapper() {
        return Mockito.mock(InterventionRecordMapper.class);
    }

    @Bean
    public HealthAssessmentMapper healthAssessmentMapper() {
        return Mockito.mock(HealthAssessmentMapper.class);
    }
}
