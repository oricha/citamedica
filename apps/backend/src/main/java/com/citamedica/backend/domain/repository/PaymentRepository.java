package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findById(Long id);

    List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    java.math.BigDecimal sumCompletedAmountForClinicBetween(
            Long clinicId,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to);

    Payment save(Payment entity);

    void deleteAll();
}
