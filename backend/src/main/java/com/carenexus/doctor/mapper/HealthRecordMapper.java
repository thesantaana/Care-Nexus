package com.carenexus.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.doctor.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
