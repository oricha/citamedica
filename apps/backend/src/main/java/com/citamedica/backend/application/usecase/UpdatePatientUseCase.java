package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.service.PatientDomainService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;
    private final PatientDomainService patientDomainService;

    public UpdatePatientUseCase(PatientRepository patientRepository, PatientDomainService patientDomainService) {
        this.patientRepository = patientRepository;
        this.patientDomainService = patientDomainService;
    }

    @Transactional
    public Patient execute(Long id, String fullName, String email, String phone, String birthDate, String insurancePlan) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + id));

        patientDomainService.validateEmailUniqueness(patientRepository, email, id);

        patient.setFullName(fullName);
        patient.setEmail(email);
        patient.setPhone(phone);
        if (birthDate != null && !birthDate.isBlank()) {
            patient.setBirthDate(LocalDate.parse(birthDate));
        }
        patient.setInsurancePlan(insurancePlan);

        return patientRepository.save(patient);
    }
}
