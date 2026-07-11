package com.carenexus.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("elder_profile")
public class ElderHealthProfile {
    private Long id;
    private Long userId;
    private String elderName;
    private String gender;
    private LocalDate birthDate;
    private String mobileLast4;
    private String healthSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long value) { this.userId = value; }

    public String getElderName() { return elderName; }

    public void setElderName(String value) { this.elderName = value; }

    public String getGender() { return gender; }

    public void setGender(String value) { this.gender = value; }

    public LocalDate getBirthDate() { return birthDate; }

    public void setBirthDate(LocalDate value) { this.birthDate = value; }

    public String getMobileLast4() { return mobileLast4; }

    public void setMobileLast4(String value) { this.mobileLast4 = value; }

    public String getHealthSummary() { return healthSummary; }

    public void setHealthSummary(String value) { this.healthSummary = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime value) { this.updatedAt = value; }

    public Integer getIsDeleted() { return isDeleted; }

    public void setIsDeleted(Integer value) { this.isDeleted = value; }
}
