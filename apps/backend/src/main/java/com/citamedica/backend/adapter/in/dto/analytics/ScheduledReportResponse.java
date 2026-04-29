package com.citamedica.backend.adapter.in.dto.analytics;

import com.citamedica.backend.domain.model.analytics.ReportFrequency;
import com.citamedica.backend.domain.model.analytics.ReportType;

import java.time.LocalDateTime;

public record ScheduledReportResponse(
        Long id,
        ReportType reportType,
        ReportFrequency frequency,
        String recipients,
        LocalDateTime nextRunAt,
        LocalDateTime lastRunAt,
        boolean active
) {}
