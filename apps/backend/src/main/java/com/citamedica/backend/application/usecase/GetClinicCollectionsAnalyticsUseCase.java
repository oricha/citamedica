package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetClinicCollectionsAnalyticsUseCase {

    private final ClinicRepository clinicRepository;
    private final AnalyticsRepository analyticsRepository;

    public GetClinicCollectionsAnalyticsUseCase(ClinicRepository clinicRepository, AnalyticsRepository analyticsRepository) {
        this.clinicRepository = clinicRepository;
        this.analyticsRepository = analyticsRepository;
    }

    @Transactional(readOnly = true)
    public AnalyticsRepository.CollectionsRow execute(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        return analyticsRepository.getCollectionsSummary(clinicId);
    }
}
