package com.carenexus.auth.vo;

import java.util.ArrayList;
import java.util.List;

public class CurrentUserResponse {

    private Long userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String mainRoleCode;
    private String mainRoleName;
    private List<String> permissionCodes = new ArrayList<>();
    private String accountStatus;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
