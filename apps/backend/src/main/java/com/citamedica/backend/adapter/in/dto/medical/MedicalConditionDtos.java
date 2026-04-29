package com.citamedica.backend.adapter.in.dto.medical;

import com.citamedica.backend.domain.model.medical.ClinicalSeverity;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public final class MedicalConditionDtos {
    private MedicalConditionDtos() {}

    public record CreateRequest(
            @NotBlank String conditionName,
            ClinicalSeverity severity,
            LocalDate onsetDate,
            LocalDate resolutionDate,
            String notes
    ) {}

    public record UpdateRequest(
            String conditionName,
            ClinicalSeverity severity,
            LocalDate onsetDate,
            LocalDate resolutionDate,
            String notes
    ) {}

    public record Response(
            Long id,
            String conditionName,
            ClinicalSeverity severity,
            LocalDate onsetDate,
            LocalDate resolutionDate,
            String notes
    ) {}
}
