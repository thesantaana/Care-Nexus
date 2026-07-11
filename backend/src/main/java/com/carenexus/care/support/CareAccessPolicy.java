package com.carenexus.care.support;

import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.PermissionService;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class CareAccessPolicy {

    private final CurrentUserService currentUserService;
    private final PermissionService permissionService;

    public CareAccessPolicy(CurrentUserService currentUserService, PermissionService permissionService) {
        this.currentUserService = currentUserService;
        this.permissionService = permissionService;
    }

    public CurrentUser requireCurrentUser() {
        return currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required"));
    }

    public CurrentUser requirePermission(String permissionCode) {
        CurrentUser currentUser = requireCurrentUser();
        if (!permissionService.hasPermission(currentUser, permissionCode)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Permission denied");
        }
        return currentUser;
    }

    public void requireRole(CurrentUser currentUser, String roleCode) {
        if (currentUser == null || !roleCode.equals(currentUser.getMainRoleCode())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Role is not allowed");
        }
    }
}
