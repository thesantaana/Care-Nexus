package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ReasonRequest {

    @NotBlank
    @Size(max = 500)
    private String reason;

    public String getReason() {

        return reason;

    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
