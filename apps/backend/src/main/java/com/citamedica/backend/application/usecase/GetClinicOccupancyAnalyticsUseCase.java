package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.service.analytics.OccupancyAnalyticsService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetClinicOccupancyAnalyticsUseCase {

    private final ClinicRepository clinicRepository;
    private final OccupancyAnalyticsService occupancyAnalyticsService;

    public GetClinicOccupancyAnalyticsUseCase(
            ClinicRepository clinicRepository,
            OccupancyAnalyticsService occupancyAnalyticsService) {
        this.clinicRepository = clinicRepository;
        this.occupancyAnalyticsService = occupancyAnalyticsService;
    }

    @Transactional(readOnly = true)
    public List<AnalyticsRepository.OccupancyRow> execute(Long clinicId, LocalDate from, LocalDate to) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        return occupancyAnalyticsService.occupancyForClinic(clinicId, from, to);
    }
}
