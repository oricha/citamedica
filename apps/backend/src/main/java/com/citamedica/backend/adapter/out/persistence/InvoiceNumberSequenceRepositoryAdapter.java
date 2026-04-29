package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.InvoiceNumberSequenceJpaRepository;
import com.citamedica.backend.domain.model.InvoiceNumberSequence;
import com.citamedica.backend.domain.model.InvoiceNumberSequenceId;
import com.citamedica.backend.domain.repository.InvoiceNumberSequenceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class InvoiceNumberSequenceRepositoryAdapter implements InvoiceNumberSequenceRepository {

    private final InvoiceNumberSequenceJpaRepository jpa;

    public InvoiceNumberSequenceRepositoryAdapter(InvoiceNumberSequenceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<InvoiceNumberSequence> findById(InvoiceNumberSequenceId id) {
        return jpa.findById(id);
    }

    @Override
    @Transactional
    public int allocateNext(Long clinicId, int year) {
        InvoiceNumberSequenceId id = new InvoiceNumberSequenceId(clinicId, year);
        Optional<InvoiceNumberSequence> locked = jpa.findByIdForUpdate(id);
        if (locked.isPresent()) {
            InvoiceNumberSequence s = locked.get();
            s.setLastValue(s.getLastValue() + 1);
            jpa.save(s);
            return s.getLastValue();
        }
        InvoiceNumberSequence created = new InvoiceNumberSequence();
        created.setId(id);
        created.setLastValue(1);
        try {
            jpa.save(created);
            return 1;
        } catch (DataIntegrityViolationException ex) {
            InvoiceNumberSequence s = jpa.findByIdForUpdate(id)
                    .orElseThrow(() -> new IllegalStateException("Sequence missing after race", ex));
            s.setLastValue(s.getLastValue() + 1);
            jpa.save(s);
            return s.getLastValue();
        }
    }

    @Override
    @Transactional
    public InvoiceNumberSequence save(InvoiceNumberSequence entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
