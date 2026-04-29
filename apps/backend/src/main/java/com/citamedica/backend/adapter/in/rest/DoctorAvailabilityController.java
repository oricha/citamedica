package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.availability.AvailabilityConfigurationPatchRequest;
import com.citamedica.backend.adapter.in.dto.availability.AvailabilityConfigurationRequest;
import com.citamedica.backend.adapter.in.dto.availability.AvailabilityConfigurationResponse;
import com.citamedica.backend.application.usecase.ConfigureDoctorAvailabilityUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/availability-configuration")
public class DoctorAvailabilityController {

    private final ConfigureDoctorAvailabilityUseCase configureDoctorAvailabilityUseCase;

    public DoctorAvailabilityController(ConfigureDoctorAvailabilityUseCase configureDoctorAvailabilityUseCase) {
        this.configureDoctorAvailabilityUseCase = configureDoctorAvailabilityUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityConfigurationResponse>> list(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                configureDoctorAvailabilityUseCase.list(doctorId).stream()
                        .map(AvailabilityConfigurationResponse::from)
                        .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AvailabilityConfigurationResponse> upsert(
            @PathVariable Long doctorId,
            @Valid @RequestBody AvailabilityConfigurationRequest request) {
        var saved = configureDoctorAvailabilityUseCase.upsert(
                doctorId,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime(),
                request.getSlotDurationMinutes(),
                request.getMaxConcurrentAppointments() != null ? request.getMaxConcurrentAppointments() : 1);
        return ResponseEntity.ok(AvailabilityConfigurationResponse.from(saved));
    }

    @PatchMapping("/{configId}")
    public ResponseEntity<AvailabilityConfigurationResponse> patch(
            @PathVariable Long doctorId,
            @PathVariable Long configId,
            @RequestBody AvailabilityConfigurationPatchRequest request) {
        var saved = configureDoctorAvailabilityUseCase.update(
                doctorId,
                configId,
                request.getStartTime(),
                request.getEndTime(),
                request.getSlotDurationMinutes(),
                request.getMaxConcurrentAppointments());
        return ResponseEntity.ok(AvailabilityConfigurationResponse.from(saved));
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> delete(@PathVariable Long doctorId, @PathVariable Long configId) {
        configureDoctorAvailabilityUseCase.delete(doctorId, configId);
        return ResponseEntity.noContent().build();
    }
}
