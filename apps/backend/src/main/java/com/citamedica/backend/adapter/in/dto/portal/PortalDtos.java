package com.citamedica.backend.adapter.in.dto.portal;

import com.citamedica.backend.adapter.in.dto.medical.AllergyDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalConditionDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalHistoryApiDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicationDtos;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.model.medical.ClinicalSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PortalDtos {
    private PortalDtos() {}

    public record MeResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            String languagePreference,
            boolean portalAccessEnabled
    ) {
        public static MeResponse from(Patient patient) {
            return new MeResponse(
                    patient.getId(),
                    patient.getFullName(),
                    patient.getEmail(),
                    patient.getPhone(),
                    patient.getLanguagePreference(),
                    patient.isPortalAccessEnabled());
        }
    }

    public record ActivatePortalRequest(
            @NotBlank @Size(min = 8, max = 128) String password
    ) {}

    public record ChangePortalPasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, max = 128) String newPassword
    ) {}

    public record UpdatePortalProfileRequest(
            @Size(max = 64) String phone,
            @Size(max = 16) String languagePreference
    ) {}

    public record PortalConditionResponse(
            Long id,
            String conditionName,
            ClinicalSeverity severity,
            LocalDate onsetDate,
            LocalDate resolutionDate
    ) {
        public static PortalConditionResponse from(MedicalConditionDtos.Response r) {
            return new PortalConditionResponse(
                    r.id(), r.conditionName(), r.severity(), r.onsetDate(), r.resolutionDate());
        }
    }

    public record PortalMedicationResponse(
            Long id,
            String medicationName,
            String dosage,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String indication
    ) {
        public static PortalMedicationResponse from(MedicationDtos.Response r) {
            return new PortalMedicationResponse(
                    r.id(),
                    r.medicationName(),
                    r.dosage(),
                    r.frequency(),
                    r.startDate(),
                    r.endDate(),
                    r.indication());
        }
    }

    public record PortalAllergyResponse(
            Long id,
            String allergenName,
            ClinicalSeverity severity,
            String reactionType
    ) {
        public static PortalAllergyResponse from(AllergyDtos.Response r) {
            return new PortalAllergyResponse(r.id(), r.allergenName(), r.severity(), r.reactionType());
        }
    }

    public record PortalAppointmentRow(
            Long id,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String type,
            String statusLabel
    ) {
        public static PortalAppointmentRow from(MedicalHistoryApiDtos.AppointmentHistoryRowResponse r) {
            return new PortalAppointmentRow(
                    r.id(), r.startAt(), r.endAt(), r.type(), r.status().name());
        }
    }

    public record PortalHealthSummary(
            int activeConditionCount,
            int activeMedicationCount,
            int allergyCount,
            int severeAllergyCount,
            java.util.List<PortalConditionResponse> activeConditions,
            java.util.List<PortalMedicationResponse> activeMedications,
            java.util.List<PortalAllergyResponse> allergies,
            java.util.List<MedicalHistoryApiDtos.TimelineEventResponse> recentEvents
    ) {
        public static PortalHealthSummary from(MedicalHistoryApiDtos.HealthSummaryResponse s) {
            var conds = s.activeConditions().stream()
                    .map(cs -> new PortalConditionResponse(cs.id(), cs.name(), cs.severity(), null, null))
                    .toList();
            var meds = s.activeMedications().stream()
                    .map(ms -> new PortalMedicationResponse(
                            ms.id(), ms.name(), ms.dosage(), ms.frequency(), null, null, null))
                    .toList();
            var all = s.allergies().stream()
                    .map(a -> new PortalAllergyResponse(a.id(), a.allergen(), a.severity(), null))
                    .toList();
            return new PortalHealthSummary(
                    s.activeConditionCount(),
                    s.activeMedicationCount(),
                    s.allergyCount(),
                    s.severeAllergyCount(),
                    conds,
                    meds,
                    all,
                    s.recentEvents());
        }
    }
}
