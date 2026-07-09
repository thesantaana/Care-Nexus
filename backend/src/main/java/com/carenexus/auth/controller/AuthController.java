package com.carenexus.auth.controller;

import com.carenexus.auth.dto.LoginRequest;
import com.carenexus.auth.service.AuthService;
import com.carenexus.auth.vo.CurrentUserResponse;
import com.carenexus.auth.vo.LoginResponse;
import com.carenexus.common.response.ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me() {
        return ApiResponse.success(authService.currentUser());
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Boolean>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        Map<String, Boolean> data = new LinkedHashMap<>();
        data.put("success", true);
        return ApiResponse.success(data);
    }

    @GetMapping("/rbac-check")
    @PreAuthorize("@permissionService.hasPermission(authentication.principal, 'system:user:view')")
    public ApiResponse<Map<String, String>> rbacCheck() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("permission", "system:user:view");
        data.put("result", "allowed");
        return ApiResponse.success(data);
    }
}
