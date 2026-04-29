package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.service.analytics.RevenueAnalyticsService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetClinicRevenueAnalyticsUseCase {

    private final ClinicRepository clinicRepository;
    private final RevenueAnalyticsService revenueAnalyticsService;

    public GetClinicRevenueAnalyticsUseCase(
            ClinicRepository clinicRepository,
            RevenueAnalyticsService revenueAnalyticsService) {
        this.clinicRepository = clinicRepository;
        this.revenueAnalyticsService = revenueAnalyticsService;
    }

    @Transactional(readOnly = true)
    public List<AnalyticsRepository.RevenueRow> execute(Long clinicId, LocalDate from, LocalDate to) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        return revenueAnalyticsService.revenueForClinic(clinicId, from, to);
    }
}
