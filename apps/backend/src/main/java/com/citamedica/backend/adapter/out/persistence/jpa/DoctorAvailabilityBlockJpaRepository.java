package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityBlockJpaRepository extends JpaRepository<DoctorAvailabilityBlock, Long> {

    List<DoctorAvailabilityBlock> findByDoctorIdAndDeletedAtIsNull(Long doctorId);

    Optional<DoctorAvailabilityBlock> findByIdAndDeletedAtIsNull(Long id);
}
