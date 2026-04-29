package com.citamedica.backend.adapter.in.dto.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ClinicOfferingCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(15)
    @Max(120)
    private Integer durationMinutes;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal basePrice;

    private Long minRequiredSpecialtyId;

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
}
