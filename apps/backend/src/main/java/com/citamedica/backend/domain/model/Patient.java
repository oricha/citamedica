package com.citamedica.backend.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    @NotBlank
    private String fullName;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "insurance_plan")
    private String insurancePlan;

    @Column(name = "language_preference")
    private String languagePreference = "es";

    @Column(name = "portal_password_hash")
    private String portalPasswordHash;

    @Column(name = "portal_access_enabled", nullable = false)
    private boolean portalAccessEnabled = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    public Patient() {}

    public Patient(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getInsurancePlan() { return insurancePlan; }
    public void setInsurancePlan(String insurancePlan) { this.insurancePlan = insurancePlan; }
    public String getLanguagePreference() { return languagePreference; }
    public void setLanguagePreference(String languagePreference) { this.languagePreference = languagePreference; }
    public String getPortalPasswordHash() { return portalPasswordHash; }
    public void setPortalPasswordHash(String portalPasswordHash) { this.portalPasswordHash = portalPasswordHash; }
    public boolean isPortalAccessEnabled() { return portalAccessEnabled; }
    public void setPortalAccessEnabled(boolean portalAccessEnabled) { this.portalAccessEnabled = portalAccessEnabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void updateNotificationPreferences(NotificationPreference preference, boolean emailEnabled, boolean smsEnabled, String phone) {
        preference.setEmailEnabled(emailEnabled);
        preference.setSmsEnabled(smsEnabled);
        preference.setPhone(phone);
        preference.setUpdatedAt(LocalDateTime.now());
        if (smsEnabled) {
            preference.setConsentTimestamp(LocalDateTime.now());
            preference.setConsentMethod("api");
        }
    }

    public boolean canReceiveNotification(NotificationChannel channel, NotificationPreference preference) {
        if (channel == NotificationChannel.EMAIL) {
            return preference.isEmailEnabled() && email != null && !email.isBlank();
        }
        return preference.isSmsEnabled() && preference.getPhone() != null && !preference.getPhone().isBlank();
    }
}
