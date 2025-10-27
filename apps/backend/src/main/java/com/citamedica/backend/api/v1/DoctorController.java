package com.citamedica.backend.api.v1;

import com.citamedica.backend.api.v1.dto.DoctorResponse;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
}