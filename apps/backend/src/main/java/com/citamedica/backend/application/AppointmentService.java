package com.citamedica.backend.application;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }

    public Appointment createAppointment(
            Long doctorId,
            Long patientId,
            String type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String calBookingId,
            String notes) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NoSuchElementException("Patient not found: " + patientId));
        Appointment appointment = new Appointment(doctor, patient, type, startAt, endAt);
        appointment.setClinic(doctor.getClinic());
        if (calBookingId != null && !calBookingId.isBlank()) {
            appointment.setCalBookingId(calBookingId);
        }
        if (notes != null) {
            appointment.setNotes(notes);
        }
        Appointment saved = appointmentRepository.save(appointment);
        if (saved.getDoctor() != null) {
            Hibernate.initialize(saved.getDoctor());
        }
        if (saved.getPatient() != null) {
            Hibernate.initialize(saved.getPatient());
        }
        if (saved.getClinic() != null) {
            Hibernate.initialize(saved.getClinic());
        }
        return saved;
    }

    public Appointment updateAppointment(
            Long id,
            String type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String notes,
            AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found: " + id));
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

    public void deleteAppointment(Long id) {
        if (appointmentRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Appointment not found: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findByCalBookingId(String calBookingId) {
        return appointmentRepository.findByCalBookingId(calBookingId);
    }
}