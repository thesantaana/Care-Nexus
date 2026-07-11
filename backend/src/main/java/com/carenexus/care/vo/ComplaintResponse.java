package com.carenexus.care.vo;

import java.time.LocalDateTime;

public class ComplaintResponse {

    private Long id;
    private Long orderId;
    private String complaintContent;
    private String complaintStatus;
    private String handledResult;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;

    public Long getId() {

        return id;

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String value) {
        this.complaintContent = value;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String value) {
        this.complaintStatus = value;
    }

    public String getHandledResult() {
        return handledResult;
    }

    public void setHandledResult(String value) {
        this.handledResult = value;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    public void setHandledAt(LocalDateTime handledAt) {
        this.handledAt = handledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
