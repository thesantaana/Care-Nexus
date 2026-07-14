package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("training_discussion_reply_like")
public class TrainingDiscussionReplyLike {
    private Long id;
    private Long replyId;
    private Long userId;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long value) { id = value; }

    public Long getReplyId() { return replyId; }

    public void setReplyId(Long value) { replyId = value; }

    public Long getUserId() { return userId; }

    public void setUserId(Long value) { userId = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}
