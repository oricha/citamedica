package com.citamedica.backend.application.service;

import com.citamedica.backend.domain.model.analytics.ReportExportFormat;
import com.citamedica.backend.domain.model.analytics.ReportType;
import com.citamedica.backend.domain.repository.AnalyticsRepository;
import com.citamedica.backend.domain.service.analytics.OccupancyAnalyticsService;
import com.citamedica.backend.domain.service.analytics.PatientAnalyticsService;
import com.citamedica.backend.domain.service.analytics.RevenueAnalyticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportGenerationService {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final OccupancyAnalyticsService occupancyAnalyticsService;
    private final RevenueAnalyticsService revenueAnalyticsService;
    private final PatientAnalyticsService patientAnalyticsService;
    private final AnalyticsRepository analyticsRepository;
    private final ReportExportService reportExportService;

    public ReportGenerationService(
            OccupancyAnalyticsService occupancyAnalyticsService,
            RevenueAnalyticsService revenueAnalyticsService,
            PatientAnalyticsService patientAnalyticsService,
            AnalyticsRepository analyticsRepository,
            ReportExportService reportExportService) {
        this.occupancyAnalyticsService = occupancyAnalyticsService;
        this.revenueAnalyticsService = revenueAnalyticsService;
        this.patientAnalyticsService = patientAnalyticsService;
        this.analyticsRepository = analyticsRepository;
        this.reportExportService = reportExportService;
    }

    public byte[] generate(Long clinicId, ReportType reportType, ReportExportFormat format, LocalDate from, LocalDate to) {
        ReportType effective = reportType == ReportType.CUSTOM ? ReportType.OPERATIONAL : reportType;
        String title = "CitaMedica " + effective + " report: " + clinicId + " (" + from.format(ISO) + " – " + to.format(ISO) + ")";
        return switch (effective) {
            case OPERATIONAL -> reportExportService.export(
                    title,
                    new String[] {"doctorId", "date", "totalSlots", "bookedSlots", "occupancyRate"},
                    operationalRows(clinicId, from, to),
                    format
            );
            case FINANCIAL -> reportExportService.export(
                    title,
                    new String[] {"doctorId", "specialty", "service", "date", "revenue"},
                    financialRows(clinicId, from, to),
                    format
            );
            case PATIENT -> reportExportService.export(
                    title,
                    new String[] {"clinicId", "totalPatients", "activePatients", "churnRate", "outstanding", "overdue"},
                    patientRows(clinicId),
                    format
            );
            case CUSTOM -> reportExportService.export(
                    title,
                    new String[] {"doctorId", "date", "totalSlots", "bookedSlots", "occupancyRate"},
                    operationalRows(clinicId, from, to),
                    format
            );
        };
    }

    private List<String[]> operationalRows(Long clinicId, LocalDate from, LocalDate to) {
        List<String[]> rows = new ArrayList<>();
        for (var row : occupancyAnalyticsService.occupancyForClinic(clinicId, from, to)) {
            rows.add(new String[] {
                    String.valueOf(row.doctorId()),
                    row.slotDate().format(ISO),
                    String.valueOf(row.totalSlots()),
                    String.valueOf(row.bookedSlots()),
                    row.occupancyRate().toPlainString()
            });
        }
        return rows;
    }

    private List<String[]> financialRows(Long clinicId, LocalDate from, LocalDate to) {
        List<String[]> rows = new ArrayList<>();
        for (var row : revenueAnalyticsService.revenueForClinic(clinicId, from, to)) {
            rows.add(new String[] {
                    row.doctorId() != null ? row.doctorId().toString() : "",
                    row.specialtyName() != null ? row.specialtyName() : "",
                    row.serviceName() != null ? row.serviceName() : "",
                    row.revenueDate().format(ISO),
                    row.revenue().toPlainString()
            });
        }
        var col = analyticsRepository.getCollectionsSummary(clinicId);
        rows.add(new String[] {
                "",
                "",
                "",
                "collections-outstanding",
                col.outstandingBalance().toPlainString()
        });
        rows.add(new String[] {
                "",
                "",
                "",
                "collections-overdue",
                col.overdueBalance().toPlainString()
        });
        return rows;
    }

    private List<String[]> patientRows(Long clinicId) {
        List<String[]> rows = new ArrayList<>();
        var retention = patientAnalyticsService.retention(clinicId);
        var col = analyticsRepository.getCollectionsSummary(clinicId);
        if (retention.isPresent()) {
            var r = retention.get();
            rows.add(new String[] {
                    String.valueOf(r.clinicId()),
                    String.valueOf(r.totalPatients()),
                    String.valueOf(r.activePatients()),
                    r.churnRate().toPlainString(),
                    col.outstandingBalance().toPlainString(),
                    col.overdueBalance().toPlainString()
            });
        } else {
            rows.add(new String[] {
                    String.valueOf(clinicId),
                    "0",
                    "0",
                    BigDecimal.ZERO.toPlainString(),
                    col.outstandingBalance().toPlainString(),
                    col.overdueBalance().toPlainString()
            });
        }
        return rows;
    }
}
