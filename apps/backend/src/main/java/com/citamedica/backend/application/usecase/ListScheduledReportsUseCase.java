package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.analytics.ScheduledReport;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.ScheduledReportJpaRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListScheduledReportsUseCase {

    private final ClinicRepository clinicRepository;
    private final ScheduledReportJpaRepository scheduledReportJpaRepository;

    public ListScheduledReportsUseCase(
            ClinicRepository clinicRepository,
            ScheduledReportJpaRepository scheduledReportJpaRepository) {
        this.clinicRepository = clinicRepository;
        this.scheduledReportJpaRepository = scheduledReportJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduledReport> execute(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        return scheduledReportJpaRepository.findByClinic_IdOrderByNextRunAtAsc(clinicId);
    }
}
