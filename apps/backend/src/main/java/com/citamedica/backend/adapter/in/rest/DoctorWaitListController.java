package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.application.usecase.ListDoctorWaitListUseCase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}")
public class DoctorWaitListController {

    private final ListDoctorWaitListUseCase listDoctorWaitListUseCase;

    public DoctorWaitListController(ListDoctorWaitListUseCase listDoctorWaitListUseCase) {
        this.listDoctorWaitListUseCase = listDoctorWaitListUseCase;
    }

    @GetMapping("/wait-list")
    @PreAuthorize("hasAnyRole('STAFF','DOCTOR','CLINIC_MANAGER','ADMIN')")
    public org.springframework.data.domain.Page<WaitListDtos.WaitListEntryResponse> listForDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @PageableDefault(size = 50) Pageable pageable) {
        return listDoctorWaitListUseCase.execute(doctorId, activeOnly, pageable);
    }
}
