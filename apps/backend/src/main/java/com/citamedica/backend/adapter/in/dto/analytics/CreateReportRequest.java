package com.citamedica.backend.adapter.in.dto.analytics;

import com.citamedica.backend.domain.model.analytics.ReportExportFormat;
import com.citamedica.backend.domain.model.analytics.ReportType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReportRequest(
        @NotNull ReportType reportType,
        @NotNull ReportExportFormat exportFormat,
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {}
