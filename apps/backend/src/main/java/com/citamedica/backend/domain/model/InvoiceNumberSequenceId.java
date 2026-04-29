package com.citamedica.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InvoiceNumberSequenceId implements Serializable {

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    public InvoiceNumberSequenceId() {}

    public InvoiceNumberSequenceId(Long clinicId, int fiscalYear) {
        this.clinicId = clinicId;
        this.fiscalYear = fiscalYear;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(int fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceNumberSequenceId that = (InvoiceNumberSequenceId) o;
        return fiscalYear == that.fiscalYear && Objects.equals(clinicId, that.clinicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clinicId, fiscalYear);
    }
}
