package com.carenexus.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.training.entity.ExamAnswer;
import com.carenexus.training.vo.MistakeQuestionResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamAnswerMapper extends BaseMapper<ExamAnswer> {

    @Select("SELECT ea.id AS answerId, q.id AS questionId, q.question_type AS questionType, "
            + "q.question_content AS questionContent, ea.user_answer AS userAnswer, "
            + "q.standard_answer AS standardAnswer, q.analysis, ea.score, er.submitted_at AS submittedAt "
            + "FROM exam_answer ea JOIN exam_record er ON er.id = ea.exam_record_id "
            + "JOIN training_exam e ON e.id = er.exam_id "
            + "JOIN exam_question q ON q.id = ea.question_id AND q.is_deleted = 0 "
            + "WHERE er.user_id = #{userId} AND e.resource_id = #{resourceId} AND ea.score = 0 "
            + "AND er.attempt_no = (SELECT MAX(er2.attempt_no) FROM exam_record er2 "
            + "WHERE er2.user_id = er.user_id AND er2.exam_id = er.exam_id) "
            + "ORDER BY er.submitted_at DESC, ea.id")
    List<MistakeQuestionResponse> selectLatestMistakes(Long userId, Long resourceId);
}
