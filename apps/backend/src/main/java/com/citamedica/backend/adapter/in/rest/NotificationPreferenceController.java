package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.notification.NotificationPreferenceRequest;
import com.citamedica.backend.adapter.in.dto.notification.NotificationPreferenceResponse;
import com.citamedica.backend.application.usecase.UpdateNotificationPreferencesUseCase;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients/{id}/notification-preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UpdateNotificationPreferencesUseCase updateUseCase;

    public NotificationPreferenceController(NotificationPreferenceRepository preferenceRepository,
                                            UpdateNotificationPreferencesUseCase updateUseCase) {
        this.preferenceRepository = preferenceRepository;
        this.updateUseCase = updateUseCase;
    }

    @GetMapping
    public ResponseEntity<NotificationPreferenceResponse> get(@PathVariable Long id) {
        var pref = preferenceRepository.findByPatientId(id)
                .orElseThrow(() -> new EntityNotFoundDomainException("Notification preferences not found for patient: " + id));
        return ResponseEntity.ok(NotificationPreferenceResponse.from(pref));
    }

    @PatchMapping
    public ResponseEntity<NotificationPreferenceResponse> patch(@PathVariable Long id,
                                                                @RequestBody NotificationPreferenceRequest request) {
        boolean emailEnabled = request.getEmailEnabled() != null ? request.getEmailEnabled() : true;
        boolean smsEnabled = request.getSmsEnabled() != null && request.getSmsEnabled();
        var updated = updateUseCase.execute(id, emailEnabled, smsEnabled, request.getPhone());
        return ResponseEntity.ok(NotificationPreferenceResponse.from(updated));
    }
}
