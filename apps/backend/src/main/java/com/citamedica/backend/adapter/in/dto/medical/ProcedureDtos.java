package com.citamedica.backend.adapter.in.dto.medical;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public final class ProcedureDtos {
    private ProcedureDtos() {}

    public record CreateRequest(
            @NotBlank String procedureName,
            @NotNull LocalDate procedureDate,
            String outcome,
            Long relatedConditionId,
            String notes
    ) {}

    public record UpdateRequest(
            String procedureName,
            LocalDate procedureDate,
            String outcome,
            Long relatedConditionId,
            String notes
    ) {}

    public record Response(
            Long id,
            String procedureName,
            LocalDate procedureDate,
            String outcome,
            Long relatedConditionId,
            String notes
    ) {}
}
