package com.citamedica.backend.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_number_sequence")
public class InvoiceNumberSequence {

    @EmbeddedId
    private InvoiceNumberSequenceId id;

    @Column(name = "last_value", nullable = false)
    private int lastValue = 0;

    public InvoiceNumberSequenceId getId() {
        return id;
    }

    public void setId(InvoiceNumberSequenceId id) {
        this.id = id;
    }

    public int getLastValue() {
        return lastValue;
    }

    public void setLastValue(int lastValue) {
        this.lastValue = lastValue;
    }
}
