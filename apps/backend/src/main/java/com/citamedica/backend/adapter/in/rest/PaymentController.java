package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.billing.PaymentResponse;
import com.citamedica.backend.adapter.in.dto.billing.ProcessPaymentRequest;
import com.citamedica.backend.application.usecase.ProcessPaymentUseCase;
import com.citamedica.backend.domain.repository.PaymentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentRepository paymentRepository;

    public PaymentController(ProcessPaymentUseCase processPaymentUseCase, PaymentRepository paymentRepository) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.paymentRepository = paymentRepository;
    }

    @PreAuthorize("hasAnyRole('STAFF','CLINIC_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        var payment = processPaymentUseCase.execute(
                request.getPatientId(),
                request.getAppointmentId(),
                request.getInvoiceId(),
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentProvider(),
                request.getProviderPaymentToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(payment));
    }

    @PreAuthorize("hasAnyRole('STAFF','CLINIC_MANAGER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(PaymentResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment not found"));
    }
}
