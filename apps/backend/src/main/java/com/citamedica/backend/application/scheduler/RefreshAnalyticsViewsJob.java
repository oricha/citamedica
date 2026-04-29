package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.application.usecase.RefreshAnalyticsViewsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.analytics.refresh-views.enabled", havingValue = "true")
public class RefreshAnalyticsViewsJob {

    private static final Logger log = LoggerFactory.getLogger(RefreshAnalyticsViewsJob.class);

    private final RefreshAnalyticsViewsUseCase refreshAnalyticsViewsUseCase;

    public RefreshAnalyticsViewsJob(RefreshAnalyticsViewsUseCase refreshAnalyticsViewsUseCase) {
        this.refreshAnalyticsViewsUseCase = refreshAnalyticsViewsUseCase;
    }

    @Scheduled(cron = "${app.analytics.refresh-views.cron:0 0 1 * * *}")
    public void nightly() {
        log.info("Analytics views refresh job tick");
        refreshAnalyticsViewsUseCase.execute();
    }
}
