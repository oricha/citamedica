package com.citamedica.backend.domain.model.medical;

import com.citamedica.backend.domain.model.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_history_audit_log", indexes = {
        @Index(name = "idx_medical_history_audit_patient_created", columnList = "patient_id,created_at"),
        @Index(name = "idx_medical_history_audit_record", columnList = "record_type,record_id"),
        @Index(name = "idx_medical_history_audit_action_created", columnList = "action,created_at")
})
public class MedicalHistoryAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "record_type", nullable = false, length = 64)
    private String recordType;

    @Column(name = "record_id")
    private Long recordId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MedicalHistoryAuditAction action;

    @Column(name = "changed_data", columnDefinition = "TEXT")
    private String changedData;

    @Column(name = "actor_id", length = 255)
    private String actorId;

    @Column(name = "actor_ip", length = 64)
    private String actorIp;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public MedicalHistoryAuditAction getAction() {
        return action;
    }

    public void setAction(MedicalHistoryAuditAction action) {
        this.action = action;
    }

    public String getChangedData() {
        return changedData;
    }

    public void setChangedData(String changedData) {
        this.changedData = changedData;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActorIp() {
        return actorIp;
    }

    public void setActorIp(String actorIp) {
        this.actorIp = actorIp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
