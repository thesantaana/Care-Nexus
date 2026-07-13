package com.carenexus.training;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carenexus.auth.CurrentUser;
import com.carenexus.training.exam.service.TrainingScoreService;
import com.carenexus.training.mapper.ExamRecordMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.CourseScoreResponse;
import com.carenexus.training.vo.TrainingScoreSummaryResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainingScoreServiceTest {

    private ExamRecordMapper examRecordMapper;
    private TrainingScoreService service;

    @BeforeEach
    void setUp() {
        TrainingResourceAccessPolicy accessPolicy = mock(TrainingResourceAccessPolicy.class);
        when(accessPolicy.requireViewOrManage()).thenReturn(new CurrentUser(2L, "caregiver", "护工",
                "CAREGIVER", "护工", "ACTIVE", Collections.emptySet()));
        examRecordMapper = mock(ExamRecordMapper.class);
        service = new TrainingScoreService(accessPolicy, examRecordMapper);
    }

    @Test
    void allCoursesPassedProducesAverageAndTrainingPass() {
        when(examRecordMapper.selectCourseScores(2L)).thenReturn(Arrays.asList(score(80), score(70), score(90)));

        TrainingScoreSummaryResponse result = service.myCourseScores();

        assertEquals(new BigDecimal("80.00"), result.getAverageScore());
        assertEquals(Integer.valueOf(3), result.getPassedCourseCount());
        assertTrue(result.getTrainingPassed());
    }

    @Test
    void oneFailedCoursePreventsTrainingPass() {
        when(examRecordMapper.selectCourseScores(2L)).thenReturn(Arrays.asList(score(80), score(59)));

        TrainingScoreSummaryResponse result = service.myCourseScores();

        assertEquals(Integer.valueOf(1), result.getPassedCourseCount());
        assertFalse(result.getTrainingPassed());
    }

    private CourseScoreResponse score(int value) {
        CourseScoreResponse score = new CourseScoreResponse();
        score.setResourceId((long) value);
        score.setResourceTitle("课程" + value);
        score.setExamId((long) value);
        score.setPassScore(new BigDecimal("60.00"));
        score.setBestScore(BigDecimal.valueOf(value));
        return score;
    }
}
