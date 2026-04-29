package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.waitlist.WaitListDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentWaitListJpaRepository;
import com.citamedica.backend.domain.model.AppointmentWaitListEntry;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.model.WaitListEntryStatus;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.DuplicateEntityException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JoinWaitListUseCase {

    private static final List<WaitListEntryStatus> ACTIVE_BLOCKING = List.of(
            WaitListEntryStatus.WAITING, WaitListEntryStatus.CONTACTED);

    private final AppointmentWaitListJpaRepository waitListJpaRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicOfferingRepository clinicOfferingRepository;
    private final ClinicRepository clinicRepository;

    public JoinWaitListUseCase(
            AppointmentWaitListJpaRepository waitListJpaRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            ClinicOfferingRepository clinicOfferingRepository,
            ClinicRepository clinicRepository) {
        this.waitListJpaRepository = waitListJpaRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.clinicOfferingRepository = clinicOfferingRepository;
        this.clinicRepository = clinicRepository;
    }

    @Transactional
    public WaitListDtos.WaitListEntryResponse execute(Long patientId, WaitListDtos.JoinWaitListRequest request) {
        if (request.preferredStartDate() != null
                && request.preferredEndDate() != null
                && request.preferredStartDate().isAfter(request.preferredEndDate())) {
            throw new InvalidDomainOperationException("preferredStartDate must be on or before preferredEndDate");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + request.doctorId()));

        if (waitListJpaRepository.existsByPatient_IdAndDoctor_IdAndStatusIn(
                patientId, request.doctorId(), ACTIVE_BLOCKING)) {
            throw new DuplicateEntityException("Patient already has an active wait-list entry for this doctor");
        }

        AppointmentWaitListEntry entry = new AppointmentWaitListEntry();
        entry.setPatient(patient);
        entry.setDoctor(doctor);
        entry.setPreferredStartDate(request.preferredStartDate());
        entry.setPreferredEndDate(request.preferredEndDate());
        entry.setAppointmentType(request.appointmentType());
        entry.setNotes(request.notes());
        entry.setStatus(WaitListEntryStatus.WAITING);
        entry.setCreatedAt(LocalDateTime.now());

        if (request.serviceId() != null) {
            ClinicOffering service = clinicOfferingRepository.findById(request.serviceId())
                    .orElseThrow(() -> new EntityNotFoundDomainException("Service not found: " + request.serviceId()));
            entry.setService(service);
            entry.setClinic(service.getClinic());
        } else if (request.clinicId() != null) {
            Clinic clinic = clinicRepository.findById(request.clinicId())
                    .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + request.clinicId()));
            entry.setClinic(clinic);
        }

        AppointmentWaitListEntry saved = waitListJpaRepository.save(entry);
        return WaitListDtos.WaitListEntryResponse.from(saved);
    }
}
