package com.carenexus.training.vo;

import java.math.BigDecimal;

public class CaregiverCourseScoreRow extends CourseScoreResponse {

    private Long userId;
    private String displayName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean calculatePassed() {
        BigDecimal best = getBestScore() == null ? BigDecimal.ZERO : getBestScore();
        BigDecimal pass = getPassScore() == null ? new BigDecimal("60.00") : getPassScore();
        return getExamId() != null && best.compareTo(pass) >= 0;
    }
}
