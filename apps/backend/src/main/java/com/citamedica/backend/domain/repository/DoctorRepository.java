package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Optional<Doctor> findById(Long id);

    List<Doctor> findByClinicId(Long clinicId);

    List<Doctor> findByActiveTrue();

    /**
     * Filter active doctors by clinic, medical specialty, and/or clinic offering (service) qualification.
     */
    List<Doctor> searchActive(Long clinicId, Long specialtyId, Long serviceOfferingId, int page, int size);

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByCalUsername(String calUsername);

    Doctor save(Doctor entity);

    void deleteById(Long id);

    void deleteAll();
}
