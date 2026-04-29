package com.citamedica.backend.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnalyticsRepository {

    record OccupancyRow(
            Long clinicId,
            Long doctorId,
            LocalDate slotDate,
            long totalSlots,
            long bookedSlots,
            BigDecimal occupancyRate
    ) {}

    record RevenueRow(
            Long clinicId,
            Long doctorId,
            String specialtyName,
            String serviceName,
            LocalDate revenueDate,
            BigDecimal revenue
    ) {}

    record CollectionsRow(
            Long clinicId,
            BigDecimal outstandingBalance,
            BigDecimal overdueBalance,
            long patientCount
    ) {}

    record PatientRetentionRow(
            Long clinicId,
            long totalPatients,
            long activePatients,
            BigDecimal churnRate
    ) {}

    List<OccupancyRow> findOccupancyForClinic(Long clinicId, LocalDate from, LocalDate to);

    List<RevenueRow> findRevenueForClinic(Long clinicId, LocalDate from, LocalDate to);

    CollectionsRow getCollectionsSummary(Long clinicId);

    Optional<PatientRetentionRow> findPatientRetention(Long clinicId);

    BigDecimal sumRevenueOnDate(Long clinicId, LocalDate date);

    long countAppointmentsOnDateForClinic(Long clinicId, LocalDate date);

    BigDecimal averageOccupancyRateForClinic(Long clinicId, LocalDate from, LocalDate to);

    long countAppointmentsOnDateForDoctor(Long doctorId, LocalDate date);

    BigDecimal averageOccupancyRateForDoctor(Long doctorId, LocalDate from, LocalDate to);

    List<OccupancyRow> findOccupancyForDoctor(Long doctorId, LocalDate from, LocalDate to);
}
