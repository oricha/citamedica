package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Consent;
import com.citamedica.backend.domain.model.ConsentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentJpaRepository extends JpaRepository<Consent, Long> {
    List<Consent> findByPatientId(Long patientId);

    List<Consent> findByPatientIdAndType(Long patientId, ConsentType type);
}
