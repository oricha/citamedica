package com.citamedica.backend.domain.port.payment;

import java.util.Optional;

public interface PaymentGatewayPort {

    PaymentGatewayChargeResult charge(PaymentGatewayChargeRequest request);

    PaymentGatewayRefundResult refund(PaymentGatewayRefundRequest request);

    Optional<PaymentGatewayTransaction> retrieveTransaction(String providerTransactionId);
}
