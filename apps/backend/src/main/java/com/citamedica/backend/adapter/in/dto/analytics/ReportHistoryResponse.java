package com.citamedica.backend.adapter.in.dto.analytics;

import com.citamedica.backend.domain.model.analytics.ReportExportFormat;
import com.citamedica.backend.domain.model.analytics.ReportHistoryStatus;
import com.citamedica.backend.domain.model.analytics.ReportType;

import java.time.LocalDateTime;

public record ReportHistoryResponse(
        Long id,
        ReportType reportType,
        ReportExportFormat exportFormat,
        ReportHistoryStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
