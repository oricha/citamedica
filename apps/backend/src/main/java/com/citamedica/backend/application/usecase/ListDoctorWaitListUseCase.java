package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentWaitListJpaRepository;
import com.citamedica.backend.domain.model.WaitListEntryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListDoctorWaitListUseCase {

    private static final List<WaitListEntryStatus> DEFAULT_OPEN =
            List.of(WaitListEntryStatus.WAITING, WaitListEntryStatus.CONTACTED);

    private final AppointmentWaitListJpaRepository waitListJpaRepository;

    public ListDoctorWaitListUseCase(AppointmentWaitListJpaRepository waitListJpaRepository) {
        this.waitListJpaRepository = waitListJpaRepository;
    }

    @Transactional(readOnly = true)
    public Page<WaitListDtos.WaitListEntryResponse> execute(Long doctorId, boolean activeOnly, Pageable pageable) {
        if (activeOnly) {
            return waitListJpaRepository
                    .findByDoctor_IdAndStatusInOrderByCreatedAtAsc(doctorId, DEFAULT_OPEN, pageable)
                    .map(WaitListDtos.WaitListEntryResponse::from);
        }
        return waitListJpaRepository.findByDoctor_IdOrderByCreatedAtAsc(doctorId, pageable)
                .map(WaitListDtos.WaitListEntryResponse::from);
    }
}
