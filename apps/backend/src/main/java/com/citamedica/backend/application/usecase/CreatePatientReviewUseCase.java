package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.review.ReviewDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.PatientReviewJpaRepository;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.PatientReview;
import com.citamedica.backend.domain.model.PatientReviewStatus;
import com.citamedica.backend.exception.domain.DuplicateEntityException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreatePatientReviewUseCase {

    private final AppointmentJpaRepository appointmentJpaRepository;
    private final PatientReviewJpaRepository patientReviewJpaRepository;

    public CreatePatientReviewUseCase(
            AppointmentJpaRepository appointmentJpaRepository,
            PatientReviewJpaRepository patientReviewJpaRepository) {
        this.appointmentJpaRepository = appointmentJpaRepository;
        this.patientReviewJpaRepository = patientReviewJpaRepository;
    }

    @Transactional
    public ReviewDtos.ReviewResponse execute(Long patientId, ReviewDtos.CreateReviewRequest request) {
        if (patientReviewJpaRepository.existsByAppointment_Id(request.appointmentId())) {
            throw new DuplicateEntityException("A review already exists for this appointment");
        }
        Appointment appointment = appointmentJpaRepository.findById(request.appointmentId())
                .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + request.appointmentId()));
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new EntityNotFoundDomainException("Appointment not found: " + request.appointmentId());
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new InvalidDomainOperationException("Only completed appointments can be reviewed");
        }

        PatientReview review = new PatientReview();
        review.setPatient(appointment.getPatient());
        review.setDoctor(appointment.getDoctor());
        review.setAppointment(appointment);
        review.setClinic(appointment.getClinic());
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setComment(request.comment());
        review.setStatus(PatientReviewStatus.PUBLISHED);
        review.setCreatedAt(LocalDateTime.now());

        PatientReview saved = patientReviewJpaRepository.save(review);
        return toResponse(saved);
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
