package com.citamedica.backend.adapter.in.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class ReviewDtos {
    private ReviewDtos() {}

    public record CreateReviewRequest(
            @NotNull Long appointmentId,
            @Min(1) @Max(5) int rating,
            @Size(max = 255) String title,
            @Size(max = 4000) String comment
    ) {}

    public record ReviewResponse(
            Long id,
            Long patientId,
            Long doctorId,
            Long appointmentId,
            int rating,
            String title,
            String comment,
            LocalDateTime createdAt
    ) {}

    public record DoctorRatingSummaryResponse(
            long reviewCount,
            double averageRating
    ) {}
}
