package com.carenexus.care.support;

import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "care-nexus.contact-protection")
public class ContactProtectionProperties {

    private String secret;
    private String salt;

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(secret) || secret.length() < 16) {
            throw new IllegalStateException("CARE_NEXUS_CONTACT_SECRET must be at least 16 characters");
        }
        if (!StringUtils.hasText(salt) || !salt.matches("[0-9a-fA-F]{16,}")) {
            throw new IllegalStateException("CARE_NEXUS_CONTACT_SALT must be hexadecimal and at least 16 characters");
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
