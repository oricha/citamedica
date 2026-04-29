package com.citamedica.backend.domain.port.payment;

public record PaymentGatewayChargeResult(
        boolean success,
        String providerTransactionId,
        String declineReason) {}
