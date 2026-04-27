package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.AppointmentResponse;
import com.citamedica.backend.adapter.in.dto.CreateAppointmentRequest;
import com.citamedica.backend.adapter.in.dto.UpdateAppointmentRequest;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.application.AppointmentService;
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

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<Appointment> appointments = appointmentService.findByDoctorIdAndDate(doctorId, date);
        
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
        LocalDateTime endAt = LocalDateTime.parse(request.getEndAt());

        Appointment appointment = appointmentService.createAppointment(
                request.getDoctorId(),
                request.getPatientId(),
                request.getType(),
                startAt,
                endAt,
                request.getCalBookingId(),
                request.getNotes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.from(appointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentRequest request) {
        LocalDateTime startAt = request.getStartAt() != null ? LocalDateTime.parse(request.getStartAt()) : null;
        LocalDateTime endAt = request.getEndAt() != null ? LocalDateTime.parse(request.getEndAt()) : null;
        Appointment updated = appointmentService.updateAppointment(
                id,
                request.getType(),
                startAt,
                endAt,
                request.getNotes(),
                request.getStatus()
        );
        return ResponseEntity.ok(AppointmentResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}