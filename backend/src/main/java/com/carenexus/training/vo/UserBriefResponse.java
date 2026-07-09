package com.carenexus.training.vo;

public class UserBriefResponse {

    private Long id;
    private String username;
    private String displayName;

    public UserBriefResponse() {
    }

    public UserBriefResponse(Long id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
}
