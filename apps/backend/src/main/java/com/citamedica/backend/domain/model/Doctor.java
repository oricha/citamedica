package com.citamedica.backend.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(name = "cal_username")
    private String calUsername;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, length = 64)
    private String timezone = "America/Sao_Paulo";

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorAvailabilityConfiguration> availabilityConfigurations = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorAvailabilityBlock> availabilityBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorSpecialty> doctorSpecialties = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    public Doctor() {}

    public Doctor(Clinic clinic, String fullName, String specialty, String email) {
        this.clinic = clinic;
        this.fullName = fullName;
        this.specialty = specialty;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Clinic getClinic() { return clinic; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCalUsername() { return calUsername; }
    public void setCalUsername(String calUsername) { this.calUsername = calUsername; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public List<DoctorAvailabilityConfiguration> getAvailabilityConfigurations() { return availabilityConfigurations; }

    public List<DoctorAvailabilityBlock> getAvailabilityBlocks() { return availabilityBlocks; }

    public void addAvailabilityConfiguration(DoctorAvailabilityConfiguration configuration) {
        availabilityConfigurations.add(configuration);
        configuration.setDoctor(this);
    }

    public void addAvailabilityBlock(DoctorAvailabilityBlock block) {
        availabilityBlocks.add(block);
        block.setDoctor(this);
    }

    public List<DoctorSpecialty> getDoctorSpecialties() { return doctorSpecialties; }

    public void addDoctorSpecialty(DoctorSpecialty ds) {
        doctorSpecialties.add(ds);
        ds.setDoctor(this);
    }

    public Optional<DoctorAvailabilityConfiguration> getAvailabilityConfig(ScheduleDayOfWeek day) {
        return availabilityConfigurations.stream()
                .filter(c -> c.getDayOfWeek() == day)
                .findFirst();
    }

    /**
     * Lightweight check: whether the doctor has configuration covering the local time's day and hour window.
     */
    public boolean isAvailable(LocalDateTime localDateTime) {
        ScheduleDayOfWeek day = ScheduleDayOfWeek.fromJava(localDateTime.getDayOfWeek());
        return getAvailabilityConfig(day)
                .filter(c -> {
                    LocalTime t = localDateTime.toLocalTime();
                    return !t.isBefore(c.getStartTime()) && t.isBefore(c.getEndTime());
                })
                .isPresent();
    }
}