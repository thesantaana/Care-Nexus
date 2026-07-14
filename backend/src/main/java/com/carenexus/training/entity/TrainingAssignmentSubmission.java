package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("training_assignment_submission")
public class TrainingAssignmentSubmission {
    private Long id;
    private Long assignmentId;
    private Long userId;
    private String answerContent;
    private BigDecimal score;
    private String submissionStatus;
    private LocalDateTime submittedAt;

    public Long getId() { return id; }

    public void setId(Long value) { id = value; }

    public Long getAssignmentId() { return assignmentId; }

    public void setAssignmentId(Long value) { assignmentId = value; }

    public Long getUserId() { return userId; }

    public void setUserId(Long value) { userId = value; }

    public String getAnswerContent() { return answerContent; }

    public void setAnswerContent(String value) { answerContent = value; }

    public BigDecimal getScore() { return score; }

    public void setScore(BigDecimal value) { score = value; }

    public String getSubmissionStatus() { return submissionStatus; }

    public void setSubmissionStatus(String value) { submissionStatus = value; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }

    public void setSubmittedAt(LocalDateTime value) { submittedAt = value; }
}

