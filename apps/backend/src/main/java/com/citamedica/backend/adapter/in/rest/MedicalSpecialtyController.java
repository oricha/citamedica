package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.catalog.MedicalSpecialtyResponse;
import com.citamedica.backend.application.usecase.ListMedicalSpecialtiesUseCase;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/specialties")
public class MedicalSpecialtyController {

    private final ListMedicalSpecialtiesUseCase listMedicalSpecialtiesUseCase;

    public MedicalSpecialtyController(ListMedicalSpecialtiesUseCase listMedicalSpecialtiesUseCase) {
        this.listMedicalSpecialtiesUseCase = listMedicalSpecialtiesUseCase;
    }

    @GetMapping
    public ResponseEntity<List<MedicalSpecialtyResponse>> list() {
        List<MedicalSpecialty> list = listMedicalSpecialtiesUseCase.execute();
        return ResponseEntity.ok(list.stream().map(MedicalSpecialtyResponse::from).collect(Collectors.toList()));
    }
}
