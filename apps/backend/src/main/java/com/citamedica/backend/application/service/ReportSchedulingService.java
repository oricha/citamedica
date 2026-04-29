package com.citamedica.backend.application.service;

import com.citamedica.backend.domain.model.analytics.ReportFrequency;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportSchedulingService {

    public LocalDateTime computeNextRun(ReportFrequency frequency, LocalDateTime from) {
        return switch (frequency) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case MONTHLY -> from.plusMonths(1);
        };
    }
}
