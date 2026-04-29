package com.citamedica.backend.domain.service.analytics;

import com.citamedica.backend.domain.repository.AnalyticsRepository;

import java.util.Optional;

public class PatientAnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public PatientAnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public Optional<AnalyticsRepository.PatientRetentionRow> retention(Long clinicId) {
        return analyticsRepository.findPatientRetention(clinicId);
    }
}
