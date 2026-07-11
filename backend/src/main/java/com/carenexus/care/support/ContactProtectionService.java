package com.carenexus.care.support;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class ContactProtectionService {

    private final TextEncryptor textEncryptor;

    public ContactProtectionService(ContactProtectionProperties properties) {
        this.textEncryptor = Encryptors.text(properties.getSecret(), properties.getSalt());
    }

    public String protect(String mobile) {
        return textEncryptor.encrypt(normalize(mobile));
    }

    public String reveal(String cipherText) {
        return textEncryptor.decrypt(CareText.required(cipherText, "Protected mobile is required"));
    }

    public String last4(String mobile) {
        String normalized = normalize(mobile);
        return normalized.substring(Math.max(0, normalized.length() - 4));
    }

    public String maskLast4(String last4) {
        return last4 == null ? null : "*******" + last4;
    }

    private String normalize(String mobile) {
        String normalized = CareText.required(mobile, "Mobile is required").replaceAll("\\s+", "");
        if (!normalized.matches("[0-9+()-]{6,20}")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Mobile format is invalid");
        }
        return normalized;
    }
}
