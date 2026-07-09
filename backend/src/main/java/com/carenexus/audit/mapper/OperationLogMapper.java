package com.carenexus.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.audit.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
