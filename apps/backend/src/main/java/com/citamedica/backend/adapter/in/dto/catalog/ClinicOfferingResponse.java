package com.citamedica.backend.adapter.in.dto.catalog;

import com.citamedica.backend.domain.model.ClinicOffering;

import java.math.BigDecimal;

public class ClinicOfferingResponse {

    private Long id;
    private Long clinicId;
    private String name;
    private String description;
    private int durationMinutes;
    private BigDecimal basePrice;
    private boolean active;
    private Long minRequiredSpecialtyId;
    private String minRequiredSpecialtyCode;

    public ClinicOfferingResponse() {}

    public static ClinicOfferingResponse from(ClinicOffering o) {
        ClinicOfferingResponse r = new ClinicOfferingResponse();
        r.setId(o.getId());
        r.setClinicId(o.getClinic() != null ? o.getClinic().getId() : null);
        r.setName(o.getName());
        r.setDescription(o.getDescription());
        r.setDurationMinutes(o.getDurationMinutes());
        r.setBasePrice(o.getBasePrice());
        r.setActive(o.isActive());
        if (o.getMinRequiredSpecialty() != null) {
            r.setMinRequiredSpecialtyId(o.getMinRequiredSpecialty().getId());
            r.setMinRequiredSpecialtyCode(o.getMinRequiredSpecialty().getCode());
        }
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getMinRequiredSpecialtyId() {
        return minRequiredSpecialtyId;
    }

    public void setMinRequiredSpecialtyId(Long minRequiredSpecialtyId) {
        this.minRequiredSpecialtyId = minRequiredSpecialtyId;
    }

    public String getMinRequiredSpecialtyCode() {
        return minRequiredSpecialtyCode;
    }

    public void setMinRequiredSpecialtyCode(String minRequiredSpecialtyCode) {
        this.minRequiredSpecialtyCode = minRequiredSpecialtyCode;
    }
}
