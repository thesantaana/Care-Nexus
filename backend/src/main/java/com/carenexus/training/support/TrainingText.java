package com.carenexus.training.support;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import org.springframework.util.StringUtils;

public final class TrainingText {

    private TrainingText() {
    }

    public static String required(String value, String message) {
        String normalized = optional(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
        return normalized;
    }

    public static String optional(String value) {
        return value == null ? null : value.trim();
    }
}
