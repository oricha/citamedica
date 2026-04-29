package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.InvoiceJpaRepository;
import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.model.InvoiceStatus;
import com.citamedica.backend.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private static final List<InvoiceStatus> UNPAID = Arrays.asList(
            InvoiceStatus.DRAFT, InvoiceStatus.SENT, InvoiceStatus.OVERDUE);

    private final InvoiceJpaRepository jpa;

    public InvoiceRepositoryAdapter(InvoiceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Invoice> findByAppointmentId(Long appointmentId) {
        return jpa.findByAppointment_Id(appointmentId);
    }

    @Override
    public List<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId) {
        return jpa.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public boolean existsByAppointmentId(Long appointmentId) {
        return jpa.findByAppointment_Id(appointmentId).isPresent();
    }

    @Override
    public BigDecimal sumOutstandingBalanceByPatientId(Long patientId) {
        BigDecimal v = jpa.sumOutstandingForPatient(patientId, UNPAID);
        return v != null ? v : BigDecimal.ZERO;
    }

    @Override
    public List<OutstandingBalanceRow> sumOutstandingByClinicGroupedByPatient(Long clinicId) {
        return jpa.sumOutstandingByClinicGrouped(clinicId).stream()
                .map(row -> new OutstandingBalanceRow(
                        ((Number) row[0]).longValue(),
                        row[1] instanceof BigDecimal b ? b : BigDecimal.valueOf(((Number) row[1]).doubleValue())))
                .collect(Collectors.toList());
    }

    @Override
    public Invoice save(Invoice entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
