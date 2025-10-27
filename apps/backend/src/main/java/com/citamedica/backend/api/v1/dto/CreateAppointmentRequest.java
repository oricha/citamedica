package com.citamedica.backend.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAppointmentRequest {
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Start time is required")
    private String startAt;
    
    @NotBlank(message = "End time is required")
    private String endAt;
    
    private String calBookingId;
    
    private String notes;

    // Constructors
    public CreateAppointmentRequest() {}

    public CreateAppointmentRequest(Long doctorId, Long patientId, String type, 
                                   String startAt, String endAt, String calBookingId, String notes) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.type = type;
        this.startAt = startAt;
        this.endAt = endAt;
        this.calBookingId = calBookingId;
        this.notes = notes;
    }

    // Getters and setters
    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getCalBookingId() {
        return calBookingId;
    }

    public void setCalBookingId(String calBookingId) {
        this.calBookingId = calBookingId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}