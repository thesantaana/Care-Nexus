package com.carenexus.care.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ComplaintRequest {

    @NotBlank
    @Size(max = 1000)
    private String complaintContent;

    public String getComplaintContent() {

        return complaintContent;

    }

    public void setComplaintContent(String value) {
        this.complaintContent = value;
    }
}
