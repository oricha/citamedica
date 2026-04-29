package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.review.ReviewDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.PatientReviewJpaRepository;
import com.citamedica.backend.domain.model.PatientReview;
import com.citamedica.backend.domain.model.PatientReviewStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDoctorRatingSummaryUseCase {

    private final PatientReviewJpaRepository patientReviewJpaRepository;

    public GetDoctorRatingSummaryUseCase(PatientReviewJpaRepository patientReviewJpaRepository) {
        this.patientReviewJpaRepository = patientReviewJpaRepository;
    }

    @Transactional(readOnly = true)
    public ReviewDtos.DoctorRatingSummaryResponse execute(Long doctorId) {
        long count = patientReviewJpaRepository.countByDoctorAndStatus(doctorId, PatientReviewStatus.PUBLISHED);
        Double avg = patientReviewJpaRepository.averageRatingByDoctor(doctorId, PatientReviewStatus.PUBLISHED);
        double average = avg != null ? roundOneDecimal(avg) : 0.0;
        return new ReviewDtos.DoctorRatingSummaryResponse(count, average);
    }

    private static double roundOneDecimal(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
