package com.citamedica.backend.adapter.in.dto.billing;

import java.math.BigDecimal;

public class OutstandingBalanceResponse {

    private Long patientId;
    private BigDecimal balance;

    public OutstandingBalanceResponse() {}

    public OutstandingBalanceResponse(Long patientId, BigDecimal balance) {
        this.patientId = patientId;
        this.balance = balance;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
