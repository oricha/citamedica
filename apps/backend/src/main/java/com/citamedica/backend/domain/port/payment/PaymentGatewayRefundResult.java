package com.citamedica.backend.domain.port.payment;

public record PaymentGatewayRefundResult(
        boolean success,
        String providerRefundId,
        String failureMessage) {}
