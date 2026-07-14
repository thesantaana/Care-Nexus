package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("training_discussion")
public class TrainingDiscussion {
    private Long id;
    private Long resourceId;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long value) { id = value; }

    public Long getResourceId() { return resourceId; }

    public void setResourceId(Long value) { resourceId = value; }

    public Long getUserId() { return userId; }

    public void setUserId(Long value) { userId = value; }

    public String getTitle() { return title; }

    public void setTitle(String value) { title = value; }

    public String getContent() { return content; }

    public void setContent(String value) { content = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}

