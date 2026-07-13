package com.carenexus.training.exam.service;

import com.carenexus.auth.CurrentUser;
import com.carenexus.training.mapper.ExamRecordMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.CaregiverCourseScoreRow;
import com.carenexus.training.vo.CaregiverTrainingScoreResponse;
import com.carenexus.training.vo.CourseScoreResponse;
import com.carenexus.training.vo.TrainingScoreSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TrainingScoreService {

    private final TrainingResourceAccessPolicy accessPolicy;
    private final ExamRecordMapper examRecordMapper;

    public TrainingScoreService(TrainingResourceAccessPolicy accessPolicy, ExamRecordMapper examRecordMapper) {
        this.accessPolicy = accessPolicy;
        this.examRecordMapper = examRecordMapper;
    }

    public TrainingScoreSummaryResponse myCourseScores() {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        List<CourseScoreResponse> scores = examRecordMapper.selectCourseScores(currentUser.getUserId());
        BigDecimal totalScore = BigDecimal.ZERO;
        int passedCount = 0;
        for (CourseScoreResponse score : scores) {
            boolean passed = score.getExamId() != null
                    && score.getBestScore().compareTo(score.getPassScore()) >= 0;
            score.setPassed(passed);
            totalScore = totalScore.add(score.getBestScore());
            if (passed) {
                passedCount++;
            }
        }
        TrainingScoreSummaryResponse response = new TrainingScoreSummaryResponse();
        response.setCourseCount(scores.size());
        response.setPassedCourseCount(passedCount);
        response.setAverageScore(scores.isEmpty() ? BigDecimal.ZERO
                : totalScore.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP));
        response.setTrainingPassed(!scores.isEmpty() && passedCount == scores.size());
        response.setCourseScores(scores);
        return response;
    }

    public List<CaregiverTrainingScoreResponse> caregiverScores() {
        accessPolicy.requireManage();
        Map<Long, List<CaregiverCourseScoreRow>> grouped = new LinkedHashMap<>();
        for (CaregiverCourseScoreRow row : examRecordMapper.selectAllCaregiverCourseScores()) {
            grouped.computeIfAbsent(row.getUserId(), key -> new ArrayList<>()).add(row);
        }
        List<CaregiverTrainingScoreResponse> responses = new ArrayList<>();
        for (List<CaregiverCourseScoreRow> rows : grouped.values()) {
            responses.add(toCaregiverScore(rows));
        }
        return responses;
    }

    private CaregiverTrainingScoreResponse toCaregiverScore(List<CaregiverCourseScoreRow> rows) {
        CaregiverCourseScoreRow first = rows.get(0);
        BigDecimal total = BigDecimal.ZERO;
        int passed = 0;
        List<CourseScoreResponse> courseScores = new ArrayList<>();
        for (CaregiverCourseScoreRow row : rows) {
            boolean coursePassed = row.calculatePassed();
            row.setPassed(coursePassed);
            total = total.add(row.getBestScore());
            if (coursePassed) {
                passed++;
            }
            courseScores.add(row);
        }
        CaregiverTrainingScoreResponse response = new CaregiverTrainingScoreResponse();
        response.setUserId(first.getUserId());
        response.setDisplayName(first.getDisplayName());
        response.setCourseCount(rows.size());
        response.setPassedCourseCount(passed);
        response.setAverageScore(total.divide(BigDecimal.valueOf(rows.size()), 2, RoundingMode.HALF_UP));
        response.setTrainingPassed(passed == rows.size());
        response.setCourseScores(courseScores);
        return response;
    }
}
