package com.citamedica.backend.adapter.in.dto.prescription;

import com.citamedica.backend.domain.model.ElectronicPrescription;
import com.citamedica.backend.domain.model.ElectronicPrescriptionLine;
import com.citamedica.backend.domain.model.ElectronicPrescriptionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class ElectronicPrescriptionDtos {
    private ElectronicPrescriptionDtos() {}

    public record LineRequest(
            @NotBlank @Size(max = 512) String medicationName,
            @Size(max = 255) String dosage,
            @Size(max = 255) String frequency,
            Integer durationDays,
            @Size(max = 128) String route,
            @Size(max = 4000) String instructions
    ) {}

    public record CreateElectronicPrescriptionRequest(
            @NotNull Long prescriberDoctorId,
            Long appointmentId,
            LocalDate validUntil,
            @Size(max = 4000) String notes,
            @NotEmpty @Valid List<LineRequest> lines
    ) {}

    public record LineResponse(
            Long id,
            String medicationName,
            String dosage,
            String frequency,
            Integer durationDays,
            String route,
            String instructions,
            int sortOrder
    ) {
        public static LineResponse from(ElectronicPrescriptionLine line) {
            return new LineResponse(
                    line.getId(),
                    line.getMedicationName(),
                    line.getDosage(),
                    line.getFrequency(),
                    line.getDurationDays(),
                    line.getRoute(),
                    line.getInstructions(),
                    line.getSortOrder());
        }
    }

    public record ElectronicPrescriptionResponse(
            Long id,
            Long patientId,
            Long prescriberDoctorId,
            String prescriberName,
            Long appointmentId,
            ElectronicPrescriptionStatus status,
            LocalDateTime issuedAt,
            LocalDate validUntil,
            String notes,
            List<LineResponse> lines
    ) {
        public static ElectronicPrescriptionResponse from(ElectronicPrescription e) {
            return new ElectronicPrescriptionResponse(
                    e.getId(),
                    e.getPatient().getId(),
                    e.getPrescriber().getId(),
                    e.getPrescriber().getFullName(),
                    e.getAppointment() != null ? e.getAppointment().getId() : null,
                    e.getStatus(),
                    e.getIssuedAt(),
                    e.getValidUntil(),
                    e.getNotes(),
                    e.getLines().stream().map(LineResponse::from).toList());
        }
    }
}
