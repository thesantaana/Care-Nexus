package com.carenexus.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class OfflineRequest {

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
