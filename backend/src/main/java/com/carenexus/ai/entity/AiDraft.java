package com.carenexus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("ai_draft")
public class AiDraft {
    private Long id;
    private String draftType;
    private String prompt;
    private String draftContent;
    private String draftStatus;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewComment;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long value) { this.id = value; }

    public String getDraftType() { return draftType; }

    public void setDraftType(String value) { this.draftType = value; }

    public String getPrompt() { return prompt; }

    public void setPrompt(String value) { this.prompt = value; }

    public String getDraftContent() { return draftContent; }

    public void setDraftContent(String value) { this.draftContent = value; }

    public String getDraftStatus() { return draftStatus; }

    public void setDraftStatus(String value) { this.draftStatus = value; }

    public Long getReviewedBy() { return reviewedBy; }

    public void setReviewedBy(Long value) { this.reviewedBy = value; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }

    public void setReviewedAt(LocalDateTime value) { this.reviewedAt = value; }

    public String getReviewComment() { return reviewComment; }

    public void setReviewComment(String value) { this.reviewComment = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }
}
