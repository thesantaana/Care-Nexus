package com.carenexus.auth.token;

import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.TokenService;
import com.carenexus.auth.blacklist.TokenBlacklist;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService implements TokenService {

    private final JwtProperties jwtProperties;
    private final TokenBlacklist tokenBlacklist;

    public JwtTokenService(JwtProperties jwtProperties, TokenBlacklist tokenBlacklist) {
        this.jwtProperties = jwtProperties;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    public String createToken(CurrentUser currentUser, Duration expiresIn) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expiresIn);
        return Jwts.builder()
                .setSubject(String.valueOf(currentUser.getUserId()))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiresAt))
                .signWith(secretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public TokenClaims parseToken(String token) {
        return parseToken(token, false);
    }

    @Override
    public TokenClaims parseToken(String token, boolean allowRevoked) {
        Claims claims = parseClaims(token);
        String tokenId = claims.getId();
        if (!allowRevoked && tokenBlacklist.isRevoked(tokenId)) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REVOKED, "Token has been revoked");
        }
        return new TokenClaims(Long.valueOf(claims.getSubject()), tokenId,
                claims.getIssuedAt().toInstant(), claims.getExpiration().toInstant());
    }

    @Override
    public boolean revokeToken(String token) {
        TokenClaims claims = parseToken(token, true);
        tokenBlacklist.revoke(claims.getTokenId(), claims.getExpiresAt());
        return true;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED, "Token has expired");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_INVALID, "Invalid token");
        }
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
