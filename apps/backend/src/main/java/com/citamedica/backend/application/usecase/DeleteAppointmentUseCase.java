package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SendAppointmentChangeNotificationUseCase sendAppointmentChangeNotificationUseCase;

    public DeleteAppointmentUseCase(AppointmentRepository appointmentRepository,
                                    TimeSlotRepository timeSlotRepository,
                                    SendAppointmentChangeNotificationUseCase sendAppointmentChangeNotificationUseCase) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.sendAppointmentChangeNotificationUseCase = sendAppointmentChangeNotificationUseCase;
    }

    @Transactional
    public void execute(Long id) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + id));

        timeSlotRepository.findByAppointmentId(id).ifPresent(slot -> {
            slot.markAvailable();
            timeSlotRepository.save(slot);
        });

        appointment.setStatus(AppointmentStatus.CANCELED);
        sendAppointmentChangeNotificationUseCase.execute(appointment);
        appointmentRepository.deleteById(id);
    }
}
