package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.TimeSlot;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.domain.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueryAvailableSlotsUseCase {

    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;

    public QueryAvailableSlotsUseCase(DoctorRepository doctorRepository, TimeSlotRepository timeSlotRepository) {
        this.doctorRepository = doctorRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<TimeSlot> execute(Long doctorId, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.plusDays(1).atStartOfDay();
        return timeSlotRepository.findAvailableByDoctorAndRange(doctorId, from, to, page, size);
    }

    public long count(Long doctorId, LocalDate dateFrom, LocalDate dateTo) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.plusDays(1).atStartOfDay();
        return timeSlotRepository.countAvailableByDoctorAndRange(doctorId, from, to);
    }
}
