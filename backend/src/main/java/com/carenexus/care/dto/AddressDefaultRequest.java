package com.carenexus.care.dto;

import javax.validation.constraints.NotNull;

public class AddressDefaultRequest {

    @NotNull
    private Long elderId;

    public Long getElderId() {

        return elderId;

    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }
}
