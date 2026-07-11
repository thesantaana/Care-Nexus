package com.carenexus.doctor.support;

import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.PermissionService;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.doctor.DoctorAuthorizationService;
import com.carenexus.doctor.constant.DoctorConstants;
import org.springframework.stereotype.Component;

@Component
public class DoctorAccessPolicy {

    private final CurrentUserService currentUserService;
    private final PermissionService permissionService;
    private final DoctorAuthorizationService authorizationService;

    public DoctorAccessPolicy(CurrentUserService currentUserService, PermissionService permissionService,
            DoctorAuthorizationService authorizationService) {
        this.currentUserService = currentUserService;
        this.permissionService = permissionService;
        this.authorizationService = authorizationService;
    }

    public CurrentUser requireView() {
        return requirePermission(DoctorConstants.PERMISSION_ELDER_VIEW);
    }

    public CurrentUser requireHealthManage() {
        return requirePermission(DoctorConstants.PERMISSION_HEALTH_MANAGE);
    }

    public CurrentUser requireAuthorize() {
        return requirePermission(DoctorConstants.PERMISSION_ELDER_AUTHORIZE);
    }

    public CurrentUser requireElderAccess(Long elderId) {
        CurrentUser currentUser = requireView();
        requireAuthorization(currentUser, elderId);
        return currentUser;
    }

    public CurrentUser requireHealthAccess(Long elderId) {
        CurrentUser currentUser = requireHealthManage();
        requireAuthorization(currentUser, elderId);
        return currentUser;
    }

    private void requireAuthorization(CurrentUser currentUser, Long elderId) {
        if (!authorizationService.canAccessElder(currentUser, elderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Elder is outside current doctor scope");
        }
    }

    private CurrentUser requirePermission(String permission) {
        CurrentUser currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required"));
        if (!permissionService.hasPermission(currentUser, permission)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Permission denied");
        }
        return currentUser;
    }
}
