package com.carenexus.common.error;

public enum ErrorCode {
    SUCCESS("SUCCESS"),
    BAD_REQUEST("BAD_REQUEST"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    NOT_FOUND("NOT_FOUND"),
    CONFLICT("CONFLICT"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    BUSINESS_ERROR("BUSINESS_ERROR"),
    SYSTEM_ERROR("SYSTEM_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
