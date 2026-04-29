package com.citamedica.backend.adapter.in.dto.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public class ClinicOfferingPatchRequest {

    private String name;
    private String description;
    @Min(15)
    @Max(120)
    private Integer durationMinutes;
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal basePrice;
    private Long minRequiredSpecialtyId;
    private Boolean clearMinRequiredSpecialty;
    private Boolean active;

    public Boolean getClearMinRequiredSpecialty() {
        return clearMinRequiredSpecialty;
    }

    public void setClearMinRequiredSpecialty(Boolean clearMinRequiredSpecialty) {
        this.clearMinRequiredSpecialty = clearMinRequiredSpecialty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Long getMinRequiredSpecialtyId() {
        return minRequiredSpecialtyId;
    }

    public void setMinRequiredSpecialtyId(Long minRequiredSpecialtyId) {
        this.minRequiredSpecialtyId = minRequiredSpecialtyId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
