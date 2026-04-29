package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentWaitListJpaRepository;
import com.citamedica.backend.domain.model.AppointmentWaitListEntry;
import com.citamedica.backend.domain.model.WaitListEntryStatus;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
public class UpdateWaitListEntryStatusUseCase {

    private static final EnumSet<WaitListEntryStatus> STAFF_ALLOWED = EnumSet.of(
            WaitListEntryStatus.WAITING,
            WaitListEntryStatus.CONTACTED,
            WaitListEntryStatus.FULFILLED,
            WaitListEntryStatus.CANCELLED,
            WaitListEntryStatus.EXPIRED);

    private final AppointmentWaitListJpaRepository waitListJpaRepository;

    public UpdateWaitListEntryStatusUseCase(AppointmentWaitListJpaRepository waitListJpaRepository) {
        this.waitListJpaRepository = waitListJpaRepository;
    }

    @Transactional
    public WaitListDtos.WaitListEntryResponse execute(Long entryId, WaitListDtos.UpdateWaitListStatusRequest request) {
        return execute(entryId, request.status());
    }

    private WaitListDtos.WaitListEntryResponse execute(Long entryId, WaitListEntryStatus newStatus) {
        if (!STAFF_ALLOWED.contains(newStatus)) {
            throw new InvalidDomainOperationException("Unsupported status transition");
        }
        AppointmentWaitListEntry entry = waitListJpaRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Wait-list entry not found: " + entryId));
        if (entry.getStatus() == WaitListEntryStatus.FULFILLED || entry.getStatus() == WaitListEntryStatus.CANCELLED) {
            throw new InvalidDomainOperationException("Wait-list entry is already closed");
        }
        entry.setStatus(newStatus);
        entry.setUpdatedAt(LocalDateTime.now());
        return WaitListDtos.WaitListEntryResponse.from(waitListJpaRepository.save(entry));
    }
}
