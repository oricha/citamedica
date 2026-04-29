package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {

    Optional<Invoice> findById(Long id);

    Optional<Invoice> findByAppointmentId(Long appointmentId);

    List<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    boolean existsByAppointmentId(Long appointmentId);

    BigDecimal sumOutstandingBalanceByPatientId(Long patientId);

    List<OutstandingBalanceRow> sumOutstandingByClinicGroupedByPatient(Long clinicId);

    Invoice save(Invoice entity);

    void deleteAll();

    record OutstandingBalanceRow(Long patientId, BigDecimal totalAmount) {}
}
