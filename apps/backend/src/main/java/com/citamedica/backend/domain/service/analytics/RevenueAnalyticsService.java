package com.citamedica.backend.domain.service.analytics;

import com.citamedica.backend.domain.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueAnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public RevenueAnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<AnalyticsRepository.RevenueRow> revenueForClinic(Long clinicId, LocalDate from, LocalDate to) {
        return analyticsRepository.findRevenueForClinic(clinicId, from, to);
    }

    public BigDecimal revenueOnDate(Long clinicId, LocalDate date) {
        return analyticsRepository.sumRevenueOnDate(clinicId, date);
    }
}
