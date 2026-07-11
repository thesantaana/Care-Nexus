package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;

public class ServiceStatusRequest {

    @NotBlank
    private String serviceStatus;

    public String getServiceStatus() {

        return serviceStatus;

    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
}
