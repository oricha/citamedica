package com.citamedica.backend.api.v1;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientRequest request) {
        Patient patient = patientService.createPatient(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getBirthDate(),
                request.getInsurancePlan()
        );
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.findAll();
        return ResponseEntity.ok(patients);
    }

    public static class PatientRequest {
        private String fullName;
        private String email;
        private String phone;
        private String birthDate;
        private String insurancePlan;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
        public String getInsurancePlan() { return insurancePlan; }
        public void setInsurancePlan(String insurancePlan) { this.insurancePlan = insurancePlan; }
    }
}