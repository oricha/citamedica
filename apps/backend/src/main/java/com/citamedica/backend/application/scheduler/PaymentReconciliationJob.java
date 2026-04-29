package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.application.usecase.ReconciliationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.payment.reconciliation.enabled", havingValue = "true")
public class PaymentReconciliationJob {

    private static final Logger log = LoggerFactory.getLogger(PaymentReconciliationJob.class);

    private final ReconciliationUseCase reconciliationUseCase;

    public PaymentReconciliationJob(ReconciliationUseCase reconciliationUseCase) {
        this.reconciliationUseCase = reconciliationUseCase;
    }

    @Scheduled(cron = "${app.payment.reconciliation.cron:0 0 2 * * *}")
    public void nightlyReconciliation() {
        log.info("Starting payment reconciliation job");
        reconciliationUseCase.executeSnapshotLog();
    }
}
