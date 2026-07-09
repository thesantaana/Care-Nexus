package com.carenexus.auth.token;

import java.time.Instant;

public class TokenClaims {

    private final Long userId;
    private final String tokenId;
    private final Instant issuedAt;
    private final Instant expiresAt;

    public TokenClaims(Long userId, String tokenId, Instant issuedAt, Instant expiresAt) {
        this.userId = userId;
        this.tokenId = tokenId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
