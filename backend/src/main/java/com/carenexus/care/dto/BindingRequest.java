package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class BindingRequest {

    @NotBlank
    @Size(max = 64)
    private String elderIdentifier;
    @NotBlank
    @Size(max = 128)
    private String verifyCode;

    public String getElderIdentifier() {

        return elderIdentifier;

    }

    public void setElderIdentifier(String elderIdentifier) {
        this.elderIdentifier = elderIdentifier;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
