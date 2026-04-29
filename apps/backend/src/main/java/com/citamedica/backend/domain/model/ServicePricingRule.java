package com.citamedica.backend.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinic_service_pricing_rule")
public class ServicePricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_service_id", nullable = false)
    private ClinicOffering clinicService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private MedicalSpecialty specialty;

    @Column(name = "override_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal overridePrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Clinic getClinic() { return clinic; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }
    public ClinicOffering getClinicService() { return clinicService; }
    public void setClinicService(ClinicOffering clinicService) { this.clinicService = clinicService; }
    public MedicalSpecialty getSpecialty() { return specialty; }
    public void setSpecialty(MedicalSpecialty specialty) { this.specialty = specialty; }
    public BigDecimal getOverridePrice() { return overridePrice; }
    public void setOverridePrice(BigDecimal overridePrice) { this.overridePrice = overridePrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
