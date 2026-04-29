package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.application.usecase.SyncCalComCalendarUseCase;
import com.citamedica.backend.config.AvailabilityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class CalComSyncJob {

    private static final Logger log = LoggerFactory.getLogger(CalComSyncJob.class);

    private final AvailabilityProperties availabilityProperties;
    private final SyncCalComCalendarUseCase syncCalComCalendarUseCase;

    public CalComSyncJob(AvailabilityProperties availabilityProperties, SyncCalComCalendarUseCase syncCalComCalendarUseCase) {
        this.availabilityProperties = availabilityProperties;
        this.syncCalComCalendarUseCase = syncCalComCalendarUseCase;
    }

    @Scheduled(fixedDelayString = "${app.availability.sync.fixed-delay-ms:21600000}")
    public void run() {
        if (!availabilityProperties.getSync().isEnabled()) {
            return;
        }
        log.info("Starting Cal.com availability sync job");
        syncCalComCalendarUseCase.execute(null);
        log.info("Cal.com availability sync job completed");
    }
}
