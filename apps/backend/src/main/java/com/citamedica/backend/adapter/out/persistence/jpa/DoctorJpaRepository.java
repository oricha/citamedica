package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorJpaRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByClinicId(Long clinicId);

    List<Doctor> findByActiveTrue();

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByCalUsername(String calUsername);
}
