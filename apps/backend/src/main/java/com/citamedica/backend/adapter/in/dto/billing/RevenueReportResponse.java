package com.citamedica.backend.adapter.in.dto.billing;

import java.math.BigDecimal;

public class RevenueReportResponse {

    private Long clinicId;
    private BigDecimal totalRevenue;

    public RevenueReportResponse() {}

    public RevenueReportResponse(Long clinicId, BigDecimal totalRevenue) {
        this.clinicId = clinicId;
        this.totalRevenue = totalRevenue;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
