package com.citamedica.backend.domain.model.analytics;

import com.citamedica.backend.domain.model.Clinic;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_history", indexes = {
        @Index(name = "idx_report_history_clinic_created", columnList = "clinic_id,created_at")
})
public class ReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_report_id")
    private ScheduledReport scheduledReport;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 32)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "export_format", nullable = false, length = 16)
    private ReportExportFormat exportFormat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReportHistoryStatus status = ReportHistoryStatus.PENDING;

    @Column(name = "filter_params", columnDefinition = "TEXT")
    private String filterParams;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "content")
    private byte[] content;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public ScheduledReport getScheduledReport() {
        return scheduledReport;
    }

    public void setScheduledReport(ScheduledReport scheduledReport) {
        this.scheduledReport = scheduledReport;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportExportFormat getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(ReportExportFormat exportFormat) {
        this.exportFormat = exportFormat;
    }

    public ReportHistoryStatus getStatus() {
        return status;
    }

    public void setStatus(ReportHistoryStatus status) {
        this.status = status;
    }

    public String getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(String filterParams) {
        this.filterParams = filterParams;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
