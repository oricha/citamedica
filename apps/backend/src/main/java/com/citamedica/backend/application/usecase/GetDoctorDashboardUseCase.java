package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.analytics.DoctorDashboardData;
import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.service.analytics.OccupancyAnalyticsService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class GetDoctorDashboardUseCase {

    private final DoctorRepository doctorRepository;
    private final AnalyticsRepository analyticsRepository;
    private final OccupancyAnalyticsService occupancyAnalyticsService;

    public GetDoctorDashboardUseCase(
            DoctorRepository doctorRepository,
            AnalyticsRepository analyticsRepository,
            OccupancyAnalyticsService occupancyAnalyticsService) {
        this.doctorRepository = doctorRepository;
        this.analyticsRepository = analyticsRepository;
        this.occupancyAnalyticsService = occupancyAnalyticsService;
    }

    @Transactional(readOnly = true)
    public DoctorDashboardData execute(Long doctorId, LocalDate asOfDate) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        long appointmentsToday = analyticsRepository.countAppointmentsOnDateForDoctor(doctorId, asOfDate);
        LocalDate from = asOfDate.minusDays(7);
        var avgOcc = occupancyAnalyticsService.averageOccupancyDoctor(doctorId, from, asOfDate);
        return new DoctorDashboardData(appointmentsToday, avgOcc);
    }
}
