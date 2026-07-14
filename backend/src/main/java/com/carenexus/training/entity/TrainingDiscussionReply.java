package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("training_discussion_reply")
public class TrainingDiscussionReply {
    private Long id;
    private Long discussionId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long value) { id = value; }

    public Long getDiscussionId() { return discussionId; }

    public void setDiscussionId(Long value) { discussionId = value; }

    public Long getUserId() { return userId; }

    public void setUserId(Long value) { userId = value; }

    public String getContent() { return content; }

    public void setContent(String value) { content = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}

