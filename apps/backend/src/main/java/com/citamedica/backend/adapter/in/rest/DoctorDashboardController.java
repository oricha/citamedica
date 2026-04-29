package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.analytics.DoctorDashboardResponse;
import com.citamedica.backend.application.usecase.GetDoctorDashboardUseCase;
import com.citamedica.backend.domain.model.analytics.DoctorDashboardData;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}")
public class DoctorDashboardController {

    private final GetDoctorDashboardUseCase getDoctorDashboardUseCase;

    public DoctorDashboardController(GetDoctorDashboardUseCase getDoctorDashboardUseCase) {
        this.getDoctorDashboardUseCase = getDoctorDashboardUseCase;
    }

    @PreAuthorize("hasAnyRole('DOCTOR','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<DoctorDashboardResponse> dashboard(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate asOf = date != null ? date : LocalDate.now();
        DoctorDashboardData data = getDoctorDashboardUseCase.execute(doctorId, asOf);
        return ResponseEntity.ok(new DoctorDashboardResponse(data.appointmentsToday(), data.avgOccupancyLast7Days()));
    }
}
