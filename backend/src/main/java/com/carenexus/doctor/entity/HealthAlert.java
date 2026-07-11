package com.carenexus.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("health_alert")
public class HealthAlert {
    private Long id;
    private Long elderId;
    private Long healthRecordId;
    private String alertSource;
    private String alertLevel;
    private String alertStatus;
    private String alertContent;
    private Long handledBy;
    private String handledComment;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getElderId() { return elderId; }

    public void setElderId(Long value) { this.elderId = value; }

    public Long getHealthRecordId() { return healthRecordId; }

    public void setHealthRecordId(Long value) { this.healthRecordId = value; }

    public String getAlertSource() { return alertSource; }

    public void setAlertSource(String value) { this.alertSource = value; }

    public String getAlertLevel() { return alertLevel; }

    public void setAlertLevel(String value) { this.alertLevel = value; }

    public String getAlertStatus() { return alertStatus; }

    public void setAlertStatus(String value) { this.alertStatus = value; }

    public String getAlertContent() { return alertContent; }

    public void setAlertContent(String value) { this.alertContent = value; }

    public Long getHandledBy() { return handledBy; }

    public void setHandledBy(Long value) { this.handledBy = value; }

    public String getHandledComment() { return handledComment; }

    public void setHandledComment(String value) { this.handledComment = value; }

    public LocalDateTime getHandledAt() { return handledAt; }

    public void setHandledAt(LocalDateTime value) { this.handledAt = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime value) { this.updatedAt = value; }
}
