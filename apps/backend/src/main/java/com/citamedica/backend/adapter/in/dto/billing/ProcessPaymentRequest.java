package com.citamedica.backend.adapter.in.dto.billing;

import com.citamedica.backend.domain.model.PaymentProvider;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProcessPaymentRequest {

    @NotNull
    private Long patientId;
    private Long appointmentId;
    private Long invoiceId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String currency = "USD";

    @NotNull
    private PaymentProvider paymentProvider;

    @NotNull
    private String providerPaymentToken;

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

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getProviderPaymentToken() {
        return providerPaymentToken;
    }

    public void setProviderPaymentToken(String providerPaymentToken) {
        this.providerPaymentToken = providerPaymentToken;
    }
}
