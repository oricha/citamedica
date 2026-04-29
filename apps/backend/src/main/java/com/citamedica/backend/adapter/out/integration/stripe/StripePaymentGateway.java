package com.citamedica.backend.adapter.out.integration.stripe;

import com.citamedica.backend.domain.model.PaymentProvider;
import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeResult;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundResult;
import com.citamedica.backend.domain.port.payment.PaymentGatewayTransaction;
import com.citamedica.backend.exception.domain.PaymentDeclinedException;
import com.citamedica.backend.exception.domain.PaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@Component
public class StripePaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(StripePaymentGateway.class);

    @Value("${app.payment.stripe.secret-key:}")
    private String secretKey;

    public PaymentGatewayChargeResult charge(PaymentGatewayChargeRequest request) {
        if (request.provider() != PaymentProvider.STRIPE) {
            throw new PaymentException("Stripe gateway received incompatible provider");
        }
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("app.payment.stripe.secret-key not set; returning stub successful payment for development");
            return new PaymentGatewayChargeResult(true, "stub_pi_" + System.currentTimeMillis(), null);
        }
        com.stripe.Stripe.apiKey = secretKey;
        try {
            long minorUnits = request.amount().movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
            PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                    .setAmount(minorUnits)
                    .setCurrency(request.currency().toLowerCase())
                    .setPaymentMethod(request.tokenOrPaymentMethodId())
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(false)
                                    .build());
            Map<String, String> meta = request.metadata();
            if (meta != null) {
                for (Map.Entry<String, String> e : meta.entrySet()) {
                    builder.putMetadata(e.getKey(), e.getValue());
                }
            }
            PaymentIntent pi = PaymentIntent.create(builder.build());
            if ("succeeded".equals(pi.getStatus())) {
                return new PaymentGatewayChargeResult(true, pi.getId(), null);
            }
            String reason = pi.getLastPaymentError() != null && pi.getLastPaymentError().getMessage() != null
                    ? pi.getLastPaymentError().getMessage()
                    : pi.getStatus();
            return new PaymentGatewayChargeResult(false, pi.getId(), reason);
        } catch (StripeException e) {
            throw new PaymentDeclinedException(e.getMessage() != null ? e.getMessage() : "Stripe error");
        }
    }

    public PaymentGatewayRefundResult refund(PaymentGatewayRefundRequest request) {
        if (secretKey == null || secretKey.isBlank()) {
            return new PaymentGatewayRefundResult(true, "stub_re_" + System.currentTimeMillis(), null);
        }
        com.stripe.Stripe.apiKey = secretKey;
        try {
            RefundCreateParams.Builder b = RefundCreateParams.builder()
                    .setPaymentIntent(request.providerTransactionId());
            if (request.amount() != null) {
                long minor = request.amount().movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
                b.setAmount(minor);
            }
            Refund r = Refund.create(b.build());
            if ("succeeded".equals(r.getStatus()) || "pending".equals(r.getStatus())) {
                return new PaymentGatewayRefundResult(true, r.getId(), null);
            }
            return new PaymentGatewayRefundResult(false, r.getId(), r.getFailureReason());
        } catch (StripeException e) {
            return new PaymentGatewayRefundResult(false, null, e.getMessage());
        }
    }

    public Optional<PaymentGatewayTransaction> retrieveTransaction(String providerTransactionId) {
        if (secretKey == null || secretKey.isBlank()) {
            return Optional.empty();
        }
        com.stripe.Stripe.apiKey = secretKey;
        try {
            PaymentIntent pi = PaymentIntent.retrieve(providerTransactionId);
            BigDecimal amt = BigDecimal.valueOf(pi.getAmount()).movePointLeft(2);
            return Optional.of(new PaymentGatewayTransaction(
                    pi.getId(),
                    pi.getStatus(),
                    amt,
                    pi.getCurrency() != null ? pi.getCurrency().toUpperCase() : "USD"));
        } catch (StripeException e) {
            return Optional.empty();
        }
    }
}
