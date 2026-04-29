package com.citamedica.backend.adapter.in.dto.analytics;

import com.citamedica.backend.domain.model.analytics.ReportFrequency;
import com.citamedica.backend.domain.model.analytics.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateScheduledReportRequest(
        @NotNull ReportType reportType,
        @NotNull ReportFrequency frequency,
        @NotEmpty List<@NotBlank String> recipients,
        LocalDateTime firstRunAt
) {}
