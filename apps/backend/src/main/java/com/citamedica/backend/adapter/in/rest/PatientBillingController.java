package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.billing.InvoiceResponse;
import com.citamedica.backend.adapter.in.dto.billing.PaymentResponse;
import com.citamedica.backend.adapter.in.dto.billing.OutstandingBalanceResponse;
import com.citamedica.backend.application.usecase.GetInvoicePdfUseCase;
import com.citamedica.backend.application.usecase.GetOutstandingBalanceUseCase;
import com.citamedica.backend.domain.repository.InvoiceRepository;
import com.citamedica.backend.domain.repository.PaymentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class PatientBillingController {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final GetOutstandingBalanceUseCase getOutstandingBalanceUseCase;
    private final GetInvoicePdfUseCase getInvoicePdfUseCase;

    public PatientBillingController(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            GetOutstandingBalanceUseCase getOutstandingBalanceUseCase,
            GetInvoicePdfUseCase getInvoicePdfUseCase) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.getOutstandingBalanceUseCase = getOutstandingBalanceUseCase;
        this.getInvoicePdfUseCase = getInvoicePdfUseCase;
    }

    @PreAuthorize("hasAnyRole('STAFF','DOCTOR','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/api/v1/patients/{patientId}/payments")
    public ResponseEntity<List<PaymentResponse>> listPayments(@PathVariable Long patientId) {
        return ResponseEntity.ok(
                paymentRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                        .map(PaymentResponse::from)
                        .collect(Collectors.toList()));
    }

    @PreAuthorize("hasAnyRole('STAFF','DOCTOR','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/api/v1/patients/{patientId}/invoices")
    public ResponseEntity<List<InvoiceResponse>> listInvoices(@PathVariable Long patientId) {
        return ResponseEntity.ok(
                invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                        .map(InvoiceResponse::from)
                        .collect(Collectors.toList()));
    }

    @PreAuthorize("hasAnyRole('STAFF','DOCTOR','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/api/v1/patients/{patientId}/balance")
    public ResponseEntity<OutstandingBalanceResponse> getBalance(@PathVariable Long patientId) {
        var balance = getOutstandingBalanceUseCase.execute(patientId);
        return ResponseEntity.ok(new OutstandingBalanceResponse(patientId, balance));
    }

    @PreAuthorize("hasAnyRole('STAFF','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/api/v1/invoices/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) {
        byte[] pdf = getInvoicePdfUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PreAuthorize("hasAnyRole('STAFF','DOCTOR','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/api/v1/invoices/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(InvoiceResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invoice not found"));
    }
}
