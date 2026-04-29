package com.citamedica.backend.domain.model.analytics;

import java.math.BigDecimal;

/**
 * Aggregated headline metrics for dashboards or export summary rows.
 */
public record ReportMetrics(
        BigDecimal revenueToday,
        long appointmentsToday,
        BigDecimal outstandingBalance,
        BigDecimal avgOccupancyLast7Days
) {
    public static ReportMetrics empty() {
        return new ReportMetrics(BigDecimal.ZERO, 0L, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
