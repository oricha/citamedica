package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.medical.MedicalHistoryApiDtos;
import com.citamedica.backend.adapter.in.dto.notification.NotificationPreferenceRequest;
import com.citamedica.backend.adapter.in.dto.notification.NotificationPreferenceResponse;
import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.adapter.in.dto.portal.PortalDtos;
import com.citamedica.backend.adapter.in.dto.review.ReviewDtos;
import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.application.medical.PatientMedicalDocumentService;
import com.citamedica.backend.application.medical.PatientMedicalHistoryQueryService;
import com.citamedica.backend.application.usecase.CancelMyWaitListEntryUseCase;
import com.citamedica.backend.application.usecase.ChangePatientPortalPasswordUseCase;
import com.citamedica.backend.application.usecase.GetMyElectronicPrescriptionUseCase;
import com.citamedica.backend.application.usecase.CreatePatientReviewUseCase;
import com.citamedica.backend.application.usecase.JoinWaitListUseCase;
import com.citamedica.backend.application.usecase.ListMyElectronicPrescriptionsUseCase;
import com.citamedica.backend.application.usecase.ListMyWaitListEntriesUseCase;
import com.citamedica.backend.application.usecase.ListPatientOwnReviewsUseCase;
import com.citamedica.backend.application.usecase.UpdateNotificationPreferencesUseCase;
import com.citamedica.backend.application.usecase.UpdatePortalProfileUseCase;
import com.citamedica.backend.config.UserPrincipal;
import com.citamedica.backend.domain.model.medical.MedicalDocumentType;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/portal/me")
@PreAuthorize("hasRole('PATIENT')")
public class PatientPortalController {

    private final PatientRepository patientRepository;
    private final PatientMedicalHistoryQueryService medicalHistoryQueryService;
    private final PatientMedicalDocumentService medicalDocumentService;
    private final UpdatePortalProfileUseCase updatePortalProfileUseCase;
    private final ChangePatientPortalPasswordUseCase changePatientPortalPasswordUseCase;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UpdateNotificationPreferencesUseCase updateNotificationPreferencesUseCase;
    private final CreatePatientReviewUseCase createPatientReviewUseCase;
    private final ListPatientOwnReviewsUseCase listPatientOwnReviewsUseCase;
    private final JoinWaitListUseCase joinWaitListUseCase;
    private final ListMyWaitListEntriesUseCase listMyWaitListEntriesUseCase;
    private final CancelMyWaitListEntryUseCase cancelMyWaitListEntryUseCase;
    private final ListMyElectronicPrescriptionsUseCase listMyElectronicPrescriptionsUseCase;
    private final GetMyElectronicPrescriptionUseCase getMyElectronicPrescriptionUseCase;

    public PatientPortalController(
            PatientRepository patientRepository,
            PatientMedicalHistoryQueryService medicalHistoryQueryService,
            PatientMedicalDocumentService medicalDocumentService,
            UpdatePortalProfileUseCase updatePortalProfileUseCase,
            ChangePatientPortalPasswordUseCase changePatientPortalPasswordUseCase,
            NotificationPreferenceRepository notificationPreferenceRepository,
            UpdateNotificationPreferencesUseCase updateNotificationPreferencesUseCase,
            CreatePatientReviewUseCase createPatientReviewUseCase,
            ListPatientOwnReviewsUseCase listPatientOwnReviewsUseCase,
            JoinWaitListUseCase joinWaitListUseCase,
            ListMyWaitListEntriesUseCase listMyWaitListEntriesUseCase,
            CancelMyWaitListEntryUseCase cancelMyWaitListEntryUseCase,
            ListMyElectronicPrescriptionsUseCase listMyElectronicPrescriptionsUseCase,
            GetMyElectronicPrescriptionUseCase getMyElectronicPrescriptionUseCase) {
        this.patientRepository = patientRepository;
        this.medicalHistoryQueryService = medicalHistoryQueryService;
        this.medicalDocumentService = medicalDocumentService;
        this.updatePortalProfileUseCase = updatePortalProfileUseCase;
        this.changePatientPortalPasswordUseCase = changePatientPortalPasswordUseCase;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.updateNotificationPreferencesUseCase = updateNotificationPreferencesUseCase;
        this.createPatientReviewUseCase = createPatientReviewUseCase;
        this.listPatientOwnReviewsUseCase = listPatientOwnReviewsUseCase;
        this.joinWaitListUseCase = joinWaitListUseCase;
        this.listMyWaitListEntriesUseCase = listMyWaitListEntriesUseCase;
        this.cancelMyWaitListEntryUseCase = cancelMyWaitListEntryUseCase;
        this.listMyElectronicPrescriptionsUseCase = listMyElectronicPrescriptionsUseCase;
        this.getMyElectronicPrescriptionUseCase = getMyElectronicPrescriptionUseCase;
    }

    private static Long requirePatientId(UserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient session is invalid");
        }
        return principal.getId();
    }

    @GetMapping
    public PortalDtos.MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        Long patientId = requirePatientId(principal);
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        return PortalDtos.MeResponse.from(patient);
    }

    @PatchMapping
    public PortalDtos.MeResponse patchProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PortalDtos.UpdatePortalProfileRequest body) {
        Long patientId = requirePatientId(principal);
        var updated = updatePortalProfileUseCase.execute(patientId, body.phone(), body.languagePreference());
        return PortalDtos.MeResponse.from(updated);
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PortalDtos.ChangePortalPasswordRequest body) {
        Long patientId = requirePatientId(principal);
        changePatientPortalPasswordUseCase.execute(patientId, body.currentPassword(), body.newPassword());
    }

    @GetMapping("/notification-preferences")
    public NotificationPreferenceResponse notificationPreferences(@AuthenticationPrincipal UserPrincipal principal) {
        Long patientId = requirePatientId(principal);
        var pref = notificationPreferenceRepository.findByPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException(
                        "Notification preferences not found for patient: " + patientId));
        return NotificationPreferenceResponse.from(pref);
    }

    @PatchMapping("/notification-preferences")
    public NotificationPreferenceResponse patchNotificationPreferences(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NotificationPreferenceRequest request) {
        Long patientId = requirePatientId(principal);
        boolean emailEnabled = request.getEmailEnabled() != null ? request.getEmailEnabled() : true;
        boolean smsEnabled = request.getSmsEnabled() != null && request.getSmsEnabled();
        var updated = updateNotificationPreferencesUseCase.execute(patientId, emailEnabled, smsEnabled, request.getPhone());
        return NotificationPreferenceResponse.from(updated);
    }

    @GetMapping("/appointments")
    public Page<PortalDtos.PortalAppointmentRow> appointments(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        Long patientId = requirePatientId(principal);
        return medicalHistoryQueryService.appointmentSummary(patientId, pageable)
                .map(PortalDtos.PortalAppointmentRow::from);
    }

    @GetMapping("/medical/health-summary")
    public PortalDtos.PortalHealthSummary healthSummary(@AuthenticationPrincipal UserPrincipal principal) {
        Long patientId = requirePatientId(principal);
        return PortalDtos.PortalHealthSummary.from(medicalHistoryQueryService.healthSummary(patientId));
    }

    @GetMapping("/medical/conditions")
    public Page<PortalDtos.PortalConditionResponse> conditions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 30) Pageable pageable) {
        Long patientId = requirePatientId(principal);
        return medicalHistoryQueryService.conditions(patientId, active, null, pageable)
                .map(PortalDtos.PortalConditionResponse::from);
    }

    @GetMapping("/medical/medications")
    public Page<PortalDtos.PortalMedicationResponse> medications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 30) Pageable pageable) {
        Long patientId = requirePatientId(principal);
        return medicalHistoryQueryService.medications(patientId, active, null, pageable)
                .map(PortalDtos.PortalMedicationResponse::from);
    }

    @GetMapping("/medical/allergies")
    public Page<PortalDtos.PortalAllergyResponse> allergies(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 30) Pageable pageable) {
        Long patientId = requirePatientId(principal);
        return medicalHistoryQueryService.allergies(patientId, null, pageable)
                .map(PortalDtos.PortalAllergyResponse::from);
    }

    @GetMapping("/documents")
    public Page<MedicalHistoryApiDtos.DocumentResponse> documents(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) MedicalDocumentType type,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Long patientId = requirePatientId(principal);
        return medicalDocumentService.list(patientId, type, dateFrom, dateTo, search, pageable);
    }

    @GetMapping("/documents/{documentId}")
    public MedicalHistoryApiDtos.DocumentResponse documentMetadata(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long documentId) {
        Long patientId = requirePatientId(principal);
        return medicalDocumentService.getMetadataForPatient(documentId, patientId);
    }

    @GetMapping("/documents/{documentId}/file")
    public ResponseEntity<byte[]> downloadDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long documentId,
            HttpServletRequest request) {
        Long patientId = requirePatientId(principal);
        PatientMedicalDocumentService.DocumentDownload dl =
                medicalDocumentService.readForDownloadForPatient(documentId, patientId, request.getRemoteAddr());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-" + documentId + "\"")
                .contentType(MediaType.parseMediaType(dl.mimeType()))
                .body(dl.bytes());
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalHistoryApiDtos.DocumentResponse> uploadDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam MedicalDocumentType documentType,
            @RequestParam(required = false) String notes) {
        Long patientId = requirePatientId(principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalDocumentService.upload(patientId, file, documentType, notes));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDtos.ReviewResponse> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReviewDtos.CreateReviewRequest body) {
        Long patientId = requirePatientId(principal);
        ReviewDtos.ReviewResponse created = createPatientReviewUseCase.execute(patientId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/wait-list")
    public Page<WaitListDtos.WaitListEntryResponse> myWaitList(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 30) Pageable pageable) {
        return listMyWaitListEntriesUseCase.execute(requirePatientId(principal), pageable);
    }

    @PostMapping("/wait-list")
    public ResponseEntity<WaitListDtos.WaitListEntryResponse> joinWaitList(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WaitListDtos.JoinWaitListRequest body) {
        WaitListDtos.WaitListEntryResponse created = joinWaitListUseCase.execute(requirePatientId(principal), body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/wait-list/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelWaitListEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long entryId) {
        cancelMyWaitListEntryUseCase.execute(requirePatientId(principal), entryId);
    }

    @GetMapping("/reviews")
    public Page<ReviewDtos.ReviewResponse> myReviews(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        Long patientId = requirePatientId(principal);
        return listPatientOwnReviewsUseCase.execute(patientId, pageable);
    }

    @GetMapping("/prescriptions")
    public Page<ElectronicPrescriptionDtos.ElectronicPrescriptionResponse> myPrescriptions(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return listMyElectronicPrescriptionsUseCase.execute(requirePatientId(principal), pageable);
    }

    @GetMapping("/prescriptions/{prescriptionId}")
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse myPrescription(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long prescriptionId) {
        return getMyElectronicPrescriptionUseCase.execute(requirePatientId(principal), prescriptionId);
    }
}
