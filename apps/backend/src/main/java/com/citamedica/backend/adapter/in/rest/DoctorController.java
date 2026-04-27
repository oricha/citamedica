package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.DoctorResponse;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.application.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors(@RequestParam(required = false) Long clinic) {
        List<Doctor> doctors;
        if (clinic != null) {
            doctors = doctorService.findByClinicId(clinic);
        } else {
            doctors = doctorService.findActiveDoctors();
        }
        
        List<DoctorResponse> response = doctors.stream()
                .map(DoctorResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Doctor not found"));
        return ResponseEntity.ok(DoctorResponse.from(doctor));
    }
}