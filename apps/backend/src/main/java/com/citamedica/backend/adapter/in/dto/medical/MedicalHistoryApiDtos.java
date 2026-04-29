package com.citamedica.backend.adapter.in.dto.medical;

import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.medical.ClinicalSeverity;

import java.time.LocalDateTime;
import java.util.List;

public final class MedicalHistoryApiDtos {
    private MedicalHistoryApiDtos() {}

    public record TimelineEventResponse(
            Long id,
            String eventType,
            LocalDateTime eventDate,
            String title,
            String description,
            Long sourceRecordId,
            String sourceRecordType
    ) {}

    public record HealthSummaryResponse(
            int activeConditionCount,
            int activeMedicationCount,
            int allergyCount,
            int severeAllergyCount,
            List<ConditionSnippet> activeConditions,
            List<MedicationSnippet> activeMedications,
            List<AllergySnippet> allergies,
            List<TimelineEventResponse> recentEvents
    ) {}

    public record ConditionSnippet(Long id, String name, ClinicalSeverity severity) {}

    public record MedicationSnippet(Long id, String name, String dosage, String frequency) {}

    public record AllergySnippet(Long id, String allergen, ClinicalSeverity severity) {}

    public record MedicalHistoryOverviewResponse(
            Long patientId,
            HealthSummaryResponse summary,
            long totalTimelineEvents
    ) {}

    public record AppointmentHistoryRowResponse(
            Long id,
            LocalDateTime startAt,
            LocalDateTime endAt,
            AppointmentStatus status,
            String type,
            String notes
    ) {}

    public record DocumentResponse(
            Long id,
            String documentType,
            String mimeType,
            long fileSize,
            String fileHash,
            String uploadedBy,
            LocalDateTime uploadedAt,
            int versionId,
            String notes
    ) {}

    public record DocumentVersionResponse(Long versionId, LocalDateTime uploadedAt, String fileHash) {}

    public record AuditLogEntryResponse(
            Long id,
            String recordType,
            Long recordId,
            String action,
            LocalDateTime createdAt,
            String actorId
    ) {}
}
