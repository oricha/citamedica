package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.application.usecase.ProcessDueScheduledReportsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.analytics.scheduled-reports.enabled", havingValue = "true")
public class ScheduledReportJob {

    private static final Logger log = LoggerFactory.getLogger(ScheduledReportJob.class);

    private final ProcessDueScheduledReportsUseCase processDueScheduledReportsUseCase;

    public ScheduledReportJob(ProcessDueScheduledReportsUseCase processDueScheduledReportsUseCase) {
        this.processDueScheduledReportsUseCase = processDueScheduledReportsUseCase;
    }

    @Scheduled(cron = "${app.analytics.scheduled-reports.cron:0 0 6 * * *}")
    public void runDueReports() {
        log.debug("Running scheduled report job");
        processDueScheduledReportsUseCase.execute();
    }
}
