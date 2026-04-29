package com.citamedica.backend.adapter.in.dto.billing;

import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.model.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private Long clinicId;
    private Long patientId;
    private Long appointmentId;
    private String invoiceNumber;
    private BigDecimal amount;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private LocalDateTime createdAt;

    public static InvoiceResponse from(Invoice i) {
        InvoiceResponse r = new InvoiceResponse();
        r.setId(i.getId());
        r.setClinicId(i.getClinic() != null ? i.getClinic().getId() : null);
        r.setPatientId(i.getPatient() != null ? i.getPatient().getId() : null);
        r.setAppointmentId(i.getAppointment() != null ? i.getAppointment().getId() : null);
        r.setInvoiceNumber(i.getInvoiceNumber());
        r.setAmount(i.getAmount());
        r.setDueDate(i.getDueDate());
        r.setStatus(i.getStatus());
        r.setCreatedAt(i.getCreatedAt());
        return r;
    }

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

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
