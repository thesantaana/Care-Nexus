package com.carenexus.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.training.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    @Select("SELECT COALESCE(MAX(attempt_no), 0) FROM exam_record WHERE user_id = #{userId} AND exam_id = #{examId}")
    int selectMaxAttemptNo(Long userId, Long examId);
}
