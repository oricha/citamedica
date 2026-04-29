package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.application.usecase.RetryFailedNotificationsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RetrySchedulerJob {

    private static final Logger log = LoggerFactory.getLogger(RetrySchedulerJob.class);

    private final RetryFailedNotificationsUseCase retryFailedNotificationsUseCase;

    public RetrySchedulerJob(RetryFailedNotificationsUseCase retryFailedNotificationsUseCase) {
        this.retryFailedNotificationsUseCase = retryFailedNotificationsUseCase;
    }

    @Scheduled(fixedDelay = 300000)
    public void run() {
        int processed = retryFailedNotificationsUseCase.execute();
        log.info("Retry job processed {} notifications", processed);
    }
}
