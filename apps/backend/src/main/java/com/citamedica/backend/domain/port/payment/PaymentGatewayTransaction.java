package com.citamedica.backend.domain.port.payment;

import java.math.BigDecimal;

public record PaymentGatewayTransaction(
        String id,
        String status,
        BigDecimal amount,
        String currency) {}
