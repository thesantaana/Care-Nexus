package com.carenexus.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("health_record")
public class HealthRecord {
    private Long id;
    private Long elderId;
    private Long recorderId;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private BigDecimal bloodGlucose;
    private Integer heartRate;
    private BigDecimal bodyTemperature;
    private LocalDateTime recordTime;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getElderId() { return elderId; }

    public void setElderId(Long value) { this.elderId = value; }

    public Long getRecorderId() { return recorderId; }

    public void setRecorderId(Long value) { this.recorderId = value; }

    public Integer getSystolicPressure() { return systolicPressure; }

    public void setSystolicPressure(Integer value) { this.systolicPressure = value; }

    public Integer getDiastolicPressure() { return diastolicPressure; }

    public void setDiastolicPressure(Integer value) { this.diastolicPressure = value; }

    public BigDecimal getBloodGlucose() { return bloodGlucose; }

    public void setBloodGlucose(BigDecimal value) { this.bloodGlucose = value; }

    public Integer getHeartRate() { return heartRate; }

    public void setHeartRate(Integer value) { this.heartRate = value; }

    public BigDecimal getBodyTemperature() { return bodyTemperature; }

    public void setBodyTemperature(BigDecimal value) { this.bodyTemperature = value; }

    public LocalDateTime getRecordTime() { return recordTime; }

    public void setRecordTime(LocalDateTime value) { this.recordTime = value; }

    public String getRemark() { return remark; }

    public void setRemark(String value) { this.remark = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }
}
