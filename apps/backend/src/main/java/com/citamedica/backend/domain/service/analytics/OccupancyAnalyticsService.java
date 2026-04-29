package com.citamedica.backend.domain.service.analytics;

import com.citamedica.backend.domain.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OccupancyAnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public OccupancyAnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<AnalyticsRepository.OccupancyRow> occupancyForClinic(Long clinicId, LocalDate from, LocalDate to) {
        return analyticsRepository.findOccupancyForClinic(clinicId, from, to);
    }

    public List<AnalyticsRepository.OccupancyRow> occupancyForDoctor(Long doctorId, LocalDate from, LocalDate to) {
        return analyticsRepository.findOccupancyForDoctor(doctorId, from, to);
    }

    public BigDecimal averageOccupancyClinic(Long clinicId, LocalDate from, LocalDate to) {
        return analyticsRepository.averageOccupancyRateForClinic(clinicId, from, to);
    }

    public BigDecimal averageOccupancyDoctor(Long doctorId, LocalDate from, LocalDate to) {
        return analyticsRepository.averageOccupancyRateForDoctor(doctorId, from, to);
    }
}
