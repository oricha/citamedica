package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.service.AppointmentDomainService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentDomainService appointmentDomainService;

    public CreateAppointmentUseCase(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentDomainService appointmentDomainService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentDomainService = appointmentDomainService;
    }

    @Transactional
    public Appointment execute(
            Long doctorId,
            Long patientId,
            String type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String calBookingId,
            String notes) {
        appointmentDomainService.validateTimes(startAt, endAt);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));

        Appointment appointment = new Appointment(doctor, patient, type, startAt, endAt);
        appointment.setClinic(doctor.getClinic());

        if (calBookingId != null && !calBookingId.isBlank()) {
            appointment.setCalBookingId(calBookingId);
        }
        if (notes != null) {
            appointment.setNotes(notes);
        }

        return appointmentRepository.save(appointment);
    }
}
