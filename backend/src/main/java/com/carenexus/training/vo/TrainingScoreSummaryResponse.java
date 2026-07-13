package com.carenexus.training.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TrainingScoreSummaryResponse {

    private Integer courseCount;
    private Integer passedCourseCount;
    private BigDecimal averageScore;
    private Boolean trainingPassed;
    private List<CourseScoreResponse> courseScores = new ArrayList<>();

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public Integer getPassedCourseCount() {
        return passedCourseCount;
    }

    public void setPassedCourseCount(Integer passedCourseCount) {
        this.passedCourseCount = passedCourseCount;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public Boolean getTrainingPassed() {
        return trainingPassed;
    }

    public void setTrainingPassed(Boolean trainingPassed) {
        this.trainingPassed = trainingPassed;
    }

    public List<CourseScoreResponse> getCourseScores() {
        return courseScores;
    }

    public void setCourseScores(List<CourseScoreResponse> courseScores) {
        this.courseScores = courseScores;
    }
}
