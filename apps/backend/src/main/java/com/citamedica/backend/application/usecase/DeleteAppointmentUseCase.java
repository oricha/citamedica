package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    public DeleteAppointmentUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void execute(Long id) {
        if (appointmentRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundDomainException("Appointment not found: " + id);
        }
        appointmentRepository.deleteById(id);
    }
}
