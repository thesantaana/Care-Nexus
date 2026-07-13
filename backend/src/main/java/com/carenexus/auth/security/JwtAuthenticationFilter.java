package com.carenexus.auth.security;

import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.TokenService;
import com.carenexus.auth.token.TokenClaims;
import com.carenexus.common.exception.BusinessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEALTH_PATH = "/api/v1/health";
    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final String LOGOUT_PATH = "/api/v1/auth/logout";
    private static final String NOTE_MEDIA_PATH = "/note-media/";

    private final TokenService tokenService;
    private final CurrentUserService currentUserService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(TokenService tokenService, CurrentUserService currentUserService,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.tokenService = tokenService;
        this.currentUserService = currentUserService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HEALTH_PATH.equals(request.getRequestURI())) {
            return true;
        }
        if (request.getRequestURI().startsWith(NOTE_MEDIA_PATH)) {
            return true;
        }
        return LOGIN_PATH.equals(request.getRequestURI()) && HttpMethod.POST.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            authenticateIfBearerTokenPresent(request);
            filterChain.doFilter(request, response);
        } catch (BusinessException | JwtAuthenticationException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new JwtAuthenticationException(ex.getMessage()));
        }
    }

    private void authenticateIfBearerTokenPresent(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization)) {
            return;
        }
        if (!authorization.startsWith(BEARER_PREFIX) || authorization.trim().length() <= BEARER_PREFIX.length()) {
            throw new JwtAuthenticationException("Invalid bearer token");
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        boolean allowRevoked = LOGOUT_PATH.equals(request.getRequestURI());
        TokenClaims claims = tokenService.parseToken(token, allowRevoked);
        CurrentUser currentUser = currentUserService.loadActiveUser(claims.getUserId());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                currentUser, token, toAuthorities(currentUser));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<SimpleGrantedAuthority> toAuthorities(CurrentUser currentUser) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String permissionCode : currentUser.getPermissionCodes()) {
            authorities.add(new SimpleGrantedAuthority(permissionCode));
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_" + currentUser.getMainRoleCode()));
        return authorities;
    }
}
