package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Payment;
import com.citamedica.backend.domain.model.PaymentStatus;
import com.citamedica.backend.domain.model.Refund;
import com.citamedica.backend.domain.model.RefundStatus;
import com.citamedica.backend.domain.port.payment.PaymentGatewayPort;
import com.citamedica.backend.domain.port.payment.PaymentGatewayRefundRequest;
import com.citamedica.backend.domain.repository.PaymentRepository;
import com.citamedica.backend.domain.repository.RefundRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ResolveRefundUseCase {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayPort paymentGateway;

    public ResolveRefundUseCase(
            RefundRepository refundRepository,
            PaymentRepository paymentRepository,
            PaymentGatewayPort paymentGateway) {
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public Refund execute(Long refundId, boolean approve, String reviewerNotes) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Refund not found: " + refundId));
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new InvalidDomainOperationException("Refund is not pending");
        }
        if (!approve) {
            refund.setStatus(RefundStatus.REJECTED);
            if (reviewerNotes != null) {
                refund.setNotes(reviewerNotes);
            }
            return refundRepository.save(refund);
        }

        Payment payment = refund.getPayment();
        String txId = payment.getProviderTransactionId();
        if (txId == null || txId.isBlank()) {
            throw new InvalidDomainOperationException("Original payment has no provider transaction id");
        }

        var result = paymentGateway.refund(new PaymentGatewayRefundRequest(
                txId,
                refund.getAmount(),
                payment.getCurrency()));

        if (!result.success()) {
            refund.setStatus(RefundStatus.FAILED);
            refund.setNotes(result.failureMessage());
            return refundRepository.save(refund);
        }

        refund.setStatus(RefundStatus.COMPLETED);
        refund.setProviderRefundId(result.providerRefundId());
        refund.setCompletedAt(LocalDateTime.now());
        if (reviewerNotes != null) {
            refund.setNotes(reviewerNotes);
        }
        refundRepository.save(refund);

        BigDecimal totalRefunded = refundRepository.sumCompletedRefundsForPayment(payment.getId());
        if (totalRefunded.compareTo(payment.getAmount()) >= 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        paymentRepository.save(payment);
        return refund;
    }
}
