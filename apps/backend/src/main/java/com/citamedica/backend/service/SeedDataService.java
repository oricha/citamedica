package com.citamedica.backend.service;

import com.citamedica.backend.domain.model.*;
import com.citamedica.backend.domain.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!test")
public class SeedDataService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SeedDataService.class);

    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public SeedDataService(
            ClinicRepository clinicRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository) {
        this.clinicRepository = clinicRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting seed data process...");
        
        // Check if seed data already exists
        if (clinicRepository.findBySlug("clinica-demo").isPresent()) {
            logger.info("Seed data already exists. Skipping seed process.");
            return;
        }

        try {
            // Create clinic
            Clinic clinic = createClinic();
            logger.info("Created clinic: {} (ID: {})", clinic.getName(), clinic.getId());

            // Create doctors
            List<Doctor> doctors = createDoctors(clinic);
            logger.info("Created {} doctors", doctors.size());

            // Create patients
            List<Patient> patients = createPatients();
            logger.info("Created {} patients", patients.size());

            // Create appointments
            List<Appointment> appointments = createAppointments(clinic, doctors, patients);
            logger.info("Created {} appointments", appointments.size());

            logger.info("Seed data process completed successfully!");
            logger.info("Summary: 1 clinic, {} doctors, {} patients, {} appointments", 
                    doctors.size(), patients.size(), appointments.size());

        } catch (Exception e) {
            logger.error("Error during seed data process", e);
            throw new RuntimeException("Failed to seed data", e);
        }
    }

    private Clinic createClinic() {
        Clinic clinic = new Clinic("clinica-demo", "Clínica Demo CitaMedica");
        clinic.setAddress("Calle Principal 123, Madrid, 28001");
        clinic.setPhone("+34 912 345 678");
        clinic.setCalTeamId("demo-team-id");
        return clinicRepository.save(clinic);
    }

    private List<Doctor> createDoctors(Clinic clinic) {
        List<Doctor> doctors = new ArrayList<>();

        // Doctor 1: Cardiologist
        Doctor doctor1 = new Doctor(clinic, "Dr. María García López", "Cardiología", "maria.garcia@clinicademo.com");
        doctor1.setPhone("+34 612 345 678");
        doctor1.setCalUsername("dr-maria-garcia");
        doctor1.setActive(true);
        doctors.add(doctorRepository.save(doctor1));

        // Doctor 2: General Practitioner
        Doctor doctor2 = new Doctor(clinic, "Dr. Juan Martínez Ruiz", "Medicina General", "juan.martinez@clinicademo.com");
        doctor2.setPhone("+34 623 456 789");
        doctor2.setCalUsername("dr-juan-martinez");
        doctor2.setActive(true);
        doctors.add(doctorRepository.save(doctor2));

        return doctors;
    }

    private List<Patient> createPatients() {
        List<Patient> patients = new ArrayList<>();

        // Patient 1
        Patient patient1 = new Patient("Ana Rodríguez Sánchez", "ana.rodriguez@email.com", "+34 634 567 890");
        patient1.setBirthDate(LocalDate.of(1985, 3, 15));
        patient1.setInsurancePlan("Sanitas Básico");
        patients.add(patientRepository.save(patient1));

        // Patient 2
        Patient patient2 = new Patient("Carlos Fernández López", "carlos.fernandez@email.com", "+34 645 678 901");
        patient2.setBirthDate(LocalDate.of(1978, 7, 22));
        patient2.setInsurancePlan("Adeslas Premium");
        patients.add(patientRepository.save(patient2));

        // Patient 3
        Patient patient3 = new Patient("Laura Gómez Martín", "laura.gomez@email.com", "+34 656 789 012");
        patient3.setBirthDate(LocalDate.of(1992, 11, 8));
        patient3.setInsurancePlan("DKV Salud");
        patients.add(patientRepository.save(patient3));

        return patients;
    }

    private List<Appointment> createAppointments(Clinic clinic, List<Doctor> doctors, List<Patient> patients) {
        List<Appointment> appointments = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Appointment 1: Past appointment (completed)
        Appointment apt1 = new Appointment(
                doctors.get(0), 
                patients.get(0), 
                "Consulta de Cardiología",
                now.minusDays(7).withHour(10).withMinute(0).withSecond(0).withNano(0),
                now.minusDays(7).withHour(10).withMinute(30).withSecond(0).withNano(0)
        );
        apt1.setClinic(clinic);
        apt1.setStatus(AppointmentStatus.COMPLETED);
        apt1.setNotes("Revisión rutinaria. Paciente en buen estado de salud.");
        appointments.add(appointmentRepository.save(apt1));

        // Appointment 2: Today's appointment (scheduled)
        Appointment apt2 = new Appointment(
                doctors.get(1), 
                patients.get(1), 
                "Consulta General",
                now.withHour(15).withMinute(0).withSecond(0).withNano(0),
                now.withHour(15).withMinute(30).withSecond(0).withNano(0)
        );
        apt2.setClinic(clinic);
        apt2.setStatus(AppointmentStatus.SCHEDULED);
        apt2.setNotes("Primera consulta. Revisión general.");
        appointments.add(appointmentRepository.save(apt2));

        // Appointment 3: Future appointment (scheduled)
        Appointment apt3 = new Appointment(
                doctors.get(0), 
                patients.get(2), 
                "Electrocardiograma",
                now.plusDays(3).withHour(11).withMinute(30).withSecond(0).withNano(0),
                now.plusDays(3).withHour(12).withMinute(0).withSecond(0).withNano(0)
        );
        apt3.setClinic(clinic);
        apt3.setStatus(AppointmentStatus.SCHEDULED);
        apt3.setNotes("Electrocardiograma de control.");
        appointments.add(appointmentRepository.save(apt3));

        // Appointment 4: Future appointment (scheduled)
        Appointment apt4 = new Appointment(
                doctors.get(1), 
                patients.get(0), 
                "Seguimiento",
                now.plusDays(14).withHour(9).withMinute(0).withSecond(0).withNano(0),
                now.plusDays(14).withHour(9).withMinute(30).withSecond(0).withNano(0)
        );
        apt4.setClinic(clinic);
        apt4.setStatus(AppointmentStatus.SCHEDULED);
        apt4.setNotes("Seguimiento de tratamiento.");
        appointments.add(appointmentRepository.save(apt4));

        return appointments;
    }
}