package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.CreatePatientRequest;
import com.citamedica.backend.adapter.in.dto.PatientResponse;
import com.citamedica.backend.application.usecase.CreatePatientUseCase;
import com.citamedica.backend.application.usecase.GetAllPatientsUseCase;
import com.citamedica.backend.application.usecase.GetPatientByIdUseCase;
import com.citamedica.backend.application.usecase.UpdatePatientUseCase;
import com.citamedica.backend.domain.model.Patient;
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

    private final CreatePatientUseCase createPatientUseCase;
    private final GetAllPatientsUseCase getAllPatientsUseCase;
    private final GetPatientByIdUseCase getPatientByIdUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;

    public PatientController(
            CreatePatientUseCase createPatientUseCase,
            GetAllPatientsUseCase getAllPatientsUseCase,
            GetPatientByIdUseCase getPatientByIdUseCase,
            UpdatePatientUseCase updatePatientUseCase) {
        this.createPatientUseCase = createPatientUseCase;
        this.getAllPatientsUseCase = getAllPatientsUseCase;
        this.getPatientByIdUseCase = getPatientByIdUseCase;
        this.updatePatientUseCase = updatePatientUseCase;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        Patient patient = createPatientUseCase.execute(
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
        List<Patient> patients = getAllPatientsUseCase.execute();
        List<PatientResponse> response = patients.stream()
                .map(PatientResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable Long id) {
        Patient patient = getPatientByIdUseCase.execute(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        return ResponseEntity.ok(PatientResponse.from(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody CreatePatientRequest request) {
        Patient patient = updatePatientUseCase.execute(
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
