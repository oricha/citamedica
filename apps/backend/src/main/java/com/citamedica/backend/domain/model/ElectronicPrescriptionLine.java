package com.citamedica.backend.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "electronic_prescription_line", indexes = {
        @Index(name = "idx_electronic_rx_line_rx", columnList = "prescription_id")
})
public class ElectronicPrescriptionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private ElectronicPrescription prescription;

    @Column(name = "medication_name", nullable = false, length = 512)
    private String medicationName;

    @Column(length = 255)
    private String dosage;

    @Column(length = 255)
    private String frequency;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(length = 128)
    private String route;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ElectronicPrescription getPrescription() {
        return prescription;
    }

    public void setPrescription(ElectronicPrescription prescription) {
        this.prescription = prescription;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
