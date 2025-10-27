package com.citamedica.backend.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consent")
public class Consent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentType type;

    @Column(nullable = false)
    private Boolean accepted;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    // Constructors, getters, setters
    public Consent() {}

    public Consent(Patient patient, ConsentType type, Boolean accepted) {
        this.patient = patient;
        this.type = type;
        this.accepted = accepted;
        if (accepted) {
            this.acceptedAt = LocalDateTime.now();
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public ConsentType getType() { return type; }
    public void setType(ConsentType type) { this.type = type; }
    public Boolean getAccepted() { return accepted; }
    public void setAccepted(Boolean accepted) { this.accepted = accepted; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
}