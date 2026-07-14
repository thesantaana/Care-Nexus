package com.carenexus.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class DiscussionReplyRequest {
    @NotBlank
    @Size(max = 3000)
    private String content;
    private Long parentReplyId;

    public String getContent() { return content; }

    public void setContent(String value) { content = value; }

    public Long getParentReplyId() { return parentReplyId; }

    public void setParentReplyId(Long value) { parentReplyId = value; }
}

