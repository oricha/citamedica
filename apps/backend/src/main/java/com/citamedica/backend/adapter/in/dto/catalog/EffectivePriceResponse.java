package com.citamedica.backend.adapter.in.dto.catalog;

import java.math.BigDecimal;

public class EffectivePriceResponse {

    private Long clinicId;
    private Long doctorId;
    private Long serviceId;
    private BigDecimal effectivePrice;

    public EffectivePriceResponse() {}

    public EffectivePriceResponse(Long clinicId, Long doctorId, Long serviceId, BigDecimal effectivePrice) {
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.serviceId = serviceId;
        this.effectivePrice = effectivePrice;
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

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public BigDecimal getEffectivePrice() {
        return effectivePrice;
    }

    public void setEffectivePrice(BigDecimal effectivePrice) {
        this.effectivePrice = effectivePrice;
    }
}
