package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.availability.AvailabilityBlockRequest;
import com.citamedica.backend.adapter.in.dto.availability.AvailabilityBlockResponse;
import com.citamedica.backend.application.usecase.CreateAvailabilityBlockUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/availability-blocks")
public class AvailabilityBlockController {

    private final CreateAvailabilityBlockUseCase createAvailabilityBlockUseCase;

    public AvailabilityBlockController(CreateAvailabilityBlockUseCase createAvailabilityBlockUseCase) {
        this.createAvailabilityBlockUseCase = createAvailabilityBlockUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityBlockResponse>> list(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                createAvailabilityBlockUseCase.list(doctorId).stream()
                        .map(AvailabilityBlockResponse::from)
                        .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AvailabilityBlockResponse> create(
            @PathVariable Long doctorId,
            @Valid @RequestBody AvailabilityBlockRequest request) {
        var saved = createAvailabilityBlockUseCase.execute(
                doctorId,
                request.getStartTime(),
                request.getEndTime(),
                request.getBlockType(),
                request.getRecurrenceRule());
        return ResponseEntity.status(HttpStatus.CREATED).body(AvailabilityBlockResponse.from(saved));
    }

    @DeleteMapping("/{blockId}")
    public ResponseEntity<Void> delete(@PathVariable Long doctorId, @PathVariable Long blockId) {
        createAvailabilityBlockUseCase.softDelete(doctorId, blockId);
        return ResponseEntity.noContent().build();
    }
}
