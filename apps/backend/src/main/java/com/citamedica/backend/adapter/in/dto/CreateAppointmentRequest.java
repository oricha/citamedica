package com.citamedica.backend.adapter.in.dto;

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

    /** Required when {@code serviceId} is omitted; ignored when a service is selected (end time is derived). */
    private String endAt;
    
    private String calBookingId;
    
    private String notes;

    /** Optional explicit slot id when {@code app.availability.enforced} is true. */
    private Long timeSlotId;

    /** Optional clinic offering (service catalog); when set, duration and validation apply from catalog. */
    private Long serviceId;

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

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}