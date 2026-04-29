package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Payment;
import com.citamedica.backend.domain.model.PaymentStatus;
import com.citamedica.backend.domain.model.Refund;
import com.citamedica.backend.domain.model.RefundReason;
import com.citamedica.backend.domain.model.RefundStatus;
import com.citamedica.backend.domain.repository.PaymentRepository;
import com.citamedica.backend.domain.repository.RefundRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RequestRefundUseCase {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    public RequestRefundUseCase(PaymentRepository paymentRepository, RefundRepository refundRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }

    @Transactional
    public Refund execute(Long paymentId, BigDecimal amount, RefundReason reason, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Payment not found: " + paymentId));
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidDomainOperationException("Only completed payments can be refunded");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDomainOperationException("Refund amount must be positive");
        }
        BigDecimal already = refundRepository.sumCompletedRefundsForPayment(paymentId);
        if (already.add(amount).compareTo(payment.getAmount()) > 0) {
            throw new InvalidDomainOperationException("Refund amount exceeds remaining refundable balance");
        }

        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(amount);
        refund.setReason(reason != null ? reason : RefundReason.PATIENT_REQUEST);
        refund.setStatus(RefundStatus.PENDING);
        refund.setNotes(notes);
        refund.setCreatedAt(LocalDateTime.now());
        return refundRepository.save(refund);
    }
}
