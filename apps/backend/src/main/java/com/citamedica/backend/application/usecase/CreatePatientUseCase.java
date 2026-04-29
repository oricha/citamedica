package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.service.PatientDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreatePatientUseCase {

    private final PatientRepository patientRepository;
    private final PatientDomainService patientDomainService;

    public CreatePatientUseCase(PatientRepository patientRepository, PatientDomainService patientDomainService) {
        this.patientRepository = patientRepository;
        this.patientDomainService = patientDomainService;
    }

    @Transactional
    public Patient execute(String fullName, String email, String phone, String birthDate, String insurancePlan) {
        patientDomainService.validateEmailUniqueness(patientRepository, email, null);

        Patient patient = new Patient(fullName, email, phone);
        if (birthDate != null && !birthDate.isBlank()) {
            patient.setBirthDate(LocalDate.parse(birthDate));
        }
        patient.setInsurancePlan(insurancePlan);
        return patientRepository.save(patient);
    }
}
