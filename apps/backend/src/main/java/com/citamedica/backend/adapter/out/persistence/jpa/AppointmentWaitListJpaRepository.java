package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.AppointmentWaitListEntry;
import com.citamedica.backend.domain.model.WaitListEntryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AppointmentWaitListJpaRepository extends JpaRepository<AppointmentWaitListEntry, Long> {

    boolean existsByPatient_IdAndDoctor_IdAndStatusIn(
            Long patientId,
            Long doctorId,
            Collection<WaitListEntryStatus> statuses);

    Page<AppointmentWaitListEntry> findByPatient_IdOrderByCreatedAtDesc(Long patientId, Pageable pageable);

    Page<AppointmentWaitListEntry> findByDoctor_IdAndStatusInOrderByCreatedAtAsc(
            Long doctorId,
            Collection<WaitListEntryStatus> statuses,
            Pageable pageable);

    Page<AppointmentWaitListEntry> findByDoctor_IdOrderByCreatedAtAsc(Long doctorId, Pageable pageable);
}
