package com.citamedica.backend.adapter.in.dto.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ServicePricingRuleRequest {

    @NotNull
    private Long serviceId;

    private Long specialtyId;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal overridePrice;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
    }

    public BigDecimal getOverridePrice() {
        return overridePrice;
    }

    public void setOverridePrice(BigDecimal overridePrice) {
        this.overridePrice = overridePrice;
    }
}
