package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.portal.PortalDtos;
import com.citamedica.backend.application.usecase.ActivatePatientPortalUseCase;
import com.citamedica.backend.application.usecase.DeactivatePatientPortalUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientPortalAccessController {

    private final ActivatePatientPortalUseCase activatePatientPortalUseCase;
    private final DeactivatePatientPortalUseCase deactivatePatientPortalUseCase;

    public PatientPortalAccessController(
            ActivatePatientPortalUseCase activatePatientPortalUseCase,
            DeactivatePatientPortalUseCase deactivatePatientPortalUseCase) {
        this.activatePatientPortalUseCase = activatePatientPortalUseCase;
        this.deactivatePatientPortalUseCase = deactivatePatientPortalUseCase;
    }

    @PostMapping("/{patientId}/portal-access")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(
            @PathVariable Long patientId,
            @Valid @RequestBody PortalDtos.ActivatePortalRequest body) {
        activatePatientPortalUseCase.execute(patientId, body.password());
    }

    @DeleteMapping("/{patientId}/portal-access")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long patientId) {
        deactivatePatientPortalUseCase.execute(patientId);
    }
}
