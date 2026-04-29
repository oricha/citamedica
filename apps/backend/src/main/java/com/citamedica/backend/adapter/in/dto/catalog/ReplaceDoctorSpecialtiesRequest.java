package com.citamedica.backend.adapter.in.dto.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ReplaceDoctorSpecialtiesRequest {

    @NotEmpty
    @Valid
    private List<Entry> specialties;

    public List<Entry> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<Entry> specialties) {
        this.specialties = specialties;
    }

    public static class Entry {
        @NotNull
        private Long specialtyId;
        private boolean primary;
        @jakarta.validation.constraints.Min(15)
        @jakarta.validation.constraints.Max(120)
        private Integer overrideDurationMinutes;

        public Long getSpecialtyId() {
            return specialtyId;
        }

        public void setSpecialtyId(Long specialtyId) {
            this.specialtyId = specialtyId;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public Integer getOverrideDurationMinutes() {
            return overrideDurationMinutes;
        }

        public void setOverrideDurationMinutes(Integer overrideDurationMinutes) {
            this.overrideDurationMinutes = overrideDurationMinutes;
        }
    }
}
