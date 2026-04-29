package com.citamedica.backend.adapter.in.dto.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SpecialtySurchargeRequest {

    @NotNull
    private Long specialtyId;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal surchargeAmount;

    /** When null, applies globally (clinic_id NULL in database). */
    private Long clinicId;

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
    }

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }
}
