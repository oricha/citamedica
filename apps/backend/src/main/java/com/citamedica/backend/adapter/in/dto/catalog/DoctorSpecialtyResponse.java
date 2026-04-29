package com.citamedica.backend.adapter.in.dto.catalog;

import com.citamedica.backend.domain.model.DoctorSpecialty;

import java.time.LocalDateTime;

public class DoctorSpecialtyResponse {

    private Long id;
    private Long specialtyId;
    private String specialtyCode;
    private String specialtyName;
    private boolean primary;
    private Integer overrideDurationMinutes;
    private LocalDateTime assignedAt;

    public static DoctorSpecialtyResponse from(DoctorSpecialty ds) {
        DoctorSpecialtyResponse r = new DoctorSpecialtyResponse();
        r.setId(ds.getId());
        r.setSpecialtyId(ds.getSpecialty().getId());
        r.setSpecialtyCode(ds.getSpecialty().getCode());
        r.setSpecialtyName(ds.getSpecialty().getName());
        r.setPrimary(ds.isPrimarySpecialty());
        r.setOverrideDurationMinutes(ds.getOverrideDurationMinutes());
        r.setAssignedAt(ds.getAssignedAt());
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
    }

    public String getSpecialtyCode() {
        return specialtyCode;
    }

    public void setSpecialtyCode(String specialtyCode) {
        this.specialtyCode = specialtyCode;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
