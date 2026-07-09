package com.carenexus.auth;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carenexus.audit.mapper.OperationLogMapper;
import com.carenexus.auth.entity.SysRole;
import com.carenexus.auth.entity.SysUser;
import com.carenexus.auth.mapper.SysPermissionMapper;
import com.carenexus.auth.mapper.SysRoleMapper;
import com.carenexus.auth.mapper.SysUserMapper;
import com.carenexus.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
        "care-nexus.jwt.secret=test-demo-secret-for-t012-auth-rbac-32",
        "care-nexus.jwt.expiration-seconds=3600"
})
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    private static final String PASSWORD = "Demo@123456";
    private static final String LOGIN_JSON = "{\"username\":\"admin_demo\",\"password\":\"Demo@123456\"}";
    private static final String WRONG_PASSWORD_JSON = "{\"username\":\"admin_demo\",\"password\":\"wrong\"}";
    private static final String MISSING_USER_JSON = "{\"username\":\"missing_demo\",\"password\":\"Demo@123456\"}";
    private static final String DISABLED_USER_JSON = "{\"username\":\"disabled_demo\",\"password\":\"Demo@123456\"}";
    private static final String DELETED_USER_JSON = "{\"username\":\"deleted_demo\",\"password\":\"Demo@123456\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CurrentUserService currentUserService;

    @MockBean
    private SysUserMapper sysUserMapper;

    @MockBean
    private SysRoleMapper sysRoleMapper;

    @MockBean
    private SysPermissionMapper sysPermissionMapper;

    @MockBean
    private OperationLogMapper operationLogMapper;

    @BeforeEach
    void setUp() {
        SysRole adminRole = role(1L, "ADMIN", "管理员", "ENABLED", 0);
        SysRole elderRole = role(2L, "ELDER", "老人", "ENABLED", 0);
        when(sysRoleMapper.selectById(1L)).thenReturn(adminRole);
        when(sysRoleMapper.selectById(2L)).thenReturn(elderRole);
        when(sysPermissionMapper.selectPermissionCodesByRoleId(1L)).thenReturn(adminPermissions());
        when(sysPermissionMapper.selectPermissionCodesByRoleId(2L)).thenReturn(Collections.emptyList());
        when(sysUserMapper.selectByUsernameIncludingDeleted("admin_demo")).thenReturn(user(1L, "admin_demo", 1L, "NORMAL", 0));
        when(sysUserMapper.selectByUsernameIncludingDeleted("disabled_demo"))
                .thenReturn(user(2L, "disabled_demo", 2L, "DISABLED", 0));
        when(sysUserMapper.selectByUsernameIncludingDeleted("deleted_demo"))
                .thenReturn(user(3L, "deleted_demo", 2L, "NORMAL", 1));
        when(sysUserMapper.selectByUsernameIncludingDeleted("broken_hash_demo"))
                .thenReturn(user(4L, "broken_hash_demo", 2L, "NORMAL", 0, "invalid-hash"));
        when(sysUserMapper.selectById(1L)).thenReturn(user(1L, "admin_demo", 1L, "NORMAL", 0));
        when(sysUserMapper.selectById(2L)).thenReturn(user(2L, "disabled_demo", 2L, "DISABLED", 0));
        when(sysUserMapper.selectById(3L)).thenReturn(user(3L, "deleted_demo", 2L, "NORMAL", 1));
        when(operationLogMapper.insert(any())).thenReturn(1);
    }

    @Test
    void loginSuccessReturnsTokenAndPermissions() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.mainRoleCode").value("ADMIN"))
                .andExpect(jsonPath("$.data.permissionCodes", hasItem("system:user:view")));
    }

    @Test
    void loginIgnoresMalformedAuthorizationHeaderBecauseItIsAnonymous() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .header("Authorization", "Basic wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    void loginResponseDoesNotReturnPasswordHash() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void tokenResponseContainsExpirationFields() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.data.expiresAt").isString());
    }

    @Test
    void wrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WRONG_PASSWORD_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Username or password is incorrect"));
    }

    @Test
    void missingAccountReturnsSamePublicMessageAsWrongPassword() throws Exception {
        String missingMessage = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_USER_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        String wrongPasswordMessage = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WRONG_PASSWORD_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<?> missing = objectMapper.readValue(missingMessage, ApiResponse.class);
        ApiResponse<?> wrongPassword = objectMapper.readValue(wrongPasswordMessage, ApiResponse.class);
        org.junit.jupiter.api.Assertions.assertEquals(wrongPassword.getMessage(), missing.getMessage());
    }

    @Test
    void disabledAccountReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DISABLED_USER_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletedAccountReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DELETED_USER_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformedStoredPasswordHashReturnsUnauthorizedInsteadOfServerError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"broken_hash_demo\",\"password\":\"Demo@123456\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meReturnsCurrentUserWithRoleAndPermissions() throws Exception {
        String token = loginToken();
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.mainRoleCode").value("ADMIN"))
                .andExpect(jsonPath("$.data.accountStatus").value("NORMAL"))
                .andExpect(jsonPath("$.data.permissionCodes", hasItem("system:user:view")));
    }

    @Test
    void meWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongBearerFormatReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Basic abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void forgedTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer("forged.token.value")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongSignatureTokenReturnsUnauthorized() throws Exception {
        String token = loginToken();
        String tampered = token.substring(0, token.length() - 2) + "xx";
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(tampered)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredTokenReturnsUnauthorized() throws Exception {
        String token = tokenService.createToken(currentUserService.loadActiveUser(1L), Duration.ofSeconds(-1));
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevokesTokenAndRepeatLogoutDoesNotReturnServerError() throws Exception {
        String token = loginToken();
        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", bearer(token)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", bearer(token)))
                .andExpect(status().isOk());
    }

    @Test
    void logoutWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permissionCheckAllowsAuthorizedUser() throws Exception {
        String token = loginToken();
        mockMvc.perform(get("/api/v1/auth/rbac-check").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.permission").value("system:user:view"));
    }

    @Test
    void permissionCheckRejectsUserWithoutPermission() throws Exception {
        when(sysUserMapper.selectById(2L)).thenReturn(user(2L, "elder_demo", 2L, "NORMAL", 0));
        String elderToken = tokenService.createToken(currentUserService.loadActiveUser(2L), Duration.ofHours(1));
        mockMvc.perform(get("/api/v1/auth/rbac-check").header("Authorization", bearer(elderToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void disabledAccountCannotUseOldToken() throws Exception {
        String token = loginToken();
        when(sysUserMapper.selectById(1L)).thenReturn(user(1L, "admin_demo", 1L, "DISABLED", 0));
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permissionChangeIsReloadedForOldToken() throws Exception {
        String token = loginToken();
        when(sysPermissionMapper.selectPermissionCodesByRoleId(1L)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/auth/rbac-check").header("Authorization", bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void passwordHashUsesBcrypt() {
        SysUser user = user(1L, "admin_demo", 1L, "NORMAL", 0);
        org.junit.jupiter.api.Assertions.assertTrue(user.getPasswordHash().startsWith("$2a$"));
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(PASSWORD, user.getPasswordHash()));
    }

    private String loginToken() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("data").path("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private SysUser user(Long id, String username, Long roleId, String status, Integer deleted) {
        return user(id, username, roleId, status, deleted,
                "$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u");
    }

    private SysUser user(Long id, String username, Long roleId, String status, Integer deleted, String passwordHash) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setRealName(username + " display");
        user.setPasswordHash(passwordHash);
        user.setMainRoleId(roleId);
        user.setAccountStatus(status);
        user.setIsDeleted(deleted);
        return user;
    }

    private SysRole role(Long id, String code, String name, String status, Integer deleted) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleCode(code);
        role.setRoleName(name);
        role.setRoleStatus(status);
        role.setIsDeleted(deleted);
        return role;
    }

    private List<String> adminPermissions() {
        return Arrays.asList("system:user:view", "system:role:view", "training:resource:view",
                "care:order:view", "doctor:elder:view");
    }
}
