package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.service.AppointmentDomainService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDomainService appointmentDomainService;

    public UpdateAppointmentUseCase(
            AppointmentRepository appointmentRepository,
            AppointmentDomainService appointmentDomainService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDomainService = appointmentDomainService;
    }

    @Transactional
    public Appointment execute(
            Long id,
            String type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String notes,
            AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + id));

        LocalDateTime effectiveStart = startAt != null ? startAt : appointment.getStartAt();
        LocalDateTime effectiveEnd = endAt != null ? endAt : appointment.getEndAt();
        appointmentDomainService.validateTimes(effectiveStart, effectiveEnd);

        if (type != null) {
            appointment.setType(type);
        }
        if (startAt != null) {
            appointment.setStartAt(startAt);
        }
        if (endAt != null) {
            appointment.setEndAt(endAt);
        }
        if (notes != null) {
            appointment.setNotes(notes);
        }
        if (status != null) {
            appointment.setStatus(status);
        }
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }
}
