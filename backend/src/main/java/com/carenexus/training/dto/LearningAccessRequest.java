package com.carenexus.training.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LearningAccessRequest {

    @NotNull
    private Long resourceId;

    @NotNull
    @Min(1)
    @Max(86400)
    private Integer accessSeconds;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAccessSeconds() {
        return accessSeconds;
    }

    public void setAccessSeconds(Integer accessSeconds) {
        this.accessSeconds = accessSeconds;
    }
}
