package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.billing.CreateRefundRequest;
import com.citamedica.backend.adapter.in.dto.billing.RefundResponse;
import com.citamedica.backend.adapter.in.dto.billing.ResolveRefundRequest;
import com.citamedica.backend.application.usecase.RequestRefundUseCase;
import com.citamedica.backend.application.usecase.ResolveRefundUseCase;
import com.citamedica.backend.domain.repository.RefundRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RequestRefundUseCase requestRefundUseCase;
    private final ResolveRefundUseCase resolveRefundUseCase;
    private final RefundRepository refundRepository;

    public RefundController(
            RequestRefundUseCase requestRefundUseCase,
            ResolveRefundUseCase resolveRefundUseCase,
            RefundRepository refundRepository) {
        this.requestRefundUseCase = requestRefundUseCase;
        this.resolveRefundUseCase = resolveRefundUseCase;
        this.refundRepository = refundRepository;
    }

    @PreAuthorize("hasAnyRole('STAFF','CLINIC_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<RefundResponse> requestRefund(@Valid @RequestBody CreateRefundRequest request) {
        var refund = requestRefundUseCase.execute(
                request.getPaymentId(),
                request.getAmount(),
                request.getReason(),
                request.getNotes());
        return ResponseEntity.status(HttpStatus.CREATED).body(RefundResponse.from(refund));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<RefundResponse> resolve(
            @PathVariable Long id,
            @Valid @RequestBody ResolveRefundRequest body) {
        if (body.getApprove() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "approve is required");
        }
        var refund = resolveRefundUseCase.execute(id, body.getApprove(), body.getNotes());
        return ResponseEntity.ok(RefundResponse.from(refund));
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RefundResponse> getRefund(@PathVariable Long id) {
        return refundRepository.findById(id)
                .map(RefundResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Refund not found"));
    }
}
