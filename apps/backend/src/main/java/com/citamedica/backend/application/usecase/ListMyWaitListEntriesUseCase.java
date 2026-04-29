package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentWaitListJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListMyWaitListEntriesUseCase {

    private final AppointmentWaitListJpaRepository waitListJpaRepository;

    public ListMyWaitListEntriesUseCase(AppointmentWaitListJpaRepository waitListJpaRepository) {
        this.waitListJpaRepository = waitListJpaRepository;
    }

    @Transactional(readOnly = true)
    public Page<WaitListDtos.WaitListEntryResponse> execute(Long patientId, Pageable pageable) {
        return waitListJpaRepository.findByPatient_IdOrderByCreatedAtDesc(patientId, pageable)
                .map(WaitListDtos.WaitListEntryResponse::from);
    }
}
