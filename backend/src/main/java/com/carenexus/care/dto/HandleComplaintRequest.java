package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class HandleComplaintRequest {

    @NotBlank
    @Size(max = 1000)
    private String handledResult;

    public String getHandledResult() {

        return handledResult;

    }

    public void setHandledResult(String value) {
        this.handledResult = value;
    }
}
