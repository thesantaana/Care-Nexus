package com.carenexus.care.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("elder_profile")
public class ElderProfile {

    private Long id;
    private Long userId;
    private String elderName;
    private String bindingCodeHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getBindingCodeHash() {
        return bindingCodeHash;
    }

    public void setBindingCodeHash(String bindingCodeHash) {
        this.bindingCodeHash = bindingCodeHash;
    }
}
