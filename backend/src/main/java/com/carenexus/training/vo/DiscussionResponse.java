package com.carenexus.training.vo;

import java.time.LocalDateTime;

public class DiscussionResponse {
    public Long id;
    public Long resourceId;
    public String title;
    public String content;
    public String authorName;
    public Integer replyCount;
    public LocalDateTime createdAt;
}
