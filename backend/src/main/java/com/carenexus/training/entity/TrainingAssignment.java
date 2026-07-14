package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("training_assignment")
public class TrainingAssignment {
    private Long id;
    private Long resourceId;
    private String title;
    private String assignmentType;
    private String content;
    private String optionsJson;
    private String standardAnswer;
    private String assignmentStatus;
    private Long createdBy;
    private LocalDateTime dueAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long value) { id = value; }

    public Long getResourceId() { return resourceId; }

    public void setResourceId(Long value) { resourceId = value; }

    public String getTitle() { return title; }

    public void setTitle(String value) { title = value; }

    public String getAssignmentType() { return assignmentType; }

    public void setAssignmentType(String value) { assignmentType = value; }

    public String getContent() { return content; }

    public void setContent(String value) { content = value; }

    public String getOptionsJson() { return optionsJson; }

    public void setOptionsJson(String value) { optionsJson = value; }

    public String getStandardAnswer() { return standardAnswer; }

    public void setStandardAnswer(String value) { standardAnswer = value; }

    public String getAssignmentStatus() { return assignmentStatus; }

    public void setAssignmentStatus(String value) { assignmentStatus = value; }

    public Long getCreatedBy() { return createdBy; }

    public void setCreatedBy(Long value) { createdBy = value; }

    public LocalDateTime getDueAt() { return dueAt; }

    public void setDueAt(LocalDateTime value) { dueAt = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}

