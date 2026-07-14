package com.carenexus.training.vo;

import java.time.LocalDateTime;

public class DiscussionResponse {
    public String id;
    public Long resourceId;
    public String title;
    public String content;
    public String authorName;
    public Integer replyCount;
    public Long likeCount;
    public boolean liked;
    public boolean ownedByCurrentUser;
    public LocalDateTime createdAt;
}
