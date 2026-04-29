package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<Invoice> findByAppointment_Id(Long appointmentId);

    @Query("""
            SELECT COALESCE(SUM(i.amount), 0)
            FROM Invoice i
            WHERE i.patient.id = :patientId
            AND i.status IN (:unpaidStatuses)
            """)
    BigDecimal sumOutstandingForPatient(
            @Param("patientId") Long patientId,
            @Param("unpaidStatuses") List<InvoiceStatus> unpaidStatuses);

    @Query(value = """
            SELECT patient_id, COALESCE(SUM(amount), 0)
            FROM invoice
            WHERE clinic_id = :clinicId
            AND status IN ('DRAFT', 'SENT', 'OVERDUE')
            GROUP BY patient_id
            """, nativeQuery = true)
    List<Object[]> sumOutstandingByClinicGrouped(@Param("clinicId") Long clinicId);
}
