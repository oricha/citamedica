package com.citamedica.backend.api.v1;

import com.citamedica.backend.api.v1.dto.AppointmentResponse;
import com.citamedica.backend.api.v1.dto.CreateAppointmentRequest;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.service.AppointmentService;
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
                endAt
        );
        
        // Set optional fields
        if (request.getCalBookingId() != null) {
            appointment.setCalBookingId(request.getCalBookingId());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.from(appointment));
    }
}