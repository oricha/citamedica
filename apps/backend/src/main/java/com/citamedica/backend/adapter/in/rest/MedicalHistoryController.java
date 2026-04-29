package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.medical.AllergyDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalConditionDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalHistoryApiDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicationDtos;
import com.citamedica.backend.adapter.in.dto.medical.ProcedureDtos;
import com.citamedica.backend.application.medical.PatientMedicalDocumentService;
import com.citamedica.backend.application.medical.PatientMedicalHistoryQueryService;
import com.citamedica.backend.application.medical.PatientMedicalRecordCommandService;
import com.citamedica.backend.domain.model.medical.MedicalDocumentType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/patients/{patientId}")
@PreAuthorize("hasAnyRole('STAFF','DOCTOR','ADMIN')")
public class MedicalHistoryController {

    private final PatientMedicalHistoryQueryService queryService;
    private final PatientMedicalRecordCommandService commandService;
    private final PatientMedicalDocumentService documentService;

    public MedicalHistoryController(
            PatientMedicalHistoryQueryService queryService,
            PatientMedicalRecordCommandService commandService,
            PatientMedicalDocumentService documentService) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.documentService = documentService;
    }

    @GetMapping("/medical-history")
    public MedicalHistoryApiDtos.MedicalHistoryOverviewResponse medicalHistoryOverview(@PathVariable Long patientId) {
        return queryService.overview(patientId);
    }

    @GetMapping("/timeline")
    public Page<MedicalHistoryApiDtos.TimelineEventResponse> timeline(
            @PathVariable Long patientId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @PageableDefault(size = 20) Pageable pageable) {
        return queryService.timeline(patientId, type, dateFrom, dateTo, pageable);
    }

    @GetMapping("/health-summary")
    public MedicalHistoryApiDtos.HealthSummaryResponse healthSummary(@PathVariable Long patientId) {
        return queryService.healthSummary(patientId);
    }

    @GetMapping("/medical-history/audit")
    public Page<MedicalHistoryApiDtos.AuditLogEntryResponse> audit(
            @PathVariable Long patientId,
            @PageableDefault(size = 50) Pageable pageable) {
        return queryService.auditLog(patientId, pageable);
    }

    @GetMapping("/conditions")
    public Page<MedicalConditionDtos.Response> listConditions(
            @PathVariable Long patientId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return queryService.conditions(patientId, active, q, pageable);
    }

    @PostMapping("/conditions")
    public ResponseEntity<MedicalConditionDtos.Response> createCondition(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalConditionDtos.CreateRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.createCondition(patientId, body));
    }

    @PatchMapping("/conditions/{conditionId}")
    public MedicalConditionDtos.Response updateCondition(
            @PathVariable Long patientId,
            @PathVariable Long conditionId,
            @Valid @RequestBody MedicalConditionDtos.UpdateRequest body) {
        return commandService.updateCondition(patientId, conditionId, body);
    }

    @DeleteMapping("/conditions/{conditionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCondition(
            @PathVariable Long patientId,
            @PathVariable Long conditionId) {
        commandService.softDeleteCondition(patientId, conditionId);
    }

    @GetMapping("/medications")
    public Page<MedicationDtos.Response> listMedications(
            @PathVariable Long patientId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return queryService.medications(patientId, active, q, pageable);
    }

    @PostMapping("/medications")
    public ResponseEntity<MedicationDtos.Response> createMedication(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicationDtos.CreateRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.createMedication(patientId, body));
    }

    @PatchMapping("/medications/{medicationId}")
    public MedicationDtos.Response updateMedication(
            @PathVariable Long patientId,
            @PathVariable Long medicationId,
            @Valid @RequestBody MedicationDtos.UpdateRequest body) {
        return commandService.updateMedication(patientId, medicationId, body);
    }

    @DeleteMapping("/medications/{medicationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedication(
            @PathVariable Long patientId,
            @PathVariable Long medicationId) {
        commandService.softDeleteMedication(patientId, medicationId);
    }

    @GetMapping("/allergies")
    public Page<AllergyDtos.Response> listAllergies(
            @PathVariable Long patientId,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return queryService.allergies(patientId, q, pageable);
    }

    @PostMapping("/allergies")
    public ResponseEntity<AllergyDtos.Response> createAllergy(
            @PathVariable Long patientId,
            @Valid @RequestBody AllergyDtos.CreateRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.createAllergy(patientId, body));
    }

    @PatchMapping("/allergies/{allergyId}")
    public AllergyDtos.Response updateAllergy(
            @PathVariable Long patientId,
            @PathVariable Long allergyId,
            @Valid @RequestBody AllergyDtos.UpdateRequest body) {
        return commandService.updateAllergy(patientId, allergyId, body);
    }

    @DeleteMapping("/allergies/{allergyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllergy(
            @PathVariable Long patientId,
            @PathVariable Long allergyId) {
        commandService.softDeleteAllergy(patientId, allergyId);
    }

    @GetMapping("/procedures")
    public Page<ProcedureDtos.Response> listProcedures(
            @PathVariable Long patientId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return queryService.procedures(patientId, dateFrom, dateTo, q, pageable);
    }

    @PostMapping("/procedures")
    public ResponseEntity<ProcedureDtos.Response> createProcedure(
            @PathVariable Long patientId,
            @Valid @RequestBody ProcedureDtos.CreateRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.createProcedure(patientId, body));
    }

    @PatchMapping("/procedures/{procedureId}")
    public ProcedureDtos.Response updateProcedure(
            @PathVariable Long patientId,
            @PathVariable Long procedureId,
            @Valid @RequestBody ProcedureDtos.UpdateRequest body) {
        return commandService.updateProcedure(patientId, procedureId, body);
    }

    @DeleteMapping("/procedures/{procedureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProcedure(
            @PathVariable Long patientId,
            @PathVariable Long procedureId) {
        commandService.softDeleteProcedure(patientId, procedureId);
    }

    @GetMapping("/appointments/summary")
    public Page<MedicalHistoryApiDtos.AppointmentHistoryRowResponse> appointmentSummary(
            @PathVariable Long patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return queryService.appointmentSummary(patientId, pageable);
    }

    @GetMapping("/documents")
    public Page<MedicalHistoryApiDtos.DocumentResponse> listDocuments(
            @PathVariable Long patientId,
            @RequestParam(required = false) MedicalDocumentType type,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return documentService.list(patientId, type, dateFrom, dateTo, search, pageable);
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalHistoryApiDtos.DocumentResponse> uploadDocument(
            @PathVariable Long patientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam MedicalDocumentType documentType,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.upload(patientId, file, documentType, notes));
    }
}
