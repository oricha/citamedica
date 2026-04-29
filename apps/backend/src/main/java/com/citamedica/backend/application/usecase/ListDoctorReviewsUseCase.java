package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.review.ReviewDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.PatientReviewJpaRepository;
import com.citamedica.backend.domain.model.PatientReview;
import com.citamedica.backend.domain.model.PatientReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListDoctorReviewsUseCase {

    private final PatientReviewJpaRepository patientReviewJpaRepository;

    public ListDoctorReviewsUseCase(PatientReviewJpaRepository patientReviewJpaRepository) {
        this.patientReviewJpaRepository = patientReviewJpaRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReviewDtos.ReviewResponse> execute(Long doctorId, Pageable pageable) {
        return patientReviewJpaRepository
                .findByDoctor_IdAndStatusOrderByCreatedAtDesc(doctorId, PatientReviewStatus.PUBLISHED, pageable)
                .map(ListDoctorReviewsUseCase::toResponse);
    }

    private static ReviewDtos.ReviewResponse toResponse(PatientReview r) {
        return new ReviewDtos.ReviewResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getDoctor().getId(),
                r.getAppointment().getId(),
                r.getRating(),
                r.getTitle(),
                r.getComment(),
                r.getCreatedAt());
    }
}
