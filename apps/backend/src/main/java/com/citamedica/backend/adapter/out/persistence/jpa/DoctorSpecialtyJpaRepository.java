package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.DoctorSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorSpecialtyJpaRepository extends JpaRepository<DoctorSpecialty, Long> {

    List<DoctorSpecialty> findByDoctorIdOrderByPrimarySpecialtyDesc(Long doctorId);

    Optional<DoctorSpecialty> findByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    boolean existsByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    long countByDoctorIdAndPrimarySpecialtyTrue(Long doctorId);

    void deleteByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM DoctorSpecialty ds WHERE ds.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);
}
