package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.DoctorResponse;
import com.citamedica.backend.application.usecase.GetActiveDoctorsUseCase;
import com.citamedica.backend.application.usecase.GetDoctorByIdUseCase;
import com.citamedica.backend.application.usecase.GetDoctorsByClinicUseCase;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final GetDoctorsByClinicUseCase getDoctorsByClinicUseCase;
    private final GetActiveDoctorsUseCase getActiveDoctorsUseCase;
    private final GetDoctorByIdUseCase getDoctorByIdUseCase;
    private final DoctorRepository doctorRepository;

    public DoctorController(
            GetDoctorsByClinicUseCase getDoctorsByClinicUseCase,
            GetActiveDoctorsUseCase getActiveDoctorsUseCase,
            GetDoctorByIdUseCase getDoctorByIdUseCase,
            DoctorRepository doctorRepository) {
        this.getDoctorsByClinicUseCase = getDoctorsByClinicUseCase;
        this.getActiveDoctorsUseCase = getActiveDoctorsUseCase;
        this.getDoctorByIdUseCase = getDoctorByIdUseCase;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors(
            @RequestParam(required = false) Long clinic,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size) {
        List<Doctor> doctors;
        if (specialtyId != null || serviceId != null) {
            doctors = doctorRepository.searchActive(clinic, specialtyId, serviceId, page, size);
        } else if (clinic != null) {
            doctors = getDoctorsByClinicUseCase.execute(clinic);
        } else {
            doctors = getActiveDoctorsUseCase.execute();
        }

        List<DoctorResponse> response = doctors.stream()
                .map(DoctorResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable Long id) {
        Doctor doctor = getDoctorByIdUseCase.execute(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Doctor not found"));
        return ResponseEntity.ok(DoctorResponse.from(doctor));
    }
}
