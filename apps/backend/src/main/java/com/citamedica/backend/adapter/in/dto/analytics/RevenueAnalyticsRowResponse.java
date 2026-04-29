package com.citamedica.backend.adapter.in.dto.analytics;

import java.math.BigDecimal;

public record RevenueAnalyticsRowResponse(
        Long doctorId,
        String specialtyName,
        String serviceName,
        String date,
        BigDecimal revenue
) {}
