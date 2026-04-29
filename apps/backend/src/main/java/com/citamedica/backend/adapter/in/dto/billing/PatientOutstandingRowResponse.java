package com.citamedica.backend.adapter.in.dto.billing;

import java.math.BigDecimal;

public class PatientOutstandingRowResponse {

    private Long patientId;
    private BigDecimal totalOutstanding;

    public PatientOutstandingRowResponse() {}

    public PatientOutstandingRowResponse(Long patientId, BigDecimal totalOutstanding) {
        this.patientId = patientId;
        this.totalOutstanding = totalOutstanding;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }
}
