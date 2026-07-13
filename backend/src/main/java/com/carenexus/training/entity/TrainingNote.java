package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("training_note")
public class TrainingNote {
    private Long id;
    private Long userId;
    private Long resourceId;
    private String noteTitle;
    private String noteContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }

    public void setId(Long value) { id = value; }

    public Long getUserId() { return userId; }

    public void setUserId(Long value) { userId = value; }

    public Long getResourceId() { return resourceId; }

    public void setResourceId(Long value) { resourceId = value; }

    public String getNoteTitle() { return noteTitle; }

    public void setNoteTitle(String value) { noteTitle = value; }

    public String getNoteContent() { return noteContent; }

    public void setNoteContent(String value) { noteContent = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { createdAt = value; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime value) { updatedAt = value; }
}
