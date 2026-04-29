package com.citamedica.backend.adapter.out.integration.paypal;

import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeResult;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundResult;
import com.citamedica.backend.exception.domain.PaymentException;
import org.springframework.stereotype.Component;

/**
 * Phase-1 placeholder for PayPal; real SDK integration can replace these methods.
 */
@Component
public class PayPalPaymentGateway {

    public PaymentGatewayChargeResult charge(PaymentGatewayChargeRequest request) {
        throw new PaymentException("PayPal processing is not enabled in this deployment");
    }

    public PaymentGatewayRefundResult refund(PaymentGatewayRefundRequest request) {
        throw new PaymentException("PayPal refunds are not enabled");
    }
}
