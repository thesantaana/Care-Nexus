package com.carenexus.care.support;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import org.springframework.util.StringUtils;

public final class CareText {

    private CareText() {
    }

    public static String required(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
        return value.trim();
    }

    public static String optional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
