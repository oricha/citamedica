package com.citamedica.backend.api.v1;

import com.citamedica.backend.api.v1.dto.CreatePatientRequest;
import com.citamedica.backend.api.v1.dto.PatientResponse;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        Patient patient = patientService.createPatient(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getBirthDate(),
                request.getInsurancePlan()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(PatientResponse.from(patient));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<Patient> patients = patientService.findAll();
        List<PatientResponse> response = patients.stream()
                .map(PatientResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}