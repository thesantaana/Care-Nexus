package com.carenexus.auth;

public interface PermissionService {

    boolean hasPermission(CurrentUser currentUser, String permissionCode);
}
