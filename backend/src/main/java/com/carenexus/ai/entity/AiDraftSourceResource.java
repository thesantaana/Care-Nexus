package com.carenexus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("ai_draft_source_resource")
public class AiDraftSourceResource {
    private Long id;
    private Long draftId;
    private Long resourceId;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long value) { this.id = value; }

    public Long getDraftId() { return draftId; }

    public void setDraftId(Long value) { this.draftId = value; }

    public Long getResourceId() { return resourceId; }

    public void setResourceId(Long value) { this.resourceId = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }
}
