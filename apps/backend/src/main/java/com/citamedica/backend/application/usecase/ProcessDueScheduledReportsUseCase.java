package com.citamedica.backend.application.usecase;

import com.citamedica.backend.application.service.ReportGenerationService;
import com.citamedica.backend.application.service.ReportSchedulingService;
import com.citamedica.backend.adapter.out.integration.notification.EmailNotification;
import com.citamedica.backend.adapter.out.integration.notification.NotificationPort;
import com.citamedica.backend.domain.model.analytics.ReportExportFormat;
import com.citamedica.backend.domain.model.analytics.ReportHistory;
import com.citamedica.backend.domain.model.analytics.ReportHistoryStatus;
import com.citamedica.backend.domain.model.analytics.ScheduledReport;
import com.citamedica.backend.adapter.out.persistence.jpa.ReportHistoryJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.ScheduledReportJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ProcessDueScheduledReportsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessDueScheduledReportsUseCase.class);

    private final ScheduledReportJpaRepository scheduledReportJpaRepository;
    private final ReportHistoryJpaRepository reportHistoryJpaRepository;
    private final ReportGenerationService reportGenerationService;
    private final ReportSchedulingService reportSchedulingService;
    private final NotificationPort notificationPort;

    public ProcessDueScheduledReportsUseCase(
            ScheduledReportJpaRepository scheduledReportJpaRepository,
            ReportHistoryJpaRepository reportHistoryJpaRepository,
            ReportGenerationService reportGenerationService,
            ReportSchedulingService reportSchedulingService,
            NotificationPort notificationPort) {
        this.scheduledReportJpaRepository = scheduledReportJpaRepository;
        this.reportHistoryJpaRepository = reportHistoryJpaRepository;
        this.reportGenerationService = reportGenerationService;
        this.reportSchedulingService = reportSchedulingService;
        this.notificationPort = notificationPort;
    }

    @Transactional
    public void execute() {
        LocalDateTime now = LocalDateTime.now();
        var due = scheduledReportJpaRepository.findByActiveTrueAndNextRunAtLessThanEqual(now);
        for (ScheduledReport sr : due) {
            processOne(sr, now);
        }
    }

    private void processOne(ScheduledReport sr, LocalDateTime now) {
        Long clinicId = sr.getClinic().getId();
        LocalDate to = now.toLocalDate();
        LocalDate from = to.minusDays(30);
        LocalDateTime previousNext = sr.getNextRunAt();
        try {
            byte[] pdf = reportGenerationService.generate(clinicId, sr.getReportType(), ReportExportFormat.PDF, from, to);
            ReportHistory history = new ReportHistory();
            history.setClinic(sr.getClinic());
            history.setScheduledReport(sr);
            history.setReportType(sr.getReportType());
            history.setExportFormat(ReportExportFormat.PDF);
            history.setStatus(ReportHistoryStatus.COMPLETED);
            history.setContent(pdf);
            history.setCompletedAt(now);
            reportHistoryJpaRepository.save(history);

            String fileName = "report-" + sr.getReportType() + "-" + to + ".pdf";
            for (String email : sr.getRecipients().split(",")) {
                String toAddr = email.trim();
                if (!toAddr.isEmpty()) {
                    notificationPort.sendEmail(EmailNotification.builder()
                            .to(toAddr)
                            .from("no-reply@citamedica.com")
                            .subject("Scheduled CitaMedica report: " + sr.getReportType())
                            .body("Please find your scheduled report attached.")
                            .attachmentContent(pdf)
                            .attachmentFileName(fileName)
                            .build());
                }
            }
            sr.setLastRunAt(now);
            sr.setNextRunAt(reportSchedulingService.computeNextRun(sr.getFrequency(), previousNext));
            scheduledReportJpaRepository.save(sr);
            log.info("Scheduled report {} delivered for clinic {}", sr.getId(), clinicId);
        } catch (RuntimeException ex) {
            log.warn("Scheduled report {} failed: {}", sr.getId(), ex.getMessage());
            ReportHistory history = new ReportHistory();
            history.setClinic(sr.getClinic());
            history.setScheduledReport(sr);
            history.setReportType(sr.getReportType());
            history.setExportFormat(ReportExportFormat.PDF);
            history.setStatus(ReportHistoryStatus.FAILED);
            history.setErrorMessage(ex.getMessage());
            history.setCompletedAt(now);
            reportHistoryJpaRepository.save(history);
            sr.setLastRunAt(now);
            sr.setNextRunAt(reportSchedulingService.computeNextRun(sr.getFrequency(), previousNext));
            scheduledReportJpaRepository.save(sr);
        }
    }
}
