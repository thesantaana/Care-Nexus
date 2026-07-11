package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ServiceItemRequest {

    @NotBlank
    @Size(max = 128)
    private String serviceName;
    @Size(max = 64)
    private String category;
    @Size(max = 500)
    private String description;

    public String getServiceName() {

        return serviceName;

    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
