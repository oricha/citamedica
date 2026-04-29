package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.review.ReviewDtos;
import com.citamedica.backend.application.usecase.GetDoctorRatingSummaryUseCase;
import com.citamedica.backend.application.usecase.ListDoctorReviewsUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}")
public class DoctorReviewController {

    private final ListDoctorReviewsUseCase listDoctorReviewsUseCase;
    private final GetDoctorRatingSummaryUseCase getDoctorRatingSummaryUseCase;

    public DoctorReviewController(
            ListDoctorReviewsUseCase listDoctorReviewsUseCase,
            GetDoctorRatingSummaryUseCase getDoctorRatingSummaryUseCase) {
        this.listDoctorReviewsUseCase = listDoctorReviewsUseCase;
        this.getDoctorRatingSummaryUseCase = getDoctorRatingSummaryUseCase;
    }

    @GetMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReviewDtos.ReviewResponse>> listReviews(
            @PathVariable Long doctorId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(listDoctorReviewsUseCase.execute(doctorId, pageable));
    }

    @GetMapping("/rating-summary")
    @PreAuthorize("isAuthenticated()")
    public ReviewDtos.DoctorRatingSummaryResponse ratingSummary(@PathVariable Long doctorId) {
        return getDoctorRatingSummaryUseCase.execute(doctorId);
    }
}
