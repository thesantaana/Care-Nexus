package com.carenexus.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.training.entity.TrainingNote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainingNoteMapper extends BaseMapper<TrainingNote> {
}
