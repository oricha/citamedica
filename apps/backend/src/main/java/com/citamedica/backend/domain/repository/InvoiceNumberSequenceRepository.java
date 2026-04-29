package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.InvoiceNumberSequence;
import com.citamedica.backend.domain.model.InvoiceNumberSequenceId;

import java.util.Optional;

public interface InvoiceNumberSequenceRepository {

    Optional<InvoiceNumberSequence> findById(InvoiceNumberSequenceId id);

    /**
     * Atomically increments and returns the new sequence value for the clinic calendar year.
     */
    int allocateNext(Long clinicId, int year);

    InvoiceNumberSequence save(InvoiceNumberSequence entity);

    void deleteAll();
}
