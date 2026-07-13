package com.carenexus.training.exam.service;

import com.carenexus.auth.CurrentUser;
import com.carenexus.training.mapper.ExamRecordMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.CourseScoreResponse;
import com.carenexus.training.vo.TrainingScoreSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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
}
