package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.CreatePatientRequest;
import com.citamedica.backend.adapter.in.dto.PatientResponse;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.application.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable Long id) {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        return ResponseEntity.ok(PatientResponse.from(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody CreatePatientRequest request) {
        Patient patient = patientService.updatePatient(
                id,
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getBirthDate(),
                request.getInsurancePlan()
        );
        return ResponseEntity.ok(PatientResponse.from(patient));
    }
}