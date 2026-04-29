package com.citamedica.backend.domain.model.analytics;

import java.math.BigDecimal;

public record DoctorDashboardData(long appointmentsToday, BigDecimal avgOccupancyLast7Days) {}
