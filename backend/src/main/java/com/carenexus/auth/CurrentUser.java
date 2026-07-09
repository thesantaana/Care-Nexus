package com.carenexus.auth;

public class CurrentUser {

    private Long userId;
    private String username;
    private String mainRole;

    public CurrentUser() {
    }

    public CurrentUser(Long userId, String username, String mainRole) {
        this.userId = userId;
        this.username = username;
        this.mainRole = mainRole;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getMainRole() {
        return mainRole;
    }
}
