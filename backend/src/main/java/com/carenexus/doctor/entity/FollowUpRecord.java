package com.carenexus.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("follow_up_record")
public class FollowUpRecord {
    private Long id;
    private Long elderId;
    private Long doctorId;
    private String followUpMethod;
    private String followUpResult;
    private String recordStatus;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getElderId() { return elderId; }

    public void setElderId(Long value) { this.elderId = value; }

    public Long getDoctorId() { return doctorId; }

    public void setDoctorId(Long value) { this.doctorId = value; }

    public String getFollowUpMethod() { return followUpMethod; }

    public void setFollowUpMethod(String value) { this.followUpMethod = value; }

    public String getFollowUpResult() { return followUpResult; }

    public void setFollowUpResult(String value) { this.followUpResult = value; }

    public String getRecordStatus() { return recordStatus; }

    public void setRecordStatus(String value) { this.recordStatus = value; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }

    public void setConfirmedAt(LocalDateTime value) { this.confirmedAt = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime value) { this.updatedAt = value; }
}
