package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.service.analytics.PatientAnalyticsService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GetClinicPatientRetentionAnalyticsUseCase {

    private final ClinicRepository clinicRepository;
    private final PatientAnalyticsService patientAnalyticsService;

    public GetClinicPatientRetentionAnalyticsUseCase(
            ClinicRepository clinicRepository,
            PatientAnalyticsService patientAnalyticsService) {
        this.clinicRepository = clinicRepository;
        this.patientAnalyticsService = patientAnalyticsService;
    }

    @Transactional(readOnly = true)
    public Optional<AnalyticsRepository.PatientRetentionRow> execute(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        return patientAnalyticsService.retention(clinicId);
    }
}
