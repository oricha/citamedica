package com.citamedica.backend.adapter.in.dto;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentResponse {
    
    private Long id;
    private Long clinicId;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String calBookingId;
    private String type;
    private AppointmentStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AppointmentResponse() {}

    public AppointmentResponse(Long id, Long clinicId, Long doctorId, String doctorName, 
                              String doctorSpecialty, Long patientId, String patientName,
                              String patientEmail, String calBookingId, String type,
                              AppointmentStatus status, LocalDateTime startAt, LocalDateTime endAt,
                              String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.calBookingId = calBookingId;
        this.type = type;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
            appointment.getId(),
            appointment.getClinic() != null ? appointment.getClinic().getId() : null,
            appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
            appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : null,
            appointment.getDoctor() != null ? appointment.getDoctor().getSpecialty() : null,
            appointment.getPatient() != null ? appointment.getPatient().getId() : null,
            appointment.getPatient() != null ? appointment.getPatient().getFullName() : null,
            appointment.getPatient() != null ? appointment.getPatient().getEmail() : null,
            appointment.getCalBookingId(),
            appointment.getType(),
            appointment.getStatus(),
            appointment.getStartAt(),
            appointment.getEndAt(),
            appointment.getNotes(),
            appointment.getCreatedAt(),
            appointment.getUpdatedAt()
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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getCalBookingId() {
        return calBookingId;
    }

    public void setCalBookingId(String calBookingId) {
        this.calBookingId = calBookingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}