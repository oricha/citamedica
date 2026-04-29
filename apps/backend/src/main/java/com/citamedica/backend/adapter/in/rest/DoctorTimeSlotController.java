package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.availability.TimeSlotResponse;
import com.citamedica.backend.application.usecase.QueryAvailableSlotsUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/available-slots")
public class DoctorTimeSlotController {

    private final QueryAvailableSlotsUseCase queryAvailableSlotsUseCase;

    public DoctorTimeSlotController(QueryAvailableSlotsUseCase queryAvailableSlotsUseCase) {
        this.queryAvailableSlotsUseCase = queryAvailableSlotsUseCase;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> query(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<TimeSlotResponse> items = queryAvailableSlotsUseCase.execute(doctorId, dateFrom, dateTo, page, size)
                .stream()
                .map(TimeSlotResponse::from)
                .collect(Collectors.toList());
        long total = queryAvailableSlotsUseCase.count(doctorId, dateFrom, dateTo);
        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("total", total);
        body.put("page", page);
        body.put("size", size);
        return ResponseEntity.ok(body);
    }
}
