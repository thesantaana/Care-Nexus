package com.carenexus.auth.blacklist;

import java.time.Instant;

public interface TokenBlacklist {

    void revoke(String tokenId, Instant expiresAt);

    boolean isRevoked(String tokenId);
}
