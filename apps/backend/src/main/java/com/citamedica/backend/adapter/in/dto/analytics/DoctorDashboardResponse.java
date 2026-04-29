package com.citamedica.backend.adapter.in.dto.analytics;

import java.math.BigDecimal;

public record DoctorDashboardResponse(
        long appointmentsToday,
        BigDecimal avgOccupancyLast7Days
) {}
