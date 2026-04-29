package com.citamedica.backend.domain.model.analytics;

import com.citamedica.backend.domain.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.util.Optional;

public record ClinicDashboardData(
        BigDecimal revenueToday,
        long appointmentsToday,
        BigDecimal outstandingBalance,
        BigDecimal avgOccupancyLast7Days,
        Optional<AnalyticsRepository.PatientRetentionRow> patientRetention
) {}
