package com.citamedica.backend.adapter.in.dto.analytics;

import java.math.BigDecimal;

public record PatientRetentionAnalyticsResponse(
        long totalPatients,
        long activePatients,
        BigDecimal churnRate
) {}
