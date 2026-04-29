package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.InvoiceNumberSequence;
import com.citamedica.backend.domain.model.InvoiceNumberSequenceId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceNumberSequenceJpaRepository extends JpaRepository<InvoiceNumberSequence, InvoiceNumberSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceNumberSequence s WHERE s.id = :id")
    Optional<InvoiceNumberSequence> findByIdForUpdate(@Param("id") InvoiceNumberSequenceId id);
}
