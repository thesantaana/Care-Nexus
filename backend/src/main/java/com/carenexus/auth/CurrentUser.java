package com.carenexus.auth;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CurrentUser {

    private Long userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String mainRoleCode;
    private String mainRoleName;
    private String accountStatus;
    private Set<String> permissionCodes = new LinkedHashSet<>();

    public CurrentUser() {
    }

    public CurrentUser(Long userId, String username, String displayName, String mainRoleCode,
            String mainRoleName, String accountStatus, Set<String> permissionCodes) {
        this(userId, username, displayName, "/assets/default-avatar.png", mainRoleCode, mainRoleName,
                accountStatus, permissionCodes);
    }

    public CurrentUser(Long userId, String username, String displayName, String avatarUrl, String mainRoleCode,
            String mainRoleName, String accountStatus, Set<String> permissionCodes) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.mainRoleCode = mainRoleCode;
        this.mainRoleName = mainRoleName;
        this.accountStatus = accountStatus;
        if (permissionCodes != null) {
            this.permissionCodes = new LinkedHashSet<>(permissionCodes);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getMainRoleCode() {
        return mainRoleCode;
    }

    public String getMainRoleName() {
        return mainRoleName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public Set<String> getPermissionCodes() {
        return Collections.unmodifiableSet(permissionCodes);
    }
}
