package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.analytics.CreateScheduledReportRequest;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.analytics.ScheduledReport;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.ScheduledReportJpaRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class CreateScheduledReportUseCase {

    private final ClinicRepository clinicRepository;
    private final ScheduledReportJpaRepository scheduledReportJpaRepository;

    public CreateScheduledReportUseCase(
            ClinicRepository clinicRepository,
            ScheduledReportJpaRepository scheduledReportJpaRepository) {
        this.clinicRepository = clinicRepository;
        this.scheduledReportJpaRepository = scheduledReportJpaRepository;
    }

    @Transactional
    public ScheduledReport execute(Long clinicId, CreateScheduledReportRequest request) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        LocalDateTime first = request.firstRunAt() != null
                ? request.firstRunAt()
                : LocalDate.now().plusDays(1).atTime(6, 0);
        ScheduledReport scheduled = new ScheduledReport();
        scheduled.setClinic(clinic);
        scheduled.setReportType(request.reportType());
        scheduled.setFrequency(request.frequency());
        scheduled.setRecipients(request.recipients().stream().map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.joining(",")));
        scheduled.setNextRunAt(first);
        scheduled.setActive(true);
        return scheduledReportJpaRepository.save(scheduled);
    }
}
