package com.carenexus.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.doctor.entity.HealthAlert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthAlertMapper extends BaseMapper<HealthAlert> {
}
