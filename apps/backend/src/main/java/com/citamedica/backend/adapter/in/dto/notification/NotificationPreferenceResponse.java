package com.citamedica.backend.adapter.in.dto.notification;

import com.citamedica.backend.domain.model.NotificationPreference;

import java.time.LocalDateTime;

public class NotificationPreferenceResponse {
    private Long patientId;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationPreferenceResponse from(NotificationPreference p) {
        NotificationPreferenceResponse r = new NotificationPreferenceResponse();
        r.patientId = p.getPatient().getId();
        r.emailEnabled = p.isEmailEnabled();
        r.smsEnabled = p.isSmsEnabled();
        r.phone = p.getPhone();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        return r;
    }

    public Long getPatientId() { return patientId; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
