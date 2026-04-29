package com.citamedica.backend.application.usecase;

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
public class CancelMyWaitListEntryUseCase {

    private static final EnumSet<WaitListEntryStatus> CANCELLABLE =
            EnumSet.of(WaitListEntryStatus.WAITING, WaitListEntryStatus.CONTACTED);

    private final AppointmentWaitListJpaRepository waitListJpaRepository;

    public CancelMyWaitListEntryUseCase(AppointmentWaitListJpaRepository waitListJpaRepository) {
        this.waitListJpaRepository = waitListJpaRepository;
    }

    @Transactional
    public void execute(Long patientId, Long entryId) {
        AppointmentWaitListEntry entry = waitListJpaRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Wait-list entry not found: " + entryId));
        if (!entry.getPatient().getId().equals(patientId)) {
            throw new EntityNotFoundDomainException("Wait-list entry not found: " + entryId);
        }
        if (!CANCELLABLE.contains(entry.getStatus())) {
            throw new InvalidDomainOperationException("This wait-list entry cannot be cancelled");
        }
        entry.setStatus(WaitListEntryStatus.CANCELLED);
        entry.setUpdatedAt(LocalDateTime.now());
        waitListJpaRepository.save(entry);
    }
}
