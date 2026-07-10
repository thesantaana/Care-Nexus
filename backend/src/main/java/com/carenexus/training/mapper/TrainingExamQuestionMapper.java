package com.carenexus.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.training.entity.TrainingExamQuestion;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrainingExamQuestionMapper extends BaseMapper<TrainingExamQuestion> {

    @Delete("DELETE FROM training_exam_question WHERE exam_id = #{examId}")
    int deleteByExamId(Long examId);

    @Select("SELECT * FROM training_exam_question WHERE exam_id = #{examId} ORDER BY sort_no ASC, id ASC")
    List<TrainingExamQuestion> selectByExamId(Long examId);
}
