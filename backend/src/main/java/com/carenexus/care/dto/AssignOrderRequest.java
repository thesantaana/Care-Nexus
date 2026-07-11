package com.carenexus.care.dto;

import javax.validation.constraints.NotNull;

public class AssignOrderRequest {

    @NotNull
    private Long caregiverId;

    public Long getCaregiverId() {

        return caregiverId;

    }

    public void setCaregiverId(Long caregiverId) {
        this.caregiverId = caregiverId;
    }
}
