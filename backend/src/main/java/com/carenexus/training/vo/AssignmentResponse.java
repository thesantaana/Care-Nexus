package com.carenexus.training.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssignmentResponse {
    public Long id;
    public Long resourceId;
    public String title;
    public String type;
    public String content;
    public String optionsJson;
    public LocalDateTime dueAt;
    public String submissionStatus;
    public String submittedAnswer;
    public BigDecimal score;
}
