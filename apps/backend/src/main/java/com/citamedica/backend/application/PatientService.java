package com.citamedica.backend.application;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(String fullName, String email, String phone, String birthDate, String insurancePlan) {
        // Validate email uniqueness
        if (patientRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        Patient patient = new Patient(fullName, email, phone);
        // Set birthDate and insurancePlan if provided
        return patientRepository.save(patient);
    }

    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient updatePatient(Long id, String fullName, String email, String phone, String birthDate, String insurancePlan) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.setFullName(fullName);
        patient.setEmail(email);
        patient.setPhone(phone);
        // Update other fields
        return patientRepository.save(patient);
    }
}