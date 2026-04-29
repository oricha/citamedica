package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.billing.PatientOutstandingRowResponse;
import com.citamedica.backend.adapter.in.dto.billing.RevenueReportResponse;
import com.citamedica.backend.application.usecase.GetClinicOutstandingBalancesUseCase;
import com.citamedica.backend.application.usecase.GetClinicRevenueReportUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clinics/{clinicId}")
public class ClinicFinancialReportingController {

    private final GetClinicOutstandingBalancesUseCase getClinicOutstandingBalancesUseCase;
    private final GetClinicRevenueReportUseCase getClinicRevenueReportUseCase;

    public ClinicFinancialReportingController(
            GetClinicOutstandingBalancesUseCase getClinicOutstandingBalancesUseCase,
            GetClinicRevenueReportUseCase getClinicRevenueReportUseCase) {
        this.getClinicOutstandingBalancesUseCase = getClinicOutstandingBalancesUseCase;
        this.getClinicRevenueReportUseCase = getClinicRevenueReportUseCase;
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/outstanding-balances")
    public ResponseEntity<List<PatientOutstandingRowResponse>> outstanding(@PathVariable Long clinicId) {
        List<PatientOutstandingRowResponse> rows = getClinicOutstandingBalancesUseCase.execute(clinicId).stream()
                .map(r -> new PatientOutstandingRowResponse(r.patientId(), r.totalAmount()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(rows);
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/reports/revenue")
    public ResponseEntity<RevenueReportResponse> revenue(
            @PathVariable Long clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        var total = getClinicRevenueReportUseCase.execute(clinicId, from, to);
        return ResponseEntity.ok(new RevenueReportResponse(clinicId, total));
    }
}
