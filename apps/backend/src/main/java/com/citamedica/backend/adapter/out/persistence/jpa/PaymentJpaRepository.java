package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Payment;
import com.citamedica.backend.domain.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = :status
            AND p.completedAt >= :from
            AND p.completedAt <= :to
            AND p.appointment IS NOT NULL
            AND p.appointment.doctor.clinic.id = :clinicId
            """)
    BigDecimal sumAmountForClinicCompletedBetween(
            @Param("clinicId") Long clinicId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") PaymentStatus status);
}
