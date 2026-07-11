package com.carenexus.care.vo;

import java.time.LocalDateTime;

public class CareOrderResponse {

    private Long id;
    private Long elderId;
    private Long orderUserId;
    private Long serviceItemId;
    private Long addressId;
    private Long assignedCaregiverId;
    private LocalDateTime appointmentTime;
    private String orderStatus;
    private String cancelReason;
    private String evaluationStatus;
    private String complaintStatus;
    private String serviceContent;
    private LocalDateTime completedAt;

    public Long getId() {

        return id;

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public Long getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(Long value) {
        this.orderUserId = value;
    }

    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long value) {
        this.serviceItemId = value;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getAssignedCaregiverId() {
        return assignedCaregiverId;
    }

    public void setAssignedCaregiverId(Long value) {
        this.assignedCaregiverId = value;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime value) {
        this.appointmentTime = value;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(String value) {
        this.evaluationStatus = value;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String value) {
        this.complaintStatus = value;
    }

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
