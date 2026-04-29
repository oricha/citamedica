package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.model.InvoiceLineItem;
import com.citamedica.backend.domain.model.InvoiceStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.InvoiceNumberSequenceRepository;
import com.citamedica.backend.domain.repository.InvoiceRepository;
import com.citamedica.backend.domain.service.InvoiceNumberFormatter;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import com.citamedica.backend.exception.domain.InvoiceGenerationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreateInvoiceForAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceNumberSequenceRepository invoiceNumberSequenceRepository;
    private final ClinicRepository clinicRepository;

    public CreateInvoiceForAppointmentUseCase(
            AppointmentRepository appointmentRepository,
            InvoiceRepository invoiceRepository,
            InvoiceNumberSequenceRepository invoiceNumberSequenceRepository,
            ClinicRepository clinicRepository) {
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceNumberSequenceRepository = invoiceNumberSequenceRepository;
        this.clinicRepository = clinicRepository;
    }

    @Transactional
    public Invoice execute(Long appointmentId) {
        Optional<Invoice> existing = invoiceRepository.findByAppointmentId(appointmentId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + appointmentId));
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new InvalidDomainOperationException("Invoice can only be created for COMPLETED appointments");
        }
        if (appointment.getClinic() == null) {
            throw new InvoiceGenerationException("Appointment has no clinic");
        }

        int year = LocalDate.now().getYear();
        int seq = invoiceNumberSequenceRepository.allocateNext(appointment.getClinic().getId(), year);
        var clinic = clinicRepository.findById(appointment.getClinic().getId())
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found"));
        String number = InvoiceNumberFormatter.format(clinic.getSlug(), year, seq);

        BigDecimal total = appointment.getTotalAmount() != null ? appointment.getTotalAmount() : BigDecimal.ZERO;
        String lineDesc = appointment.getType();
        ClinicOffering offering = appointment.getClinicOffering();
        if (offering != null) {
            lineDesc = offering.getName();
        }

        Invoice invoice = new Invoice();
        invoice.setClinic(appointment.getClinic());
        invoice.setPatient(appointment.getPatient());
        invoice.setAppointment(appointment);
        invoice.setInvoiceNumber(number);
        invoice.setAmount(total);
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setCreatedAt(LocalDateTime.now());

        InvoiceLineItem line = new InvoiceLineItem();
        line.setDescription(lineDesc != null ? lineDesc : "Consultation");
        line.setQuantity(1);
        line.setUnitPrice(total);
        line.setAmount(total);
        line.validateLine();
        invoice.addLineItem(line);
        invoice.validateTotals();

        return invoiceRepository.save(invoice);
    }
}
