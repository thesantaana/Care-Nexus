package com.carenexus.auth.token;

import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "care-nexus.jwt")
public class JwtProperties {

    private String secret;
    private long expirationSeconds = 7200L;

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(secret) || secret.length() < 32) {
            throw new IllegalStateException("CARE_NEXUS_JWT_SECRET must be at least 32 characters");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalStateException("CARE_NEXUS_JWT_EXPIRATION_SECONDS must be positive");
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }
}
