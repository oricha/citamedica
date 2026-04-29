package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.application.usecase.UpdateWaitListEntryStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wait-list")
@PreAuthorize("hasAnyRole('STAFF','DOCTOR','CLINIC_MANAGER','ADMIN')")
public class WaitListEntryManagementController {

    private final UpdateWaitListEntryStatusUseCase updateWaitListEntryStatusUseCase;

    public WaitListEntryManagementController(UpdateWaitListEntryStatusUseCase updateWaitListEntryStatusUseCase) {
        this.updateWaitListEntryStatusUseCase = updateWaitListEntryStatusUseCase;
    }

    @PatchMapping("/{entryId}")
    public WaitListDtos.WaitListEntryResponse updateStatus(
            @PathVariable Long entryId,
            @Valid @RequestBody WaitListDtos.UpdateWaitListStatusRequest body) {
        return updateWaitListEntryStatusUseCase.execute(entryId, body);
    }
}
