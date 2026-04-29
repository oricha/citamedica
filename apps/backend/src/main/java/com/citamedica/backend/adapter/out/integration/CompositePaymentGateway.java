package com.citamedica.backend.adapter.out.integration;

import com.citamedica.backend.adapter.out.integration.paypal.PayPalPaymentGateway;
import com.citamedica.backend.adapter.out.integration.stripe.StripePaymentGateway;
import com.citamedica.backend.domain.model.PaymentProvider;
import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeResult;
import com.citamedica.backend.domain.port.payment.PaymentGatewayPort;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundResult;
import com.citamedica.backend.domain.port.payment.PaymentGatewayTransaction;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class CompositePaymentGateway implements PaymentGatewayPort {

    private final StripePaymentGateway stripe;
    private final PayPalPaymentGateway paypal;

    public CompositePaymentGateway(StripePaymentGateway stripe, PayPalPaymentGateway paypal) {
        this.stripe = stripe;
        this.paypal = paypal;
    }

    @Override
    public PaymentGatewayChargeResult charge(PaymentGatewayChargeRequest request) {
        if (request.provider() == PaymentProvider.PAYPAL) {
            return paypal.charge(request);
        }
        return stripe.charge(request);
    }

    @Override
    public PaymentGatewayRefundResult refund(PaymentGatewayRefundRequest request) {
        return stripe.refund(request);
    }

    @Override
    public Optional<PaymentGatewayTransaction> retrieveTransaction(String providerTransactionId) {
        return stripe.retrieveTransaction(providerTransactionId);
    }
}
