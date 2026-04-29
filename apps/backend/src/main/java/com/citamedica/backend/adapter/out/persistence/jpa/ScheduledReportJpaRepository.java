package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.analytics.ScheduledReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledReportJpaRepository extends JpaRepository<ScheduledReport, Long> {

    List<ScheduledReport> findByActiveTrueAndNextRunAtLessThanEqual(LocalDateTime threshold);

    List<ScheduledReport> findByClinic_IdOrderByNextRunAtAsc(Long clinicId);
}
