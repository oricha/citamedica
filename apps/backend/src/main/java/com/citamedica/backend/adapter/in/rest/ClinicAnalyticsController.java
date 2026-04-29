package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.analytics.*;
import com.citamedica.backend.application.usecase.*;
import com.citamedica.backend.domain.model.analytics.ClinicDashboardData;
import com.citamedica.backend.domain.model.analytics.ReportHistory;
import com.citamedica.backend.domain.model.analytics.ScheduledReport;
import com.citamedica.backend.domain.repository.AnalyticsRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clinics/{clinicId}")
public class ClinicAnalyticsController {

    private final GetClinicOccupancyAnalyticsUseCase getClinicOccupancyAnalyticsUseCase;
    private final GetClinicRevenueAnalyticsUseCase getClinicRevenueAnalyticsUseCase;
    private final GetClinicCollectionsAnalyticsUseCase getClinicCollectionsAnalyticsUseCase;
    private final GetClinicPatientRetentionAnalyticsUseCase getClinicPatientRetentionAnalyticsUseCase;
    private final GetClinicDashboardUseCase getClinicDashboardUseCase;
    private final CreateAdHocReportUseCase createAdHocReportUseCase;
    private final ListReportHistoryUseCase listReportHistoryUseCase;
    private final DownloadReportUseCase downloadReportUseCase;
    private final CreateScheduledReportUseCase createScheduledReportUseCase;
    private final ListScheduledReportsUseCase listScheduledReportsUseCase;

    public ClinicAnalyticsController(
            GetClinicOccupancyAnalyticsUseCase getClinicOccupancyAnalyticsUseCase,
            GetClinicRevenueAnalyticsUseCase getClinicRevenueAnalyticsUseCase,
            GetClinicCollectionsAnalyticsUseCase getClinicCollectionsAnalyticsUseCase,
            GetClinicPatientRetentionAnalyticsUseCase getClinicPatientRetentionAnalyticsUseCase,
            GetClinicDashboardUseCase getClinicDashboardUseCase,
            CreateAdHocReportUseCase createAdHocReportUseCase,
            ListReportHistoryUseCase listReportHistoryUseCase,
            DownloadReportUseCase downloadReportUseCase,
            CreateScheduledReportUseCase createScheduledReportUseCase,
            ListScheduledReportsUseCase listScheduledReportsUseCase) {
        this.getClinicOccupancyAnalyticsUseCase = getClinicOccupancyAnalyticsUseCase;
        this.getClinicRevenueAnalyticsUseCase = getClinicRevenueAnalyticsUseCase;
        this.getClinicCollectionsAnalyticsUseCase = getClinicCollectionsAnalyticsUseCase;
        this.getClinicPatientRetentionAnalyticsUseCase = getClinicPatientRetentionAnalyticsUseCase;
        this.getClinicDashboardUseCase = getClinicDashboardUseCase;
        this.createAdHocReportUseCase = createAdHocReportUseCase;
        this.listReportHistoryUseCase = listReportHistoryUseCase;
        this.downloadReportUseCase = downloadReportUseCase;
        this.createScheduledReportUseCase = createScheduledReportUseCase;
        this.listScheduledReportsUseCase = listScheduledReportsUseCase;
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/analytics/occupancy")
    public ResponseEntity<List<OccupancyAnalyticsRowResponse>> occupancy(
            @PathVariable Long clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<OccupancyAnalyticsRowResponse> rows = getClinicOccupancyAnalyticsUseCase.execute(clinicId, from, to).stream()
                .map(r -> new OccupancyAnalyticsRowResponse(
                        r.doctorId(),
                        r.slotDate().toString(),
                        r.totalSlots(),
                        r.bookedSlots(),
                        r.occupancyRate()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(rows);
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/analytics/revenue")
    public ResponseEntity<List<RevenueAnalyticsRowResponse>> revenue(
            @PathVariable Long clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<RevenueAnalyticsRowResponse> rows = getClinicRevenueAnalyticsUseCase.execute(clinicId, from, to).stream()
                .map(r -> new RevenueAnalyticsRowResponse(
                        r.doctorId(),
                        r.specialtyName(),
                        r.serviceName(),
                        r.revenueDate().toString(),
                        r.revenue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(rows);
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/analytics/collections")
    public ResponseEntity<CollectionsAnalyticsResponse> collections(@PathVariable Long clinicId) {
        AnalyticsRepository.CollectionsRow row = getClinicCollectionsAnalyticsUseCase.execute(clinicId);
        return ResponseEntity.ok(new CollectionsAnalyticsResponse(
                row.outstandingBalance(),
                row.overdueBalance(),
                row.patientCount()));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/analytics/patient-retention")
    public ResponseEntity<PatientRetentionAnalyticsResponse> patientRetention(@PathVariable Long clinicId) {
        return getClinicPatientRetentionAnalyticsUseCase.execute(clinicId)
                .map(r -> new PatientRetentionAnalyticsResponse(r.totalPatients(), r.activePatients(), r.churnRate()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(new PatientRetentionAnalyticsResponse(0L, 0L, BigDecimal.ZERO)));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<ClinicDashboardResponse> dashboard(
            @PathVariable Long clinicId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate asOf = date != null ? date : LocalDate.now();
        ClinicDashboardData data = getClinicDashboardUseCase.execute(clinicId, asOf);
        PatientRetentionAnalyticsResponse retention = data.patientRetention()
                .map(r -> new PatientRetentionAnalyticsResponse(r.totalPatients(), r.activePatients(), r.churnRate()))
                .orElseGet(() -> new PatientRetentionAnalyticsResponse(0L, 0L, BigDecimal.ZERO));
        return ResponseEntity.ok(new ClinicDashboardResponse(
                data.revenueToday(),
                data.appointmentsToday(),
                data.outstandingBalance(),
                data.avgOccupancyLast7Days(),
                retention));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PostMapping("/reports")
    public ResponseEntity<ReportHistoryResponse> createReport(
            @PathVariable Long clinicId,
            @Valid @RequestBody CreateReportRequest request) {
        ReportHistory h = createAdHocReportUseCase.execute(clinicId, request);
        return ResponseEntity.ok(toHistoryResponse(h));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<List<ReportHistoryResponse>> listReports(@PathVariable Long clinicId) {
        List<ReportHistoryResponse> list = listReportHistoryUseCase.execute(clinicId).stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/reports/{reportId}/file")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long clinicId, @PathVariable Long reportId) {
        DownloadReportUseCase.DownloadedReport downloaded = downloadReportUseCase.execute(clinicId, reportId);
        MediaType mediaType = switch (downloaded.format()) {
            case PDF -> MediaType.APPLICATION_PDF;
            case CSV -> new MediaType("text", "csv");
            case XLSX -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        };
        String ext = downloaded.format().name().toLowerCase();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-" + reportId + "." + ext + "\"")
                .contentType(mediaType)
                .body(downloaded.content());
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PostMapping("/scheduled-reports")
    public ResponseEntity<ScheduledReportResponse> createScheduled(
            @PathVariable Long clinicId,
            @Valid @RequestBody CreateScheduledReportRequest request) {
        ScheduledReport saved = createScheduledReportUseCase.execute(clinicId, request);
        return ResponseEntity.ok(toScheduledResponse(saved));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/scheduled-reports")
    public ResponseEntity<List<ScheduledReportResponse>> listScheduled(@PathVariable Long clinicId) {
        List<ScheduledReportResponse> list = listScheduledReportsUseCase.execute(clinicId).stream()
                .map(this::toScheduledResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private ReportHistoryResponse toHistoryResponse(ReportHistory h) {
        return new ReportHistoryResponse(
                h.getId(),
                h.getReportType(),
                h.getExportFormat(),
                h.getStatus(),
                h.getCreatedAt(),
                h.getCompletedAt());
    }

    private ScheduledReportResponse toScheduledResponse(ScheduledReport s) {
        return new ScheduledReportResponse(
                s.getId(),
                s.getReportType(),
                s.getFrequency(),
                s.getRecipients(),
                s.getNextRunAt(),
                s.getLastRunAt(),
                s.isActive());
    }
}
