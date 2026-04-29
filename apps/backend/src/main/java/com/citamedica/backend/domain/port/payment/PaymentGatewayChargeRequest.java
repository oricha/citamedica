package com.citamedica.backend.domain.port.payment;

import com.citamedica.backend.domain.model.PaymentProvider;

import java.math.BigDecimal;
import java.util.Map;

public record PaymentGatewayChargeRequest(
        PaymentProvider provider,
        String tokenOrPaymentMethodId,
        BigDecimal amount,
        String currency,
        Map<String, String> metadata) {}
