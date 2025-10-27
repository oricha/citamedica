package com.citamedica.backend.api.v1;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getDoctors(@RequestParam(required = false) Long clinicId) {
        List<Doctor> doctors;
        if (clinicId != null) {
            doctors = doctorService.findByClinicId(clinicId);
        } else {
            doctors = doctorService.findActiveDoctors();
        }
        return ResponseEntity.ok(doctors);
    }
}