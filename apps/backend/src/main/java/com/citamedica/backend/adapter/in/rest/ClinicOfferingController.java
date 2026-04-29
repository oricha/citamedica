package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.catalog.ClinicOfferingCreateRequest;
import com.citamedica.backend.adapter.in.dto.catalog.ClinicOfferingPatchRequest;
import com.citamedica.backend.adapter.in.dto.catalog.ClinicOfferingResponse;
import com.citamedica.backend.application.usecase.CreateClinicOfferingUseCase;
import com.citamedica.backend.application.usecase.DeactivateClinicOfferingUseCase;
import com.citamedica.backend.application.usecase.ListClinicOfferingsUseCase;
import com.citamedica.backend.application.usecase.UpdateClinicOfferingUseCase;
import com.citamedica.backend.domain.model.ClinicOffering;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/services")
public class ClinicOfferingController {

    private final CreateClinicOfferingUseCase createClinicOfferingUseCase;
    private final UpdateClinicOfferingUseCase updateClinicOfferingUseCase;
    private final ListClinicOfferingsUseCase listClinicOfferingsUseCase;
    private final DeactivateClinicOfferingUseCase deactivateClinicOfferingUseCase;

    public ClinicOfferingController(
            CreateClinicOfferingUseCase createClinicOfferingUseCase,
            UpdateClinicOfferingUseCase updateClinicOfferingUseCase,
            ListClinicOfferingsUseCase listClinicOfferingsUseCase,
            DeactivateClinicOfferingUseCase deactivateClinicOfferingUseCase) {
        this.createClinicOfferingUseCase = createClinicOfferingUseCase;
        this.updateClinicOfferingUseCase = updateClinicOfferingUseCase;
        this.listClinicOfferingsUseCase = listClinicOfferingsUseCase;
        this.deactivateClinicOfferingUseCase = deactivateClinicOfferingUseCase;
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<ClinicOfferingResponse> create(
            @PathVariable Long clinicId,
            @Valid @RequestBody ClinicOfferingCreateRequest request) {
        ClinicOffering created = createClinicOfferingUseCase.execute(
                clinicId,
                request.getName(),
                request.getDescription(),
                request.getDurationMinutes(),
                request.getBasePrice(),
                request.getMinRequiredSpecialtyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClinicOfferingResponse.from(created));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN','STAFF','DOCTOR')")
    @GetMapping
    public ResponseEntity<List<ClinicOfferingResponse>> list(
            @PathVariable Long clinicId,
            @RequestParam(name = "activeOnly", defaultValue = "true") boolean activeOnly) {
        List<ClinicOfferingResponse> body = listClinicOfferingsUseCase.execute(clinicId, activeOnly).stream()
                .map(ClinicOfferingResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(body);
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PatchMapping("/{serviceId}")
    public ResponseEntity<ClinicOfferingResponse> patch(
            @PathVariable Long clinicId,
            @PathVariable Long serviceId,
            @Valid @RequestBody ClinicOfferingPatchRequest request) {
        ClinicOffering updated = updateClinicOfferingUseCase.execute(
                clinicId,
                serviceId,
                request.getName(),
                request.getDescription(),
                request.getDurationMinutes(),
                request.getBasePrice(),
                request.getClearMinRequiredSpecialty(),
                request.getMinRequiredSpecialtyId(),
                request.getActive());
        return ResponseEntity.ok(ClinicOfferingResponse.from(updated));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ClinicOfferingResponse> deactivate(
            @PathVariable Long clinicId,
            @PathVariable Long serviceId) {
        ClinicOffering o = deactivateClinicOfferingUseCase.execute(clinicId, serviceId);
        return ResponseEntity.ok(ClinicOfferingResponse.from(o));
    }
}
