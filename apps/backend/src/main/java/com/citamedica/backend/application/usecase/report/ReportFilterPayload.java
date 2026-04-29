package com.citamedica.backend.application.usecase.report;

import com.citamedica.backend.domain.model.analytics.ReportType;

import java.time.LocalDate;

public record ReportFilterPayload(LocalDate from, LocalDate to, ReportType reportType) {}