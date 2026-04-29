package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.AppointmentResponse;
import com.citamedica.backend.adapter.in.dto.CreateAppointmentRequest;
import com.citamedica.backend.adapter.in.dto.UpdateAppointmentRequest;
import com.citamedica.backend.application.usecase.CreateAppointmentUseCase;
import com.citamedica.backend.application.usecase.DeleteAppointmentUseCase;
import com.citamedica.backend.application.usecase.GetAppointmentsByDoctorAndDateUseCase;
import com.citamedica.backend.application.usecase.UpdateAppointmentUseCase;
import com.citamedica.backend.domain.model.Appointment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final GetAppointmentsByDoctorAndDateUseCase getAppointmentsByDoctorAndDateUseCase;
    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final DeleteAppointmentUseCase deleteAppointmentUseCase;

    public AppointmentController(
            GetAppointmentsByDoctorAndDateUseCase getAppointmentsByDoctorAndDateUseCase,
            CreateAppointmentUseCase createAppointmentUseCase,
            UpdateAppointmentUseCase updateAppointmentUseCase,
            DeleteAppointmentUseCase deleteAppointmentUseCase) {
        this.getAppointmentsByDoctorAndDateUseCase = getAppointmentsByDoctorAndDateUseCase;
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.updateAppointmentUseCase = updateAppointmentUseCase;
        this.deleteAppointmentUseCase = deleteAppointmentUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<Appointment> appointments = getAppointmentsByDoctorAndDateUseCase.execute(doctorId, date);
        
        // Sort by start time
        List<AppointmentResponse> response = appointments.stream()
                .sorted(Comparator.comparing(Appointment::getStartAt))
                .map(AppointmentResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {

        LocalDateTime startAt = LocalDateTime.parse(request.getStartAt());
        LocalDateTime endAt = null;
        if (request.getServiceId() == null) {
            if (request.getEndAt() == null || request.getEndAt().isBlank()) {
                throw new ResponseStatusException(BAD_REQUEST, "endAt is required when serviceId is omitted");
            }
            endAt = LocalDateTime.parse(request.getEndAt());
        }

        Appointment appointment = createAppointmentUseCase.execute(
                request.getDoctorId(),
                request.getPatientId(),
                request.getType(),
                startAt,
                endAt,
                request.getCalBookingId(),
                request.getNotes(),
                request.getTimeSlotId(),
                request.getServiceId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.from(appointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentRequest request) {
        LocalDateTime startAt = request.getStartAt() != null ? LocalDateTime.parse(request.getStartAt()) : null;
        LocalDateTime endAt = request.getEndAt() != null ? LocalDateTime.parse(request.getEndAt()) : null;
        Appointment updated = updateAppointmentUseCase.execute(
                id,
                request.getType(),
                startAt,
                endAt,
                request.getNotes(),
                request.getStatus(),
                request.getTimeSlotId()
        );
        return ResponseEntity.ok(AppointmentResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        deleteAppointmentUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
