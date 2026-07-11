package com.carenexus.care;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.carenexus.care.support.ContactProtectionProperties;
import com.carenexus.care.support.ContactProtectionService;
import org.junit.jupiter.api.Test;

class ContactProtectionServiceTest {

    @Test
    void shouldEncryptRevealAndMaskMobile() {
        ContactProtectionProperties properties = new ContactProtectionProperties();
        properties.setSecret("test-contact-secret-change-me");
        properties.setSalt("0123456789abcdef");
        properties.validate();
        ContactProtectionService service = new ContactProtectionService(properties);

        String cipherText = service.protect("138 0013 8000");

        assertNotEquals("13800138000", cipherText);
        assertEquals("13800138000", service.reveal(cipherText));
        assertEquals("8000", service.last4("13800138000"));
        assertEquals("*******8000", service.maskLast4("8000"));
    }
}
