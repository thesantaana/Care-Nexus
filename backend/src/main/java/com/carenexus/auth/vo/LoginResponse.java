package com.carenexus.auth.vo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private Instant expiresAt;
    private Long userId;
    private String username;
    private String displayName;
    private String mainRoleCode;
    private String mainRoleName;
    private List<String> permissionCodes = new ArrayList<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMainRoleCode() {
        return mainRoleCode;
    }

    public void setMainRoleCode(String mainRoleCode) {
        this.mainRoleCode = mainRoleCode;
    }

    public String getMainRoleName() {
        return mainRoleName;
    }

    public void setMainRoleName(String mainRoleName) {
        this.mainRoleName = mainRoleName;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }
}
