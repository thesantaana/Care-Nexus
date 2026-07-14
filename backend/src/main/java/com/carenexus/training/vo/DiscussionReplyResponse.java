package com.carenexus.training.vo;

import java.time.LocalDateTime;

public class DiscussionReplyResponse {
    public String id;
    public String content;
    public String authorName;
    public String parentReplyId;
    public String replyToAuthorName;
    public Long likeCount;
    public boolean liked;
    public boolean ownedByCurrentUser;
    public LocalDateTime createdAt;
}
