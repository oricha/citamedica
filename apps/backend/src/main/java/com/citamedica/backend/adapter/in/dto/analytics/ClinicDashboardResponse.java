package com.citamedica.backend.adapter.in.dto.analytics;

import java.math.BigDecimal;

public record ClinicDashboardResponse(
        BigDecimal revenueToday,
        long appointmentsToday,
        BigDecimal outstandingBalance,
        BigDecimal avgOccupancyLast7Days,
        PatientRetentionAnalyticsResponse patientRetention
) {}
