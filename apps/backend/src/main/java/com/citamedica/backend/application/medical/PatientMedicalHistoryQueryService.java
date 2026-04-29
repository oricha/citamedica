package com.citamedica.backend.application.medical;

import com.citamedica.backend.adapter.in.dto.medical.MedicalConditionDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalHistoryApiDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicationDtos;
import com.citamedica.backend.adapter.in.dto.medical.AllergyDtos;
import com.citamedica.backend.adapter.in.dto.medical.ProcedureDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.MedicalConditionJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.MedicalHistoryAuditLogJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.MedicalHistoryEventJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.MedicalProcedureJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.PatientAllergyJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.PatientMedicationJpaRepository;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.medical.ClinicalSeverity;
import com.citamedica.backend.domain.model.medical.MedicalCondition;
import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditLog;
import com.citamedica.backend.domain.model.medical.MedicalHistoryEvent;
import com.citamedica.backend.domain.model.medical.MedicalProcedure;
import com.citamedica.backend.domain.model.medical.PatientAllergy;
import com.citamedica.backend.domain.model.medical.PatientMedication;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.service.medical.DrugInteractionCheckService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientMedicalHistoryQueryService {

    private final PatientRepository patientRepository;
    private final MedicalConditionJpaRepository conditionJpaRepository;
    private final PatientMedicationJpaRepository medicationJpaRepository;
    private final PatientAllergyJpaRepository allergyJpaRepository;
    private final MedicalProcedureJpaRepository procedureJpaRepository;
    private final MedicalHistoryEventJpaRepository eventJpaRepository;
    private final MedicalHistoryAuditLogJpaRepository auditLogJpaRepository;
    private final AppointmentRepository appointmentRepository;
    private final DrugInteractionCheckService drugInteractionCheckService;

    public PatientMedicalHistoryQueryService(
            PatientRepository patientRepository,
            MedicalConditionJpaRepository conditionJpaRepository,
            PatientMedicationJpaRepository medicationJpaRepository,
            PatientAllergyJpaRepository allergyJpaRepository,
            MedicalProcedureJpaRepository procedureJpaRepository,
            MedicalHistoryEventJpaRepository eventJpaRepository,
            MedicalHistoryAuditLogJpaRepository auditLogJpaRepository,
            AppointmentRepository appointmentRepository,
            DrugInteractionCheckService drugInteractionCheckService) {
        this.patientRepository = patientRepository;
        this.conditionJpaRepository = conditionJpaRepository;
        this.medicationJpaRepository = medicationJpaRepository;
        this.allergyJpaRepository = allergyJpaRepository;
        this.procedureJpaRepository = procedureJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.auditLogJpaRepository = auditLogJpaRepository;
        this.appointmentRepository = appointmentRepository;
        this.drugInteractionCheckService = drugInteractionCheckService;
    }

    private void ensurePatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
    }

    @Transactional(readOnly = true)
    public MedicalHistoryApiDtos.MedicalHistoryOverviewResponse overview(Long patientId) {
        ensurePatient(patientId);
        HealthBundle bundle = loadHealthBundle(patientId);
        long totalTimeline = eventJpaRepository.countByPatient_Id(patientId);
        return new MedicalHistoryApiDtos.MedicalHistoryOverviewResponse(
                patientId,
                bundle.summary(),
                totalTimeline);
    }

    @Transactional(readOnly = true)
    public Page<MedicalHistoryApiDtos.TimelineEventResponse> timeline(
            Long patientId,
            String eventType,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable) {
        ensurePatient(patientId);
        Page<MedicalHistoryEvent> page;
        LocalDateTime fromDt = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime toDt = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;
        if (fromDt != null && toDt != null) {
            page = eventJpaRepository.findByPatientAndEventDateBetween(patientId, fromDt, toDt, pageable);
        } else if (eventType != null && !eventType.isBlank()) {
            page = eventJpaRepository.findByPatient_IdAndEventTypeOrderByEventDateDesc(patientId, eventType, pageable);
        } else {
            page = eventJpaRepository.findByPatient_IdOrderByEventDateDesc(patientId, pageable);
        }
        return page.map(PatientMedicalHistoryQueryService::toTimelineDto);
    }

    @Transactional(readOnly = true)
    public MedicalHistoryApiDtos.HealthSummaryResponse healthSummary(Long patientId) {
        ensurePatient(patientId);
        return loadHealthBundle(patientId).summary();
    }

    @Transactional(readOnly = true)
    public Page<MedicalConditionDtos.Response> conditions(
            Long patientId,
            Boolean activeOnly,
            String q,
            Pageable pageable) {
        ensurePatient(patientId);
        Page<MedicalCondition> page;
        if (Boolean.TRUE.equals(activeOnly)) {
            if (q != null && !q.isBlank()) {
                page = conditionJpaRepository
                        .findByPatient_IdAndResolutionDateIsNullAndDeletedAtIsNullAndConditionNameContainingIgnoreCase(
                                patientId, q, pageable);
            } else {
                page = conditionJpaRepository.findByPatient_IdAndResolutionDateIsNullAndDeletedAtIsNull(
                        patientId, pageable);
            }
        } else if (q != null && !q.isBlank()) {
            page = conditionJpaRepository.findByPatient_IdAndConditionNameContainingIgnoreCaseAndDeletedAtIsNull(
                    patientId, q, pageable);
        } else {
            page = conditionJpaRepository.findByPatient_IdAndDeletedAtIsNull(patientId, pageable);
        }
        return page.map(PatientMedicalHistoryQueryService::toConditionDto);
    }

    @Transactional(readOnly = true)
    public Page<MedicationDtos.Response> medications(
            Long patientId,
            Boolean activeOnly,
            String q,
            Pageable pageable) {
        ensurePatient(patientId);
        LocalDate today = LocalDate.now();
        Page<PatientMedication> page;
        if (Boolean.TRUE.equals(activeOnly)) {
            page = medicationJpaRepository.findActivePage(patientId, today, pageable);
        } else if (q != null && !q.isBlank()) {
            page = medicationJpaRepository.findByPatient_IdAndMedicationNameContainingIgnoreCaseAndDeletedAtIsNull(
                    patientId, q, pageable);
        } else {
            page = medicationJpaRepository.findByPatient_IdAndDeletedAtIsNull(patientId, pageable);
        }
        return page.map(m -> toMedicationDto(m, patientId));
    }

    @Transactional(readOnly = true)
    public Page<AllergyDtos.Response> allergies(Long patientId, String q, Pageable pageable) {
        ensurePatient(patientId);
        Page<PatientAllergy> page = (q != null && !q.isBlank())
                ? allergyJpaRepository.findByPatient_IdAndAllergenNameContainingIgnoreCaseAndDeletedAtIsNull(
                patientId, q, pageable)
                : allergyJpaRepository.findByPatient_IdAndDeletedAtIsNull(patientId, pageable);
        return page.map(PatientMedicalHistoryQueryService::toAllergyDto);
    }

    @Transactional(readOnly = true)
    public Page<ProcedureDtos.Response> procedures(
            Long patientId,
            LocalDate dateFrom,
            LocalDate dateTo,
            String q,
            Pageable pageable) {
        ensurePatient(patientId);
        Page<MedicalProcedure> page;
        if (dateFrom != null && dateTo != null) {
            page = procedureJpaRepository.findByPatientAndProcedureDateBetween(patientId, dateFrom, dateTo, pageable);
        } else if (q != null && !q.isBlank()) {
            page = procedureJpaRepository.findByPatient_IdAndProcedureNameContainingIgnoreCaseAndDeletedAtIsNull(
                    patientId, q, pageable);
        } else {
            page = procedureJpaRepository.findByPatient_IdAndDeletedAtIsNull(patientId, pageable);
        }
        return page.map(PatientMedicalHistoryQueryService::toProcedureDto);
    }

    @Transactional(readOnly = true)
    public Page<MedicalHistoryApiDtos.AppointmentHistoryRowResponse> appointmentSummary(
            Long patientId,
            Pageable pageable) {
        ensurePatient(patientId);
        Page<Appointment> page = appointmentRepository.findByPatientIdOrderByStartAtDesc(patientId, pageable);
        return page.map(PatientMedicalHistoryQueryService::toAppointmentRow);
    }

    @Transactional(readOnly = true)
    public Page<MedicalHistoryApiDtos.AuditLogEntryResponse> auditLog(Long patientId, Pageable pageable) {
        ensurePatient(patientId);
        return auditLogJpaRepository.findByPatient_IdOrderByCreatedAtDesc(patientId, pageable)
                .map(PatientMedicalHistoryQueryService::toAuditDto);
    }

    private HealthBundle loadHealthBundle(Long patientId) {
        List<MedicalCondition> activeConditions = conditionJpaRepository
                .findByPatient_IdAndResolutionDateIsNullAndDeletedAtIsNull(patientId);
        List<PatientMedication> activeMeds = medicationJpaRepository.findActiveForPatient(patientId, LocalDate.now());
        List<PatientAllergy> allergies = allergyJpaRepository.findByPatient_IdAndDeletedAtIsNullOrderByIdAsc(patientId);
        Page<MedicalHistoryEvent> recent = eventJpaRepository.findByPatient_IdOrderByEventDateDesc(
                patientId, PageRequest.of(0, 10));
        int severe = (int) allergies.stream()
                .filter(a -> a.getSeverity() == ClinicalSeverity.SEVERE)
                .count();
        var conditionSnippets = activeConditions.stream()
                .map(c -> new MedicalHistoryApiDtos.ConditionSnippet(c.getId(), c.getConditionName(), c.getSeverity()))
                .collect(Collectors.toList());
        var medSnippets = activeMeds.stream()
                .map(m -> new MedicalHistoryApiDtos.MedicationSnippet(
                        m.getId(), m.getMedicationName(), m.getDosage(), m.getFrequency()))
                .collect(Collectors.toList());
        var allergySnippets = allergies.stream()
                .map(a -> new MedicalHistoryApiDtos.AllergySnippet(a.getId(), a.getAllergenName(), a.getSeverity()))
                .collect(Collectors.toList());
        var recentDtos = recent.getContent().stream()
                .map(PatientMedicalHistoryQueryService::toTimelineDto)
                .collect(Collectors.toList());
        MedicalHistoryApiDtos.HealthSummaryResponse summary = new MedicalHistoryApiDtos.HealthSummaryResponse(
                activeConditions.size(),
                activeMeds.size(),
                allergies.size(),
                severe,
                conditionSnippets,
                medSnippets,
                allergySnippets,
                recentDtos);
        return new HealthBundle(summary);
    }

    private MedicationDtos.Response toMedicationDto(PatientMedication m, Long patientId) {
        List<String> warnings = drugInteractionCheckService.checkAgainstPatientMedications(
                patientId, m.getMedicationName());
        return new MedicationDtos.Response(
                m.getId(),
                m.getMedicationName(),
                m.getDosage(),
                m.getFrequency(),
                m.getStartDate(),
                m.getEndDate(),
                m.getIndication(),
                m.getContraindications(),
                warnings != null ? warnings : Collections.emptyList());
    }

    private static MedicalHistoryApiDtos.TimelineEventResponse toTimelineDto(MedicalHistoryEvent e) {
        return new MedicalHistoryApiDtos.TimelineEventResponse(
                e.getId(),
                e.getEventType(),
                e.getEventDate(),
                e.getEventTitle(),
                e.getEventDescription(),
                e.getSourceRecordId(),
                e.getSourceRecordType());
    }

    private static MedicalConditionDtos.Response toConditionDto(MedicalCondition c) {
        return new MedicalConditionDtos.Response(
                c.getId(),
                c.getConditionName(),
                c.getSeverity(),
                c.getOnsetDate(),
                c.getResolutionDate(),
                c.getNotes());
    }

    private static AllergyDtos.Response toAllergyDto(PatientAllergy a) {
        return new AllergyDtos.Response(
                a.getId(),
                a.getAllergenName(),
                a.getSeverity(),
                a.getReactionType(),
                a.getNotes());
    }

    private static ProcedureDtos.Response toProcedureDto(MedicalProcedure p) {
        return new ProcedureDtos.Response(
                p.getId(),
                p.getProcedureName(),
                p.getProcedureDate(),
                p.getOutcome(),
                p.getRelatedCondition() != null ? p.getRelatedCondition().getId() : null,
                p.getNotes());
    }

    private static MedicalHistoryApiDtos.AppointmentHistoryRowResponse toAppointmentRow(Appointment a) {
        return new MedicalHistoryApiDtos.AppointmentHistoryRowResponse(
                a.getId(),
                a.getStartAt(),
                a.getEndAt(),
                a.getStatus(),
                a.getType(),
                a.getNotes());
    }

    private static MedicalHistoryApiDtos.AuditLogEntryResponse toAuditDto(MedicalHistoryAuditLog log) {
        return new MedicalHistoryApiDtos.AuditLogEntryResponse(
                log.getId(),
                log.getRecordType(),
                log.getRecordId(),
                log.getAction().name(),
                log.getCreatedAt(),
                log.getActorId());
    }

    private record HealthBundle(MedicalHistoryApiDtos.HealthSummaryResponse summary) {}
}
