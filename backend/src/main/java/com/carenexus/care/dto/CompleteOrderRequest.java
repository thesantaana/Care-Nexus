package com.carenexus.care.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

public class CompleteOrderRequest {

    @NotBlank
    @Size(max = 1000)
    private String serviceContent;
    @NotNull
    @PastOrPresent
    private LocalDateTime completedAt;

    public String getServiceContent() {

        return serviceContent;

    }

    public void setServiceContent(String value) {
        this.serviceContent = value;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
