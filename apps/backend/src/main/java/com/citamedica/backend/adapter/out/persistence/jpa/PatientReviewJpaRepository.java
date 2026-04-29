package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.PatientReview;
import com.citamedica.backend.domain.model.PatientReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientReviewJpaRepository extends JpaRepository<PatientReview, Long> {

    boolean existsByAppointment_Id(Long appointmentId);

    Page<PatientReview> findByDoctor_IdAndStatusOrderByCreatedAtDesc(
            Long doctorId,
            PatientReviewStatus status,
            Pageable pageable);

    Page<PatientReview> findByPatient_IdOrderByCreatedAtDesc(Long patientId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM PatientReview r WHERE r.doctor.id = :doctorId AND r.status = :status")
    Double averageRatingByDoctor(@Param("doctorId") Long doctorId, @Param("status") PatientReviewStatus status);

    @Query("SELECT COUNT(r) FROM PatientReview r WHERE r.doctor.id = :doctorId AND r.status = :status")
    long countByDoctorAndStatus(@Param("doctorId") Long doctorId, @Param("status") PatientReviewStatus status);
}
