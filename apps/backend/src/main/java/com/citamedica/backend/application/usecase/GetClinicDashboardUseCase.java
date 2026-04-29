package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.analytics.ClinicDashboardData;
import com.citamedica.backend.domain.model.analytics.ClinicDashboardData;
import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.service.analytics.OccupancyAnalyticsService;
import com.citamedica.backend.domain.service.analytics.PatientAnalyticsService;
import com.citamedica.backend.domain.service.analytics.RevenueAnalyticsService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class GetClinicDashboardUseCase {

    private final ClinicRepository clinicRepository;
    private final RevenueAnalyticsService revenueAnalyticsService;
    private final AnalyticsRepository analyticsRepository;
    private final OccupancyAnalyticsService occupancyAnalyticsService;
    private final PatientAnalyticsService patientAnalyticsService;

    public GetClinicDashboardUseCase(
            ClinicRepository clinicRepository,
            RevenueAnalyticsService revenueAnalyticsService,
            AnalyticsRepository analyticsRepository,
            OccupancyAnalyticsService occupancyAnalyticsService,
            PatientAnalyticsService patientAnalyticsService) {
        this.clinicRepository = clinicRepository;
        this.revenueAnalyticsService = revenueAnalyticsService;
        this.analyticsRepository = analyticsRepository;
        this.occupancyAnalyticsService = occupancyAnalyticsService;
        this.patientAnalyticsService = patientAnalyticsService;
    }

    @Cacheable(cacheNames = "analytics-dashboard", key = "#clinicId + '-' + #asOfDate")
    @Transactional(readOnly = true)
    public ClinicDashboardData execute(Long clinicId, LocalDate asOfDate) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        BigDecimal revenueToday = revenueAnalyticsService.revenueOnDate(clinicId, asOfDate);
        long appointmentsToday = analyticsRepository.countAppointmentsOnDateForClinic(clinicId, asOfDate);
        var collections = analyticsRepository.getCollectionsSummary(clinicId);
        LocalDate from = asOfDate.minusDays(7);
        BigDecimal avgOcc = occupancyAnalyticsService.averageOccupancyClinic(clinicId, from, asOfDate);
        var retention = patientAnalyticsService.retention(clinicId);
        return new ClinicDashboardData(
                revenueToday,
                appointmentsToday,
                collections.outstandingBalance(),
                avgOcc,
                retention
        );
    }
}
