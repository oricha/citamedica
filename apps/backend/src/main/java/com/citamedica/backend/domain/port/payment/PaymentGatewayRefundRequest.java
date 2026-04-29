package com.citamedica.backend.domain.port.payment;

import java.math.BigDecimal;

public record PaymentGatewayRefundRequest(
        String providerTransactionId,
        BigDecimal amount,
        String currency) {}
