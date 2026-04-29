package com.citamedica.backend.adapter.in.dto.notification;

public class NotificationPreferenceRequest {
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private String phone;

    public Boolean getEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(Boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public Boolean getSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(Boolean smsEnabled) { this.smsEnabled = smsEnabled; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
