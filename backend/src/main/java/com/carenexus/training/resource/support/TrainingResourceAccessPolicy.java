package com.carenexus.training.resource.support;

import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.PermissionService;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.constant.TrainingPermissions;
import org.springframework.stereotype.Component;

@Component
public class TrainingResourceAccessPolicy {

    private final CurrentUserService currentUserService;
    private final PermissionService permissionService;

    public TrainingResourceAccessPolicy(CurrentUserService currentUserService, PermissionService permissionService) {
        this.currentUserService = currentUserService;
        this.permissionService = permissionService;
    }

    public CurrentUser requireViewOrManage() {
        CurrentUser currentUser = requireCurrentUser();
        if (!hasView(currentUser) && !hasManage(currentUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Permission denied");
        }
        return currentUser;
    }

    public CurrentUser requireManage() {
        CurrentUser currentUser = requireCurrentUser();
        if (!hasManage(currentUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Permission denied");
        }
        return currentUser;
    }

    public boolean hasManage(CurrentUser currentUser) {
        return permissionService.hasPermission(currentUser, TrainingPermissions.RESOURCE_MANAGE);
    }

    private boolean hasView(CurrentUser currentUser) {
        return permissionService.hasPermission(currentUser, TrainingPermissions.RESOURCE_VIEW);
    }

    private CurrentUser requireCurrentUser() {
        return currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required"));
    }
}
