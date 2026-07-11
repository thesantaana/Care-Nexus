package com.carenexus.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("doctor_elder_authorization")
public class DoctorElderAuthorization {
    private Long id;
    private Long doctorUserId;
    private Long elderId;
    private String authStatus;
    private Long authorizedBy;
    private LocalDateTime authorizedAt;
    private Long cancelledBy;
    private LocalDateTime cancelledAt;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getDoctorUserId() { return doctorUserId; }

    public void setDoctorUserId(Long value) { this.doctorUserId = value; }

    public Long getElderId() { return elderId; }

    public void setElderId(Long value) { this.elderId = value; }

    public String getAuthStatus() { return authStatus; }

    public void setAuthStatus(String value) { this.authStatus = value; }

    public Long getAuthorizedBy() { return authorizedBy; }

    public void setAuthorizedBy(Long value) { this.authorizedBy = value; }

    public LocalDateTime getAuthorizedAt() { return authorizedAt; }

    public void setAuthorizedAt(LocalDateTime value) { this.authorizedAt = value; }

    public Long getCancelledBy() { return cancelledBy; }

    public void setCancelledBy(Long value) { this.cancelledBy = value; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }

    public void setCancelledAt(LocalDateTime value) { this.cancelledAt = value; }
}
