package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Refund;
import com.citamedica.backend.domain.model.RefundStatus;

import java.math.BigDecimal;
import java.util.Optional;

public interface RefundRepository {

    Optional<Refund> findById(Long id);

    BigDecimal sumCompletedRefundsForPayment(Long paymentId);

    Refund save(Refund entity);

    void deleteAll();
}
