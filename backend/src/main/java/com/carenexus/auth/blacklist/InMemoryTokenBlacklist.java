package com.carenexus.auth.blacklist;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryTokenBlacklist implements TokenBlacklist {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    @Override
    public void revoke(String tokenId, Instant expiresAt) {
        cleanupExpired();
        if (tokenId != null && expiresAt != null && expiresAt.isAfter(Instant.now())) {
            revokedTokens.put(tokenId, expiresAt);
        }
    }

    @Override
    public boolean isRevoked(String tokenId) {
        cleanupExpired();
        return tokenId != null && revokedTokens.containsKey(tokenId);
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        Iterator<Map.Entry<String, Instant>> iterator = revokedTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Instant> entry = iterator.next();
            if (!entry.getValue().isAfter(now)) {
                iterator.remove();
            }
        }
    }
}
