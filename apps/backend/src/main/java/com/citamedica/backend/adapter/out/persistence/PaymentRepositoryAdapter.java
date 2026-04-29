package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.PaymentJpaRepository;
import com.citamedica.backend.domain.model.Payment;
import com.citamedica.backend.domain.model.PaymentStatus;
import com.citamedica.backend.domain.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpa;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId) {
        return jpa.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public BigDecimal sumCompletedAmountForClinicBetween(Long clinicId, LocalDateTime from, LocalDateTime to) {
        BigDecimal v = jpa.sumAmountForClinicCompletedBetween(clinicId, from, to, PaymentStatus.COMPLETED);
        return v != null ? v : BigDecimal.ZERO;
    }

    @Override
    public Payment save(Payment entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
