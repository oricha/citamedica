package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.out.pdf.InvoicePdfRenderer;
import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.InvoiceRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetInvoicePdfUseCase {

    private final InvoiceRepository invoiceRepository;
    private final ClinicRepository clinicRepository;
    private final InvoicePdfRenderer invoicePdfRenderer;

    public GetInvoicePdfUseCase(
            InvoiceRepository invoiceRepository,
            ClinicRepository clinicRepository,
            InvoicePdfRenderer invoicePdfRenderer) {
        this.invoiceRepository = invoiceRepository;
        this.clinicRepository = clinicRepository;
        this.invoicePdfRenderer = invoicePdfRenderer;
    }

    @Transactional(readOnly = true)
    public byte[] execute(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Invoice not found: " + invoiceId));
        var clinic = clinicRepository.findById(invoice.getClinic().getId())
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found"));
        invoice.getPatient().getFullName();
        invoice.getLineItems().size();
        return invoicePdfRenderer.render(invoice, clinic);
    }
}
