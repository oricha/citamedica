package com.citamedica.backend.adapter.in.dto.medical;

import com.citamedica.backend.domain.model.medical.ClinicalSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AllergyDtos {
    private AllergyDtos() {}

    public record CreateRequest(
            @NotBlank String allergenName,
            @NotNull ClinicalSeverity severity,
            String reactionType,
            String notes
    ) {}

    public record UpdateRequest(
            String allergenName,
            ClinicalSeverity severity,
            String reactionType,
            String notes
    ) {}

    public record Response(
            Long id,
            String allergenName,
            ClinicalSeverity severity,
            String reactionType,
            String notes
    ) {}
}
