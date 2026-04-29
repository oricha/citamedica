package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorJpaRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByClinicId(Long clinicId);

    List<Doctor> findByActiveTrue();

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByCalUsername(String calUsername);

    @Query("""
            SELECT DISTINCT d FROM Doctor d
            WHERE d.active = true
            AND (:clinicId IS NULL OR d.clinic.id = :clinicId)
            AND (:specialtyId IS NULL OR EXISTS (
                SELECT 1 FROM DoctorSpecialty ds WHERE ds.doctor = d AND ds.specialty.id = :specialtyId))
            AND (:serviceOfferingId IS NULL OR EXISTS (
                SELECT 1 FROM ClinicOffering cs WHERE cs.id = :serviceOfferingId AND cs.active = true
                AND cs.clinic = d.clinic
                AND (cs.minRequiredSpecialty IS NULL OR EXISTS (
                    SELECT 1 FROM DoctorSpecialty ds2 WHERE ds2.doctor = d
                    AND ds2.specialty.id = cs.minRequiredSpecialty.id))))
            """)
    Page<Doctor> searchActiveDoctors(
            @Param("clinicId") Long clinicId,
            @Param("specialtyId") Long specialtyId,
            @Param("serviceOfferingId") Long serviceOfferingId,
            Pageable pageable);
}
