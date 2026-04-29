package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.port.payment.PaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconciliationUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationUseCase.class);

    private final PaymentGatewayPort paymentGateway;

    public ReconciliationUseCase(PaymentGatewayPort paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Transactional(readOnly = true)
    public void executeSnapshotLog() {
        log.info("Payment reconciliation: batch compare not implemented; use Stripe dashboard with local payment exports.");
    }

    @Transactional(readOnly = true)
    public boolean quickCheckProviderVsLocal(String stripePaymentIntentId) {
        return paymentGateway.retrieveTransaction(stripePaymentIntentId).isPresent();
    }
}
