package com.citamedica.backend.api.v1.dto;

import com.citamedica.backend.domain.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientResponse {
    
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String insurancePlan;
    private LocalDateTime createdAt;

    // Constructors
    public PatientResponse() {}

    public PatientResponse(Long id, String fullName, String email, String phone, 
                          LocalDate birthDate, String insurancePlan, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.insurancePlan = insurancePlan;
        this.createdAt = createdAt;
    }

    // Factory method
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getFullName(),
            patient.getEmail(),
            patient.getPhone(),
            patient.getBirthDate(),
            patient.getInsurancePlan(),
            patient.getCreatedAt()
        );
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}