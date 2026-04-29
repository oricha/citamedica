package com.citamedica.backend.adapter.in.dto.analytics;

import java.math.BigDecimal;

public record CollectionsAnalyticsResponse(
        BigDecimal outstandingBalance,
        BigDecimal overdueBalance,
        long patientCountWithBalance
) {}
