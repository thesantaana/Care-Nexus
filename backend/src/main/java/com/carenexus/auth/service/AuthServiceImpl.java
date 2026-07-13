package com.carenexus.auth.service;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.TokenService;
import com.carenexus.auth.dto.LoginRequest;
import com.carenexus.auth.entity.SysUser;
import com.carenexus.auth.token.JwtProperties;
import com.carenexus.auth.token.TokenClaims;
import com.carenexus.auth.vo.CurrentUserResponse;
import com.carenexus.auth.vo.LoginResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import java.time.Duration;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthUserService authUserService;
    private final CurrentUserService currentUserService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final OperationLogService operationLogService;

    public AuthServiceImpl(AuthUserService authUserService, CurrentUserService currentUserService,
            TokenService tokenService, PasswordEncoder passwordEncoder, JwtProperties jwtProperties,
            OperationLogService operationLogService) {
        this.authUserService = authUserService;
        this.currentUserService = currentUserService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
        this.operationLogService = operationLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        SysUser rawUser = authUserService.loadRawByUsername(request.getUsername());
        if (rawUser == null || !passwordMatches(request.getPassword(), rawUser.getPasswordHash())) {
            LOGGER.info("Login failed for username={}, reason=invalid_credentials", maskUsername(request.getUsername()));
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Username or password is incorrect");
        }
        CurrentUser currentUser = authUserService.loadByUsernameForLogin(request.getUsername());
        Duration expiresIn = Duration.ofSeconds(jwtProperties.getExpirationSeconds());
        String token = tokenService.createToken(currentUser, expiresIn);
        TokenClaims claims = tokenService.parseToken(token);
        operationLogService.record(currentUser, "LOGIN", "ACCOUNT", currentUser.getUserId(), "SUCCESS");
        LOGGER.info("Login success for userId={}", currentUser.getUserId());
        return toLoginResponse(currentUser, token, expiresIn.getSeconds(), claims);
    }

    @Override
    public CurrentUserResponse currentUser() {
        CurrentUser currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_UNAUTHORIZED, "Authentication required"));
        return toCurrentUserResponse(currentUser);
    }

    @Override
    public void logout(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        CurrentUser currentUser = currentUserService.getCurrentUser().orElse(null);
        tokenService.revokeToken(token);
        operationLogService.record(currentUser, "LOGOUT", "ACCOUNT",
                currentUser == null ? null : currentUser.getUserId(), "SUCCESS");
    }

    private LoginResponse toLoginResponse(CurrentUser currentUser, String token, Long expiresIn, TokenClaims claims) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(expiresIn);
        response.setExpiresAt(claims.getExpiresAt());
        response.setUserId(currentUser.getUserId());
        response.setUsername(currentUser.getUsername());
        response.setDisplayName(currentUser.getDisplayName());
        response.setAvatarUrl(currentUser.getAvatarUrl());
        response.setMainRoleCode(currentUser.getMainRoleCode());
        response.setMainRoleName(currentUser.getMainRoleName());
        response.setPermissionCodes(new ArrayList<>(currentUser.getPermissionCodes()));
        return response;
    }

    private CurrentUserResponse toCurrentUserResponse(CurrentUser currentUser) {
        CurrentUserResponse response = new CurrentUserResponse();
        response.setUserId(currentUser.getUserId());
        response.setUsername(currentUser.getUsername());
        response.setDisplayName(currentUser.getDisplayName());
        response.setAvatarUrl(currentUser.getAvatarUrl());
        response.setMainRoleCode(currentUser.getMainRoleCode());
        response.setMainRoleName(currentUser.getMainRoleName());
        response.setPermissionCodes(new ArrayList<>(currentUser.getPermissionCodes()));
        response.setAccountStatus(currentUser.getAccountStatus());
        return response;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_MISSING, "Bearer token is required");
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    private boolean passwordMatches(String rawPassword, String passwordHash) {
        try {
            return passwordEncoder.matches(rawPassword, passwordHash);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private String maskUsername(String username) {
        if (!StringUtils.hasText(username) || username.length() <= 2) {
            return "***";
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
}
