package com.citamedica.backend.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RefreshAnalyticsViewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(RefreshAnalyticsViewsUseCase.class);

    public void execute() {
        log.info("Analytics SQL views are non-materialized; no refresh step is required.");
    }
}
