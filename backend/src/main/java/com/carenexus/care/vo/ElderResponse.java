package com.carenexus.care.vo;

public class ElderResponse {

    private Long elderId;
    private String elderName;
    private Long bindingId;

    public Long getElderId() {

        return elderId;

    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public Long getBindingId() {
        return bindingId;
    }

    public void setBindingId(Long bindingId) {
        this.bindingId = bindingId;
    }
}
