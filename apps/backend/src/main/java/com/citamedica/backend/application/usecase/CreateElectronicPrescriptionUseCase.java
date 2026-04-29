package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.ElectronicPrescription;
import com.citamedica.backend.domain.model.ElectronicPrescriptionLine;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateElectronicPrescriptionUseCase {

    private final ElectronicPrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public CreateElectronicPrescriptionUseCase(
            ElectronicPrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse execute(
            Long patientId,
            ElectronicPrescriptionDtos.CreateElectronicPrescriptionRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        Doctor prescriber = doctorRepository.findById(request.prescriberDoctorId())
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + request.prescriberDoctorId()));
        if (Boolean.FALSE.equals(prescriber.getActive())) {
            throw new InvalidDomainOperationException("Cannot prescribe with an inactive doctor");
        }

        Appointment appointment = null;
        if (request.appointmentId() != null) {
            appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + request.appointmentId()));
            if (appointment.getPatient() == null || !appointment.getPatient().getId().equals(patientId)) {
                throw new InvalidDomainOperationException("Appointment does not belong to this patient");
            }
            if (appointment.getDoctor() == null || !appointment.getDoctor().getId().equals(prescriber.getId())) {
                throw new InvalidDomainOperationException("Prescriber must match the appointment doctor");
            }
        }

        ElectronicPrescription rx = new ElectronicPrescription();
        rx.setPatient(patient);
        rx.setPrescriber(prescriber);
        rx.setAppointment(appointment);
        rx.setValidUntil(request.validUntil());
        rx.setNotes(request.notes());

        List<ElectronicPrescriptionLine> lines = new ArrayList<>();
        int order = 0;
        for (ElectronicPrescriptionDtos.LineRequest lineReq : request.lines()) {
            ElectronicPrescriptionLine line = new ElectronicPrescriptionLine();
            line.setMedicationName(lineReq.medicationName().trim());
            line.setDosage(lineReq.dosage() != null ? lineReq.dosage().trim() : null);
            line.setFrequency(lineReq.frequency() != null ? lineReq.frequency().trim() : null);
            line.setDurationDays(lineReq.durationDays());
            line.setRoute(lineReq.route() != null ? lineReq.route().trim() : null);
            line.setInstructions(lineReq.instructions() != null ? lineReq.instructions().trim() : null);
            line.setSortOrder(order++);
            line.setPrescription(rx);
            lines.add(line);
        }
        rx.setLines(lines);

        ElectronicPrescription saved = prescriptionRepository.save(rx);
        return ElectronicPrescriptionDtos.ElectronicPrescriptionResponse.from(
                prescriptionRepository.findDetailById(saved.getId()).orElse(saved));
    }
}
