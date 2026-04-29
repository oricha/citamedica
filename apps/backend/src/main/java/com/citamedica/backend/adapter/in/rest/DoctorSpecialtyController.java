package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.catalog.DoctorSpecialtyResponse;
import com.citamedica.backend.adapter.in.dto.catalog.ReplaceDoctorSpecialtiesRequest;
import com.citamedica.backend.application.usecase.GetDoctorSpecialtiesUseCase;
import com.citamedica.backend.application.usecase.ReplaceDoctorSpecialtiesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/specialties")
public class DoctorSpecialtyController {

    private final ReplaceDoctorSpecialtiesUseCase replaceDoctorSpecialtiesUseCase;
    private final GetDoctorSpecialtiesUseCase getDoctorSpecialtiesUseCase;

    public DoctorSpecialtyController(
            ReplaceDoctorSpecialtiesUseCase replaceDoctorSpecialtiesUseCase,
            GetDoctorSpecialtiesUseCase getDoctorSpecialtiesUseCase) {
        this.replaceDoctorSpecialtiesUseCase = replaceDoctorSpecialtiesUseCase;
        this.getDoctorSpecialtiesUseCase = getDoctorSpecialtiesUseCase;
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PatchMapping
    public ResponseEntity<List<DoctorSpecialtyResponse>> replace(
            @PathVariable Long doctorId,
            @Valid @RequestBody ReplaceDoctorSpecialtiesRequest request) {
        var updated = replaceDoctorSpecialtiesUseCase.execute(doctorId, request);
        return ResponseEntity.ok(updated.stream().map(DoctorSpecialtyResponse::from).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN','STAFF','DOCTOR')")
    @GetMapping
    public ResponseEntity<List<DoctorSpecialtyResponse>> list(@PathVariable Long doctorId) {
        var list = getDoctorSpecialtiesUseCase.execute(doctorId);
        return ResponseEntity.ok(list.stream().map(DoctorSpecialtyResponse::from).collect(Collectors.toList()));
    }
}
