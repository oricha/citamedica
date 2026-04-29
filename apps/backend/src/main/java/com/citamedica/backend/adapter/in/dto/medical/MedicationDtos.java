package com.citamedica.backend.adapter.in.dto.medical;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public final class MedicationDtos {
    private MedicationDtos() {}

    public record CreateRequest(
            @NotBlank String medicationName,
            String dosage,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String indication,
            String contraindications
    ) {}

    public record UpdateRequest(
            String medicationName,
            String dosage,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String indication,
            String contraindications
    ) {}

    public record Response(
            Long id,
            String medicationName,
            String dosage,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String indication,
            String contraindications,
            List<String> interactionWarnings
    ) {}
}
