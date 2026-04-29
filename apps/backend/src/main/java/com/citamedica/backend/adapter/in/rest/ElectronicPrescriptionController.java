package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.application.usecase.CancelElectronicPrescriptionUseCase;
import com.citamedica.backend.application.usecase.CreateElectronicPrescriptionUseCase;
import com.citamedica.backend.application.usecase.GetElectronicPrescriptionUseCase;
import com.citamedica.backend.application.usecase.ListElectronicPrescriptionsForPatientUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('STAFF','DOCTOR','ADMIN')")
public class ElectronicPrescriptionController {

    private final CreateElectronicPrescriptionUseCase createElectronicPrescriptionUseCase;
    private final ListElectronicPrescriptionsForPatientUseCase listElectronicPrescriptionsForPatientUseCase;
    private final GetElectronicPrescriptionUseCase getElectronicPrescriptionUseCase;
    private final CancelElectronicPrescriptionUseCase cancelElectronicPrescriptionUseCase;

    public ElectronicPrescriptionController(
            CreateElectronicPrescriptionUseCase createElectronicPrescriptionUseCase,
            ListElectronicPrescriptionsForPatientUseCase listElectronicPrescriptionsForPatientUseCase,
            GetElectronicPrescriptionUseCase getElectronicPrescriptionUseCase,
            CancelElectronicPrescriptionUseCase cancelElectronicPrescriptionUseCase) {
        this.createElectronicPrescriptionUseCase = createElectronicPrescriptionUseCase;
        this.listElectronicPrescriptionsForPatientUseCase = listElectronicPrescriptionsForPatientUseCase;
        this.getElectronicPrescriptionUseCase = getElectronicPrescriptionUseCase;
        this.cancelElectronicPrescriptionUseCase = cancelElectronicPrescriptionUseCase;
    }

    @PostMapping("/api/v1/patients/{patientId}/prescriptions")
    public ResponseEntity<ElectronicPrescriptionDtos.ElectronicPrescriptionResponse> create(
            @PathVariable Long patientId,
            @Valid @RequestBody ElectronicPrescriptionDtos.CreateElectronicPrescriptionRequest body) {
        var created = createElectronicPrescriptionUseCase.execute(patientId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/v1/patients/{patientId}/prescriptions")
    public Page<ElectronicPrescriptionDtos.ElectronicPrescriptionResponse> listForPatient(
            @PathVariable Long patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return listElectronicPrescriptionsForPatientUseCase.execute(patientId, pageable);
    }

    @GetMapping("/api/v1/prescriptions/{prescriptionId}")
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse get(
            @PathVariable Long prescriptionId) {
        return getElectronicPrescriptionUseCase.execute(prescriptionId);
    }

    @PatchMapping("/api/v1/prescriptions/{prescriptionId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse cancel(
            @PathVariable Long prescriptionId) {
        return cancelElectronicPrescriptionUseCase.execute(prescriptionId);
    }
}
