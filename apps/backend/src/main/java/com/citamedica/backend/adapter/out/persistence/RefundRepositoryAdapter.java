package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.RefundJpaRepository;
import com.citamedica.backend.domain.model.Refund;
import com.citamedica.backend.domain.model.RefundStatus;
import com.citamedica.backend.domain.repository.RefundRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class RefundRepositoryAdapter implements RefundRepository {

    private final RefundJpaRepository jpa;

    public RefundRepositoryAdapter(RefundJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Refund> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public BigDecimal sumCompletedRefundsForPayment(Long paymentId) {
        BigDecimal v = jpa.sumAmountByPaymentIdAndStatus(paymentId, RefundStatus.COMPLETED);
        return v != null ? v : BigDecimal.ZERO;
    }

    @Override
    public Refund save(Refund entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
