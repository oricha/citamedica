package com.citamedica.backend.adapter.in.dto.billing;

import com.citamedica.backend.domain.model.Payment;
import com.citamedica.backend.domain.model.PaymentProvider;
import com.citamedica.backend.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long patientId;
    private Long appointmentId;
    private Long invoiceId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentProvider paymentProvider;
    private String stripeTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static PaymentResponse from(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setPatientId(p.getPatient() != null ? p.getPatient().getId() : null);
        r.setAppointmentId(p.getAppointment() != null ? p.getAppointment().getId() : null);
        r.setInvoiceId(p.getInvoice() != null ? p.getInvoice().getId() : null);
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setStatus(p.getStatus());
        r.setPaymentProvider(p.getPaymentProvider());
        r.setStripeTransactionId(p.getStripeTransactionId());
        r.setCreatedAt(p.getCreatedAt());
        r.setCompletedAt(p.getCompletedAt());
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getStripeTransactionId() {
        return stripeTransactionId;
    }

    public void setStripeTransactionId(String stripeTransactionId) {
        this.stripeTransactionId = stripeTransactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
