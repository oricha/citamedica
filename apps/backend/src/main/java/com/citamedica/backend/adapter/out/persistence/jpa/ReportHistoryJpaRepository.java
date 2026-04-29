package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.analytics.ReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportHistoryJpaRepository extends JpaRepository<ReportHistory, Long> {

    List<ReportHistory> findByClinic_IdOrderByCreatedAtDesc(Long clinicId);

    Optional<ReportHistory> findByIdAndClinic_Id(Long id, Long clinicId);
}
