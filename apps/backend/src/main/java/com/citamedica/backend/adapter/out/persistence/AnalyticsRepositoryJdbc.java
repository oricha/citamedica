package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.domain.repository.AnalyticsRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AnalyticsRepositoryJdbc implements AnalyticsRepository {

    private static final RowMapper<OccupancyRow> OCCUPANCY_ROW = new RowMapper<>() {
        @Override
        public OccupancyRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OccupancyRow(
                    rs.getLong("clinic_id"),
                    rs.getLong("doctor_id"),
                    rs.getDate("slot_date").toLocalDate(),
                    rs.getLong("total_slots"),
                    rs.getLong("booked_slots"),
                    readDecimal(rs, "occupancy_rate")
            );
        }
    };

    private static final RowMapper<RevenueRow> REVENUE_ROW = new RowMapper<>() {
        @Override
        public RevenueRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long doctorId = rs.getObject("doctor_id") != null ? rs.getLong("doctor_id") : null;
            return new RevenueRow(
                    rs.getLong("clinic_id"),
                    doctorId,
                    rs.getString("specialty_name"),
                    rs.getString("service_name"),
                    rs.getDate("revenue_date").toLocalDate(),
                    readDecimal(rs, "revenue")
            );
        }
    };

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static BigDecimal readDecimal(ResultSet rs, String column) throws SQLException {
        BigDecimal v = rs.getBigDecimal(column);
        return v != null ? v : BigDecimal.ZERO;
    }

    @Override
    public List<OccupancyRow> findOccupancyForClinic(Long clinicId, LocalDate from, LocalDate to) {
        return jdbcTemplate.query(
                """
                        SELECT clinic_id, doctor_id, slot_date, total_slots, booked_slots, occupancy_rate
                        FROM clinic_occupancy_view
                        WHERE clinic_id = ? AND slot_date BETWEEN ? AND ?
                        ORDER BY slot_date DESC, doctor_id
                        """,
                OCCUPANCY_ROW,
                clinicId,
                Date.valueOf(from),
                Date.valueOf(to)
        );
    }

    @Override
    public List<RevenueRow> findRevenueForClinic(Long clinicId, LocalDate from, LocalDate to) {
        return jdbcTemplate.query(
                """
                        SELECT clinic_id, doctor_id, specialty_name, service_name, revenue_date, revenue
                        FROM clinic_revenue_view
                        WHERE clinic_id = ? AND revenue_date BETWEEN ? AND ?
                        ORDER BY revenue_date DESC, doctor_id
                        """,
                REVENUE_ROW,
                clinicId,
                Date.valueOf(from),
                Date.valueOf(to)
        );
    }

    @Override
    public CollectionsRow getCollectionsSummary(Long clinicId) {
        List<CollectionsRow> rows = jdbcTemplate.query(
                """
                        SELECT clinic_id, outstanding_balance, overdue_balance, patient_count
                        FROM clinic_collections_view
                        WHERE clinic_id = ?
                        """,
                (rs, rowNum) -> new CollectionsRow(
                        rs.getLong("clinic_id"),
                        readDecimal(rs, "outstanding_balance"),
                        readDecimal(rs, "overdue_balance"),
                        rs.getLong("patient_count")
                ),
                clinicId
        );
        if (rows.isEmpty()) {
            return new CollectionsRow(clinicId, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
        }
        return rows.getFirst();
    }

    @Override
    public Optional<PatientRetentionRow> findPatientRetention(Long clinicId) {
        List<PatientRetentionRow> rows = jdbcTemplate.query(
                """
                        SELECT clinic_id, total_patients, active_patients, churn_rate
                        FROM patient_retention_view
                        WHERE clinic_id = ?
                        """,
                (rs, rowNum) -> new PatientRetentionRow(
                        rs.getLong("clinic_id"),
                        rs.getLong("total_patients"),
                        rs.getLong("active_patients"),
                        readDecimal(rs, "churn_rate")
                ),
                clinicId
        );
        return rows.stream().findFirst();
    }

    @Override
    public BigDecimal sumRevenueOnDate(Long clinicId, LocalDate date) {
        BigDecimal sum = jdbcTemplate.queryForObject(
                """
                        SELECT COALESCE(SUM(revenue), 0)
                        FROM clinic_revenue_view
                        WHERE clinic_id = ? AND revenue_date = ?
                        """,
                BigDecimal.class,
                clinicId,
                Date.valueOf(date)
        );
        return sum != null ? sum.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    @Override
    public long countAppointmentsOnDateForClinic(Long clinicId, LocalDate date) {
        Long count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM appointment
                        WHERE clinic_id = ? AND CAST(start_at AS DATE) = ?
                        """,
                Long.class,
                clinicId,
                Date.valueOf(date)
        );
        return count != null ? count : 0L;
    }

    @Override
    public BigDecimal averageOccupancyRateForClinic(Long clinicId, LocalDate from, LocalDate to) {
        BigDecimal avg = jdbcTemplate.queryForObject(
                """
                        SELECT COALESCE(AVG(occupancy_rate), 0)
                        FROM clinic_occupancy_view
                        WHERE clinic_id = ? AND slot_date BETWEEN ? AND ?
                        """,
                BigDecimal.class,
                clinicId,
                Date.valueOf(from),
                Date.valueOf(to)
        );
        return avg != null ? avg.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    @Override
    public long countAppointmentsOnDateForDoctor(Long doctorId, LocalDate date) {
        Long count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM appointment
                        WHERE doctor_id = ? AND CAST(start_at AS DATE) = ?
                        """,
                Long.class,
                doctorId,
                Date.valueOf(date)
        );
        return count != null ? count : 0L;
    }

    @Override
    public BigDecimal averageOccupancyRateForDoctor(Long doctorId, LocalDate from, LocalDate to) {
        BigDecimal avg = jdbcTemplate.queryForObject(
                """
                        SELECT COALESCE(AVG(occupancy_rate), 0)
                        FROM clinic_occupancy_view
                        WHERE doctor_id = ? AND slot_date BETWEEN ? AND ?
                        """,
                BigDecimal.class,
                doctorId,
                Date.valueOf(from),
                Date.valueOf(to)
        );
        return avg != null ? avg.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    @Override
    public List<OccupancyRow> findOccupancyForDoctor(Long doctorId, LocalDate from, LocalDate to) {
        return jdbcTemplate.query(
                """
                        SELECT clinic_id, doctor_id, slot_date, total_slots, booked_slots, occupancy_rate
                        FROM clinic_occupancy_view
                        WHERE doctor_id = ? AND slot_date BETWEEN ? AND ?
                        ORDER BY slot_date DESC
                        """,
                OCCUPANCY_ROW,
                doctorId,
                Date.valueOf(from),
                Date.valueOf(to)
        );
    }
}
