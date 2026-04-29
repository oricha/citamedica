package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.analytics.CreateReportRequest;
import com.citamedica.backend.application.service.ReportGenerationService;
import com.citamedica.backend.application.usecase.report.ReportFilterPayload;
import com.citamedica.backend.domain.model.analytics.ReportHistory;
import com.citamedica.backend.domain.model.analytics.ReportHistoryStatus;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.ReportHistoryJpaRepository;
import com.citamedica.backend.exception.domain.AnalyticsException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateAdHocReportUseCase {

    private final ClinicRepository clinicRepository;
    private final ReportHistoryJpaRepository reportHistoryJpaRepository;
    private final ReportGenerationService reportGenerationService;
    private final ObjectMapper objectMapper;

    public CreateAdHocReportUseCase(
            ClinicRepository clinicRepository,
            ReportHistoryJpaRepository reportHistoryJpaRepository,
            ReportGenerationService reportGenerationService,
            ObjectMapper objectMapper) {
        this.clinicRepository = clinicRepository;
        this.reportHistoryJpaRepository = reportHistoryJpaRepository;
        this.reportGenerationService =
                reportGenerationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReportHistory execute(Long clinicId, CreateReportRequest request) {
        var clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        String paramsJson;
        try {
            paramsJson = objectMapper.writeValueAsString(
                    new ReportFilterPayload(request.from(), request.to(), request.reportType()));
        } catch (JsonProcessingException e) {
            throw new AnalyticsException("Failed to serialize report parameters");
        }
        ReportHistory history = new ReportHistory();
        history.setClinic(clinic);
        history.setReportType(request.reportType());
        history.setExportFormat(request.exportFormat());
        history.setStatus(ReportHistoryStatus.PENDING);
        history.setFilterParams(paramsJson);
        history = reportHistoryJpaRepository.save(history);
        try {
            byte[] content = reportGenerationService.generate(
                    clinicId,
                    request.reportType(),
                    request.exportFormat(),
                    request.from(),
                    request.to());
            history.setContent(content);
            history.setStatus(ReportHistoryStatus.COMPLETED);
            history.setCompletedAt(LocalDateTime.now());
        } catch (RuntimeException ex) {
            history.setStatus(ReportHistoryStatus.FAILED);
            history.setErrorMessage(ex.getMessage());
        }
        return reportHistoryJpaRepository.save(history);
    }
}
