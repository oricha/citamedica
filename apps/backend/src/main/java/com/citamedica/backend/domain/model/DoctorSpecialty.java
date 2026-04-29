package com.citamedica.backend.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_specialty", uniqueConstraints =
        @UniqueConstraint(name = "uq_doctor_specialty", columnNames = {"doctor_id", "specialty_id"}),
        indexes = {
                @Index(name = "idx_doctor_specialty_doctor", columnList = "doctor_id"),
                @Index(name = "idx_doctor_specialty_specialty", columnList = "specialty_id")
        })
public class DoctorSpecialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private MedicalSpecialty specialty;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "primary_specialty", nullable = false)
    private boolean primarySpecialty;

    @Column(name = "override_duration_minutes")
    private Integer overrideDurationMinutes;

    public void validateAssignment() {
        if (overrideDurationMinutes != null && (overrideDurationMinutes < 15 || overrideDurationMinutes > 120)) {
            throw new IllegalArgumentException("override_duration_minutes must be between 15 and 120 when set");
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public MedicalSpecialty getSpecialty() { return specialty; }
    public void setSpecialty(MedicalSpecialty specialty) { this.specialty = specialty; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public boolean isPrimarySpecialty() { return primarySpecialty; }
    public void setPrimarySpecialty(boolean primarySpecialty) { this.primarySpecialty = primarySpecialty; }
    public Integer getOverrideDurationMinutes() { return overrideDurationMinutes; }
    public void setOverrideDurationMinutes(Integer overrideDurationMinutes) { this.overrideDurationMinutes = overrideDurationMinutes; }
}
