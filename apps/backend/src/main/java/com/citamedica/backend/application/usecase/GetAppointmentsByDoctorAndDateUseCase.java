package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetAppointmentsByDoctorAndDateUseCase {

    private final AppointmentRepository appointmentRepository;

    public GetAppointmentsByDoctorAndDateUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Appointment> execute(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }
}
