package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AddressUpdateRequest {

    @NotBlank
    @Size(max = 64)
    private String contactName;
    @NotBlank
    @Pattern(regexp = "^[0-9]{7,20}$")
    private String mobile;
    @NotBlank
    @Size(max = 255)
    private String addressDetail;

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
}
