package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("learning_access_log")
public class LearningAccessLog {

    private Long id;
    private Long userId;
    private Long resourceId;
    private Integer accessSeconds;
    private LocalDateTime accessedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAccessSeconds() {
        return accessSeconds;
    }

    public void setAccessSeconds(Integer accessSeconds) {
        this.accessSeconds = accessSeconds;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(LocalDateTime accessedAt) {
        this.accessedAt = accessedAt;
    }
}
