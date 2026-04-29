package com.citamedica.backend.adapter.in.dto.billing;

import com.citamedica.backend.domain.model.Refund;
import com.citamedica.backend.domain.model.RefundReason;
import com.citamedica.backend.domain.model.RefundStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundResponse {

    private Long id;
    private Long paymentId;
    private BigDecimal amount;
    private RefundReason reason;
    private RefundStatus status;
    private String providerRefundId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static RefundResponse from(Refund r) {
        RefundResponse x = new RefundResponse();
        x.setId(r.getId());
        x.setPaymentId(r.getPayment() != null ? r.getPayment().getId() : null);
        x.setAmount(r.getAmount());
        x.setReason(r.getReason());
        x.setStatus(r.getStatus());
        x.setProviderRefundId(r.getProviderRefundId());
        x.setCreatedAt(r.getCreatedAt());
        x.setCompletedAt(r.getCompletedAt());
        return x;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RefundReason getReason() {
        return reason;
    }

    public void setReason(RefundReason reason) {
        this.reason = reason;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public String getProviderRefundId() {
        return providerRefundId;
    }

    public void setProviderRefundId(String providerRefundId) {
        this.providerRefundId = providerRefundId;
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
