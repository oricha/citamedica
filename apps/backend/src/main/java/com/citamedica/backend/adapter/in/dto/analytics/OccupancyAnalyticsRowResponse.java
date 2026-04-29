package com.citamedica.backend.adapter.in.dto.analytics;

import java.math.BigDecimal;

public record OccupancyAnalyticsRowResponse(
        Long doctorId,
        String date,
        long totalSlots,
        long bookedSlots,
        BigDecimal occupancyRate
) {}
