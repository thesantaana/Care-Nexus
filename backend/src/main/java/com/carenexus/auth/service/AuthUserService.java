package com.carenexus.auth.service;

import com.carenexus.auth.AccountStatus;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.PermissionService;
import com.carenexus.auth.entity.SysRole;
import com.carenexus.auth.entity.SysUser;
import com.carenexus.auth.mapper.SysPermissionMapper;
import com.carenexus.auth.mapper.SysRoleMapper;
import com.carenexus.auth.mapper.SysUserMapper;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("permissionService")
public class AuthUserService implements CurrentUserService, PermissionService {

    private static final String ROLE_ENABLED = "ENABLED";

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    public AuthUserService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper,
            SysPermissionMapper sysPermissionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public Optional<CurrentUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
            return Optional.empty();
        }
        return Optional.of((CurrentUser) authentication.getPrincipal());
    }

    @Override
    public Long requireCurrentUserId() {
        return getCurrentUser()
                .map(CurrentUser::getUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_UNAUTHORIZED, "Authentication required"));
    }

    @Override
    public CurrentUser loadActiveUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        return toCurrentUser(user, false);
    }

    public CurrentUser loadByUsernameForLogin(String username) {
        SysUser user = sysUserMapper.selectByUsernameIncludingDeleted(username);
        return toCurrentUser(user, true);
    }

    public SysUser loadRawByUsername(String username) {
        return sysUserMapper.selectByUsernameIncludingDeleted(username);
    }

    @Override
    public boolean hasPermission(CurrentUser currentUser, String permissionCode) {
        return currentUser != null && currentUser.getPermissionCodes().contains(permissionCode);
    }

    private CurrentUser toCurrentUser(SysUser user, boolean loginContext) {
        if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
            throw new BusinessException(loginContext ? ErrorCode.AUTH_INVALID_CREDENTIALS : ErrorCode.AUTH_UNAUTHORIZED,
                    loginContext ? "Username or password is incorrect" : "Authentication required");
        }
        if (!AccountStatus.NORMAL.equals(user.getAccountStatus())) {
            throw new BusinessException(loginContext ? ErrorCode.AUTH_INVALID_CREDENTIALS : ErrorCode.AUTH_UNAUTHORIZED,
                    loginContext ? "Username or password is incorrect" : "Authentication required");
        }
        SysRole role = sysRoleMapper.selectById(user.getMainRoleId());
        if (role == null || Integer.valueOf(1).equals(role.getIsDeleted()) || !ROLE_ENABLED.equals(role.getRoleStatus())) {
            throw new BusinessException(ErrorCode.AUTH_FORBIDDEN, "Role is not available");
        }
        Set<String> permissionCodes = new LinkedHashSet<>(sysPermissionMapper.selectPermissionCodesByRoleId(role.getId()));
        return new CurrentUser(user.getId(), user.getUsername(), user.getRealName(), role.getRoleCode(),
                role.getRoleName(), user.getAccountStatus(), permissionCodes);
    }
}
