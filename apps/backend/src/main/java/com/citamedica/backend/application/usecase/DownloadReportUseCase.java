package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.analytics.ReportExportFormat;
import com.citamedica.backend.domain.model.analytics.ReportHistory;
import com.citamedica.backend.domain.model.analytics.ReportHistoryStatus;
import com.citamedica.backend.adapter.out.persistence.jpa.ReportHistoryJpaRepository;
import com.citamedica.backend.exception.domain.AnalyticsException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DownloadReportUseCase {

    private final ReportHistoryJpaRepository reportHistoryJpaRepository;

    public DownloadReportUseCase(ReportHistoryJpaRepository reportHistoryJpaRepository) {
        this.reportHistoryJpaRepository = reportHistoryJpaRepository;
    }

    public record DownloadedReport(byte[] content, ReportExportFormat format) {}

    @Transactional(readOnly = true)
    public DownloadedReport execute(Long clinicId, Long reportId) {
        ReportHistory history = reportHistoryJpaRepository.findByIdAndClinic_Id(reportId, clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Report not found: " + reportId));
        if (history.getStatus() != ReportHistoryStatus.COMPLETED || history.getContent() == null) {
            throw new AnalyticsException("Report is not ready for download");
        }
        return new DownloadedReport(history.getContent(), history.getExportFormat());
    }
}
