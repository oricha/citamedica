package com.citamedica.backend.application.medical;

import com.citamedica.backend.adapter.in.dto.medical.AllergyDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalConditionDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicationDtos;
import com.citamedica.backend.adapter.in.dto.medical.ProcedureDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.*;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.model.medical.*;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.service.medical.DrugInteractionCheckService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PatientMedicalRecordCommandService {

    private final PatientRepository patientRepository;
    private final MedicalConditionJpaRepository conditionJpaRepository;
    private final PatientMedicationJpaRepository medicationJpaRepository;
    private final PatientAllergyJpaRepository allergyJpaRepository;
    private final MedicalProcedureJpaRepository procedureJpaRepository;
    private final MedicalHistoryRecorder recorder;
    private final DrugInteractionCheckService drugInteractionCheckService;
    private final ObjectMapper objectMapper;

    public PatientMedicalRecordCommandService(
            PatientRepository patientRepository,
            MedicalConditionJpaRepository conditionJpaRepository,
            PatientMedicationJpaRepository medicationJpaRepository,
            PatientAllergyJpaRepository allergyJpaRepository,
            MedicalProcedureJpaRepository procedureJpaRepository,
            MedicalHistoryRecorder recorder,
            DrugInteractionCheckService drugInteractionCheckService,
            ObjectMapper objectMapper) {
        this.patientRepository = patientRepository;
        this.conditionJpaRepository = conditionJpaRepository;
        this.medicationJpaRepository = medicationJpaRepository;
        this.allergyJpaRepository = allergyJpaRepository;
        this.procedureJpaRepository = procedureJpaRepository;
        this.recorder = recorder;
        this.drugInteractionCheckService = drugInteractionCheckService;
        this.objectMapper = objectMapper;
    }

    private Patient loadPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
    }

    private String actor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? auth.getName() : "system";
    }

    private String json(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Transactional
    public MedicalConditionDtos.Response createCondition(Long patientId, MedicalConditionDtos.CreateRequest req) {
        Patient patient = loadPatient(patientId);
        MedicalCondition c = new MedicalCondition();
        c.setPatient(patient);
        c.setConditionName(req.conditionName());
        c.setSeverity(req.severity());
        c.setOnsetDate(req.onsetDate());
        c.setResolutionDate(req.resolutionDate());
        c.setNotes(req.notes());
        MedicalCondition saved = conditionJpaRepository.save(c);
        recorder.appendTimelineEvent(patient, "CONDITION", LocalDateTime.now(),
                "Condition recorded: " + saved.getConditionName(),
                saved.getNotes(),
                saved.getId(),
                MedicalHistorySourceTypes.CONDITION);
        recorder.audit(patient, MedicalHistorySourceTypes.CONDITION, saved.getId(), MedicalHistoryAuditAction.CREATE,
                json(Map.of("conditionName", saved.getConditionName())), actor(), null);
        return toConditionResponse(saved);
    }

    @Transactional
    public MedicalConditionDtos.Response updateCondition(Long patientId, Long conditionId, MedicalConditionDtos.UpdateRequest req) {
        MedicalCondition c = conditionJpaRepository.findById(conditionId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Condition not found: " + conditionId));
        if (req.conditionName() != null) {
            c.setConditionName(req.conditionName());
        }
        if (req.severity() != null) {
            c.setSeverity(req.severity());
        }
        if (req.onsetDate() != null) {
            c.setOnsetDate(req.onsetDate());
        }
        if (req.resolutionDate() != null) {
            c.setResolutionDate(req.resolutionDate());
        }
        if (req.notes() != null) {
            c.setNotes(req.notes());
        }
        c.setUpdatedAt(LocalDateTime.now());
        MedicalCondition saved = conditionJpaRepository.save(c);
        recorder.audit(c.getPatient(), MedicalHistorySourceTypes.CONDITION, saved.getId(), MedicalHistoryAuditAction.UPDATE,
                json(Map.of("conditionId", saved.getId())), actor(), null);
        return toConditionResponse(saved);
    }

    @Transactional
    public void softDeleteCondition(Long patientId, Long conditionId) {
        MedicalCondition c = conditionJpaRepository.findById(conditionId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Condition not found: " + conditionId));
        c.setDeletedAt(LocalDateTime.now());
        conditionJpaRepository.save(c);
        recorder.appendTimelineEvent(c.getPatient(), "CONDITION", LocalDateTime.now(),
                "Condition removed from active chart: " + c.getConditionName(),
                null,
                c.getId(),
                MedicalHistorySourceTypes.CONDITION);
        recorder.audit(c.getPatient(), MedicalHistorySourceTypes.CONDITION, c.getId(), MedicalHistoryAuditAction.SOFT_DELETE,
                json(Map.of("conditionId", conditionId)), actor(), null);
    }

    @Transactional
    public MedicationDtos.Response createMedication(Long patientId, MedicationDtos.CreateRequest req) {
        Patient patient = loadPatient(patientId);
        List<String> warnings = drugInteractionCheckService.checkAgainstPatientMedications(patientId, req.medicationName());
        PatientMedication m = new PatientMedication();
        m.setPatient(patient);
        m.setMedicationName(req.medicationName());
        m.setDosage(req.dosage());
        m.setFrequency(req.frequency());
        m.setStartDate(req.startDate());
        m.setEndDate(req.endDate());
        m.setIndication(req.indication());
        m.setContraindications(req.contraindications());
        PatientMedication saved = medicationJpaRepository.save(m);
        recorder.appendTimelineEvent(patient, "MEDICATION", LocalDateTime.now(),
                "Medication added: " + saved.getMedicationName(),
                saved.getIndication(),
                saved.getId(),
                MedicalHistorySourceTypes.MEDICATION);
        recorder.audit(patient, MedicalHistorySourceTypes.MEDICATION, saved.getId(), MedicalHistoryAuditAction.CREATE,
                json(Map.of("medicationName", saved.getMedicationName())), actor(), null);
        return toMedicationResponse(saved, warnings);
    }

    @Transactional
    public MedicationDtos.Response updateMedication(Long patientId, Long medicationId, MedicationDtos.UpdateRequest req) {
        PatientMedication m = medicationJpaRepository.findById(medicationId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Medication not found: " + medicationId));
        if (req.medicationName() != null) {
            m.setMedicationName(req.medicationName());
        }
        if (req.dosage() != null) {
            m.setDosage(req.dosage());
        }
        if (req.frequency() != null) {
            m.setFrequency(req.frequency());
        }
        if (req.startDate() != null) {
            m.setStartDate(req.startDate());
        }
        if (req.endDate() != null) {
            m.setEndDate(req.endDate());
        }
        if (req.indication() != null) {
            m.setIndication(req.indication());
        }
        if (req.contraindications() != null) {
            m.setContraindications(req.contraindications());
        }
        m.setUpdatedAt(LocalDateTime.now());
        PatientMedication saved = medicationJpaRepository.save(m);
        recorder.audit(m.getPatient(), MedicalHistorySourceTypes.MEDICATION, saved.getId(), MedicalHistoryAuditAction.UPDATE,
                json(Map.of("medicationId", saved.getId())), actor(), null);
        return toMedicationResponse(saved, List.of());
    }

    @Transactional
    public void softDeleteMedication(Long patientId, Long medicationId) {
        PatientMedication m = medicationJpaRepository.findById(medicationId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Medication not found: " + medicationId));
        m.setDeletedAt(LocalDateTime.now());
        medicationJpaRepository.save(m);
        recorder.audit(m.getPatient(), MedicalHistorySourceTypes.MEDICATION, m.getId(), MedicalHistoryAuditAction.SOFT_DELETE,
                json(Map.of("medicationId", medicationId)), actor(), null);
    }

    @Transactional
    public AllergyDtos.Response createAllergy(Long patientId, AllergyDtos.CreateRequest req) {
        Patient patient = loadPatient(patientId);
        PatientAllergy a = new PatientAllergy();
        a.setPatient(patient);
        a.setAllergenName(req.allergenName());
        a.setSeverity(req.severity());
        a.setReactionType(req.reactionType());
        a.setNotes(req.notes());
        PatientAllergy saved = allergyJpaRepository.save(a);
        recorder.appendTimelineEvent(patient, "ALLERGY", LocalDateTime.now(),
                "Allergy recorded: " + saved.getAllergenName(),
                saved.getNotes(),
                saved.getId(),
                MedicalHistorySourceTypes.ALLERGY);
        recorder.audit(patient, MedicalHistorySourceTypes.ALLERGY, saved.getId(), MedicalHistoryAuditAction.CREATE,
                json(Map.of("allergen", saved.getAllergenName())), actor(), null);
        return toAllergyResponse(saved);
    }

    @Transactional
    public AllergyDtos.Response updateAllergy(Long patientId, Long allergyId, AllergyDtos.UpdateRequest req) {
        PatientAllergy a = allergyJpaRepository.findById(allergyId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Allergy not found: " + allergyId));
        if (req.allergenName() != null) {
            a.setAllergenName(req.allergenName());
        }
        if (req.severity() != null) {
            a.setSeverity(req.severity());
        }
        if (req.reactionType() != null) {
            a.setReactionType(req.reactionType());
        }
        if (req.notes() != null) {
            a.setNotes(req.notes());
        }
        a.setUpdatedAt(LocalDateTime.now());
        PatientAllergy saved = allergyJpaRepository.save(a);
        recorder.audit(a.getPatient(), MedicalHistorySourceTypes.ALLERGY, saved.getId(), MedicalHistoryAuditAction.UPDATE,
                json(Map.of("allergyId", saved.getId())), actor(), null);
        return toAllergyResponse(saved);
    }

    @Transactional
    public void softDeleteAllergy(Long patientId, Long allergyId) {
        PatientAllergy a = allergyJpaRepository.findById(allergyId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Allergy not found: " + allergyId));
        a.setDeletedAt(LocalDateTime.now());
        allergyJpaRepository.save(a);
        recorder.audit(a.getPatient(), MedicalHistorySourceTypes.ALLERGY, a.getId(), MedicalHistoryAuditAction.SOFT_DELETE,
                json(Map.of("allergyId", allergyId)), actor(), null);
    }

    @Transactional
    public ProcedureDtos.Response createProcedure(Long patientId, ProcedureDtos.CreateRequest req) {
        Patient patient = loadPatient(patientId);
        MedicalProcedure p = new MedicalProcedure();
        p.setPatient(patient);
        p.setProcedureName(req.procedureName());
        p.setProcedureDate(req.procedureDate());
        p.setOutcome(req.outcome());
        p.setNotes(req.notes());
        if (req.relatedConditionId() != null) {
            MedicalCondition cond = conditionJpaRepository.findById(req.relatedConditionId())
                    .filter(c -> c.getPatient().getId().equals(patientId) && c.getDeletedAt() == null)
                    .orElseThrow(() -> new EntityNotFoundDomainException("Condition not found: " + req.relatedConditionId()));
            p.setRelatedCondition(cond);
        }
        MedicalProcedure saved = procedureJpaRepository.save(p);
        recorder.appendTimelineEvent(patient, "PROCEDURE", saved.getProcedureDate().atStartOfDay(),
                "Procedure: " + saved.getProcedureName(),
                saved.getOutcome(),
                saved.getId(),
                MedicalHistorySourceTypes.PROCEDURE);
        recorder.audit(patient, MedicalHistorySourceTypes.PROCEDURE, saved.getId(), MedicalHistoryAuditAction.CREATE,
                json(Map.of("procedureName", saved.getProcedureName())), actor(), null);
        return toProcedureResponse(saved);
    }

    @Transactional
    public ProcedureDtos.Response updateProcedure(Long patientId, Long procedureId, ProcedureDtos.UpdateRequest req) {
        MedicalProcedure p = procedureJpaRepository.findById(procedureId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Procedure not found: " + procedureId));
        if (req.procedureName() != null) {
            p.setProcedureName(req.procedureName());
        }
        if (req.procedureDate() != null) {
            p.setProcedureDate(req.procedureDate());
        }
        if (req.outcome() != null) {
            p.setOutcome(req.outcome());
        }
        if (req.notes() != null) {
            p.setNotes(req.notes());
        }
        if (req.relatedConditionId() != null) {
            if (req.relatedConditionId() == 0L) {
                p.setRelatedCondition(null);
            } else {
                MedicalCondition cond = conditionJpaRepository.findById(req.relatedConditionId())
                        .filter(c -> c.getPatient().getId().equals(patientId) && c.getDeletedAt() == null)
                        .orElseThrow(() -> new EntityNotFoundDomainException("Condition not found: " + req.relatedConditionId()));
                p.setRelatedCondition(cond);
            }
        }
        p.setUpdatedAt(LocalDateTime.now());
        MedicalProcedure saved = procedureJpaRepository.save(p);
        recorder.audit(p.getPatient(), MedicalHistorySourceTypes.PROCEDURE, saved.getId(), MedicalHistoryAuditAction.UPDATE,
                json(Map.of("procedureId", saved.getId())), actor(), null);
        return toProcedureResponse(saved);
    }

    @Transactional
    public void softDeleteProcedure(Long patientId, Long procedureId) {
        MedicalProcedure p = procedureJpaRepository.findById(procedureId)
                .filter(x -> x.getPatient().getId().equals(patientId) && x.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundDomainException("Procedure not found: " + procedureId));
        p.setDeletedAt(LocalDateTime.now());
        procedureJpaRepository.save(p);
        recorder.audit(p.getPatient(), MedicalHistorySourceTypes.PROCEDURE, p.getId(), MedicalHistoryAuditAction.SOFT_DELETE,
                json(Map.of("procedureId", procedureId)), actor(), null);
    }

    private static MedicalConditionDtos.Response toConditionResponse(MedicalCondition c) {
        return new MedicalConditionDtos.Response(
                c.getId(),
                c.getConditionName(),
                c.getSeverity(),
                c.getOnsetDate(),
                c.getResolutionDate(),
                c.getNotes());
    }

    private static MedicationDtos.Response toMedicationResponse(PatientMedication m, List<String> warnings) {
        return new MedicationDtos.Response(
                m.getId(),
                m.getMedicationName(),
                m.getDosage(),
                m.getFrequency(),
                m.getStartDate(),
                m.getEndDate(),
                m.getIndication(),
                m.getContraindications(),
                warnings);
    }

    private static AllergyDtos.Response toAllergyResponse(PatientAllergy a) {
        return new AllergyDtos.Response(
                a.getId(),
                a.getAllergenName(),
                a.getSeverity(),
                a.getReactionType(),
                a.getNotes());
    }

    private static ProcedureDtos.Response toProcedureResponse(MedicalProcedure p) {
        return new ProcedureDtos.Response(
                p.getId(),
                p.getProcedureName(),
                p.getProcedureDate(),
                p.getOutcome(),
                p.getRelatedCondition() != null ? p.getRelatedCondition().getId() : null,
                p.getNotes());
    }
}
