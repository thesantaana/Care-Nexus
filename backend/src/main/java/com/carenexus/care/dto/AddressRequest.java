package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AddressRequest {

    @NotNull
    private Long elderId;
    @NotBlank
    @Size(max = 64)
    private String contactName;
    @NotBlank
    @Pattern(regexp = "^[0-9]{7,20}$")
    private String mobile;
    @NotBlank
    @Size(max = 255)
    private String addressDetail;
    private Boolean defaultAddress;

    public Long getElderId() {

        return elderId;

    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Boolean getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
