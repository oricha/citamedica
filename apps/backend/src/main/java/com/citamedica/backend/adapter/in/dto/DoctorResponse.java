package com.citamedica.backend.adapter.in.dto;

import com.citamedica.backend.domain.model.Doctor;

import java.time.LocalDateTime;

public class DoctorResponse {
    
    private Long id;
    private Long clinicId;
    private String clinicName;
    private String fullName;
    private String specialty;
    private String email;
    private String phone;
    private String calUsername;
    private Boolean active;
    private LocalDateTime createdAt;

    // Constructors
    public DoctorResponse() {}

    public DoctorResponse(Long id, Long clinicId, String clinicName, String fullName, 
                         String specialty, String email, String phone, String calUsername,
                         Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.fullName = fullName;
        this.specialty = specialty;
        this.email = email;
        this.phone = phone;
        this.calUsername = calUsername;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Factory method
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
            doctor.getId(),
            doctor.getClinic() != null ? doctor.getClinic().getId() : null,
            doctor.getClinic() != null ? doctor.getClinic().getName() : null,
            doctor.getFullName(),
            doctor.getSpecialty(),
            doctor.getEmail(),
            doctor.getPhone(),
            doctor.getCalUsername(),
            doctor.getActive(),
            doctor.getCreatedAt()
        );
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCalUsername() {
        return calUsername;
    }

    public void setCalUsername(String calUsername) {
        this.calUsername = calUsername;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}