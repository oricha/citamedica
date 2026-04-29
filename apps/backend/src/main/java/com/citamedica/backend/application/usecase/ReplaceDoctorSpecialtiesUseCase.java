package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.catalog.ReplaceDoctorSpecialtiesRequest;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.DoctorSpecialty;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.DoctorSpecialtyRepository;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import com.citamedica.backend.exception.domain.InvalidSpecialtyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReplaceDoctorSpecialtiesUseCase {

    private final DoctorRepository doctorRepository;
    private final DoctorSpecialtyRepository doctorSpecialtyRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;

    public ReplaceDoctorSpecialtiesUseCase(
            DoctorRepository doctorRepository,
            DoctorSpecialtyRepository doctorSpecialtyRepository,
            MedicalSpecialtyRepository medicalSpecialtyRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorSpecialtyRepository = doctorSpecialtyRepository;
        this.medicalSpecialtyRepository = medicalSpecialtyRepository;
    }

    @Transactional
    public List<DoctorSpecialty> execute(Long doctorId, ReplaceDoctorSpecialtiesRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));

        List<ReplaceDoctorSpecialtiesRequest.Entry> entries = request.getSpecialties();
        long primaryCount = entries.stream().filter(ReplaceDoctorSpecialtiesRequest.Entry::isPrimary).count();
        if (primaryCount != 1) {
            throw new InvalidDomainOperationException("Exactly one specialty must be marked primary");
        }

        for (ReplaceDoctorSpecialtiesRequest.Entry entry : entries) {
            medicalSpecialtyRepository.findById(entry.getSpecialtyId())
                    .orElseThrow(() -> new InvalidSpecialtyException("Unknown specialty id: " + entry.getSpecialtyId()));
        }

        doctorSpecialtyRepository.deleteAllByDoctorId(doctorId);

        for (ReplaceDoctorSpecialtiesRequest.Entry entry : entries) {
            MedicalSpecialty specialty = medicalSpecialtyRepository.findById(entry.getSpecialtyId()).orElseThrow();
            DoctorSpecialty ds = new DoctorSpecialty();
            ds.setDoctor(doctor);
            ds.setSpecialty(specialty);
            ds.setPrimarySpecialty(entry.isPrimary());
            ds.setAssignedAt(LocalDateTime.now());
            ds.setOverrideDurationMinutes(entry.getOverrideDurationMinutes());
            ds.validateAssignment();
            doctorSpecialtyRepository.save(ds);
        }

        return doctorSpecialtyRepository.findByDoctorId(doctorId);
    }
}
