package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.BlockType;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.repository.DoctorAvailabilityBlockRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateAvailabilityBlockUseCase {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityBlockRepository blockRepository;

    public CreateAvailabilityBlockUseCase(
            DoctorRepository doctorRepository,
            DoctorAvailabilityBlockRepository blockRepository) {
        this.doctorRepository = doctorRepository;
        this.blockRepository = blockRepository;
    }

    @Transactional
    public DoctorAvailabilityBlock execute(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end,
            BlockType blockType,
            String recurrenceRule) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Block end must be after start");
        }
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        DoctorAvailabilityBlock block = new DoctorAvailabilityBlock();
        block.setDoctor(doctor);
        block.setStartTime(start);
        block.setEndTime(end);
        block.setBlockType(blockType);
        block.setRecurrenceRule(recurrenceRule);
        block.setCreatedAt(LocalDateTime.now());
        return blockRepository.save(block);
    }

    public java.util.List<DoctorAvailabilityBlock> list(Long doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        return blockRepository.findActiveByDoctorId(doctorId);
    }

    @Transactional
    public void softDelete(Long doctorId, Long blockId) {
        DoctorAvailabilityBlock block = blockRepository.findById(blockId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Block not found: " + blockId));
        if (!block.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Block does not belong to doctor");
        }
        block.setDeletedAt(LocalDateTime.now());
        blockRepository.save(block);
    }
}
