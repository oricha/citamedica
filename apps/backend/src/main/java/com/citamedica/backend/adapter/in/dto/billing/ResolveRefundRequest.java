package com.citamedica.backend.adapter.in.dto.billing;

import jakarta.validation.constraints.NotNull;

public class ResolveRefundRequest {

    @NotNull
    private Boolean approve;
    private String notes;

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
