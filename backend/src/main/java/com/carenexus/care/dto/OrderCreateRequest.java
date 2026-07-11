package com.carenexus.care.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

public class OrderCreateRequest {

    @NotNull
    private Long elderId;
    @NotNull
    private Long serviceItemId;
    @NotNull
    private Long addressId;
    @NotNull
    @Future
    private LocalDateTime appointmentTime;

    public Long getElderId() {

        return elderId;

    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
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

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime value) {
        this.appointmentTime = value;
    }
}
