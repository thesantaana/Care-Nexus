package com.carenexus.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.training.entity.ExamRecord;
import com.carenexus.training.vo.CourseScoreResponse;
import com.carenexus.training.vo.CaregiverCourseScoreRow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    @Select("SELECT COALESCE(MAX(attempt_no), 0) FROM exam_record WHERE user_id = #{userId} AND exam_id = #{examId}")
    int selectMaxAttemptNo(Long userId, Long examId);

    @Select("SELECT r.id AS resourceId, r.title AS resourceTitle, e.id AS examId, "
            + "COALESCE(e.pass_score, 60.00) AS passScore, COALESCE(MAX(er.score), 0.00) AS bestScore "
            + "FROM training_resource r "
            + "LEFT JOIN training_exam e ON e.resource_id = r.id AND e.exam_status = 'PUBLISHED' "
            + "AND e.is_deleted = 0 "
            + "LEFT JOIN exam_record er ON er.exam_id = e.id AND er.user_id = #{userId} "
            + "WHERE r.resource_status = 'PUBLISHED' AND r.is_deleted = 0 "
            + "GROUP BY r.id, r.title, e.id, e.pass_score ORDER BY r.id")
    List<CourseScoreResponse> selectCourseScores(Long userId);

    @Select("SELECT u.id AS userId, u.real_name AS displayName, r.id AS resourceId, "
            + "r.title AS resourceTitle, e.id AS examId, COALESCE(e.pass_score, 60.00) AS passScore, "
            + "COALESCE(MAX(er.score), 0.00) AS bestScore FROM sys_user u "
            + "JOIN sys_role role ON role.id = u.main_role_id AND role.role_code = 'CAREGIVER' "
            + "CROSS JOIN training_resource r "
            + "LEFT JOIN training_exam e ON e.resource_id = r.id AND e.exam_status = 'PUBLISHED' "
            + "AND e.is_deleted = 0 LEFT JOIN exam_record er ON er.exam_id = e.id AND er.user_id = u.id "
            + "WHERE u.account_status = 'NORMAL' AND u.is_deleted = 0 "
            + "AND r.resource_status = 'PUBLISHED' AND r.is_deleted = 0 "
            + "GROUP BY u.id, u.real_name, r.id, r.title, e.id, e.pass_score ORDER BY u.id, r.id")
    List<CaregiverCourseScoreRow> selectAllCaregiverCourseScores();
}
