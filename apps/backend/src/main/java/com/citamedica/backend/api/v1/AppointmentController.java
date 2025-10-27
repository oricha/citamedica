package com.citamedica.backend.api.v1;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Appointment> appointments = appointmentService.findByDoctorIdAndDate(doctorId, localDate);
        return ResponseEntity.ok(appointments);
    }
}