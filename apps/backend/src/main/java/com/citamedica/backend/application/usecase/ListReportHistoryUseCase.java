package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.analytics.ReportHistory;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.ReportHistoryJpaRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListReportHistoryUseCase {

    private final ClinicRepository clinicRepository;
    private final ReportHistoryJpaRepository reportHistoryJpaRepository;

    public ListReportHistoryUseCase(ClinicRepository clinicRepository, ReportHistoryJpaRepository reportHistoryJpaRepository) {
        this.clinicRepository = clinicRepository;
        this.reportHistoryJpaRepository = reportHistoryJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<ReportHistory> execute(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        return reportHistoryJpaRepository.findByClinic_IdOrderByCreatedAtDesc(clinicId);
    }
}
