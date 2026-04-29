package com.citamedica.backend.application.usecase;

import com.citamedica.backend.config.AvailabilityProperties;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.DoctorAvailabilityBlockRepository;
import com.citamedica.backend.domain.repository.DoctorAvailabilityConfigurationRepository;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import com.citamedica.backend.domain.service.AppointmentAvailabilityValidator;
import com.citamedica.backend.domain.service.AppointmentDomainService;
import com.citamedica.backend.domain.service.AvailabilityConflictDetectionService;
import com.citamedica.backend.exception.domain.ConflictingAppointmentException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.SlotUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDomainService appointmentDomainService;
    private final SendAppointmentChangeNotificationUseCase sendAppointmentChangeNotificationUseCase;
    private final AvailabilityProperties availabilityProperties;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository;
    private final DoctorAvailabilityConfigurationRepository doctorAvailabilityConfigurationRepository;
    private final AppointmentAvailabilityValidator appointmentAvailabilityValidator;
    private final AvailabilityConflictDetectionService availabilityConflictDetectionService;

    public UpdateAppointmentUseCase(
            AppointmentRepository appointmentRepository,
            AppointmentDomainService appointmentDomainService,
            SendAppointmentChangeNotificationUseCase sendAppointmentChangeNotificationUseCase,
            AvailabilityProperties availabilityProperties,
            TimeSlotRepository timeSlotRepository,
            DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository,
            DoctorAvailabilityConfigurationRepository doctorAvailabilityConfigurationRepository,
            AppointmentAvailabilityValidator appointmentAvailabilityValidator,
            AvailabilityConflictDetectionService availabilityConflictDetectionService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDomainService = appointmentDomainService;
        this.sendAppointmentChangeNotificationUseCase = sendAppointmentChangeNotificationUseCase;
        this.availabilityProperties = availabilityProperties;
        this.timeSlotRepository = timeSlotRepository;
        this.doctorAvailabilityBlockRepository = doctorAvailabilityBlockRepository;
        this.doctorAvailabilityConfigurationRepository = doctorAvailabilityConfigurationRepository;
        this.appointmentAvailabilityValidator = appointmentAvailabilityValidator;
        this.availabilityConflictDetectionService = availabilityConflictDetectionService;
    }

    @Transactional
    public Appointment execute(
            Long id,
            String type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String notes,
            AppointmentStatus status,
            Long timeSlotId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + id));

        LocalDateTime effectiveStart = startAt != null ? startAt : appointment.getStartAt();
        LocalDateTime effectiveEnd = endAt != null ? endAt : appointment.getEndAt();
        appointmentDomainService.validateTimes(effectiveStart, effectiveEnd);

        boolean managedByCalCom = appointment.getCalBookingId() != null && !appointment.getCalBookingId().isBlank();
        boolean timeChanges = !Objects.equals(appointment.getStartAt(), effectiveStart)
                || !Objects.equals(appointment.getEndAt(), effectiveEnd);

        TimeSlot newLockedSlot = null;
        if (availabilityProperties.isEnforced() && !managedByCalCom && timeChanges) {
            releaseSlotIfPresent(appointment.getId());
            newLockedSlot = lockAndValidateNewSlot(
                    appointment.getDoctor().getId(),
                    effectiveStart,
                    effectiveEnd,
                    timeSlotId);
            long overlapping = appointmentRepository.countOverlappingScheduledExcluding(
                    appointment.getDoctor().getId(),
                    effectiveStart,
                    effectiveEnd,
                    appointment.getId());
            if (availabilityConflictDetectionService.hasConflictWithConcurrentLimit(
                    overlapping, resolveMaxConcurrent(appointment.getDoctor().getId(), effectiveStart))) {
                throw new ConflictingAppointmentException("Doctor concurrent appointment limit reached for this time window");
            }
        }

        if (type != null) {
            appointment.setType(type);
        }
        if (startAt != null) {
            appointment.setStartAt(startAt);
        }
        if (endAt != null) {
            appointment.setEndAt(endAt);
        }
        if (notes != null) {
            appointment.setNotes(notes);
        }
        if (status != null) {
            appointment.setStatus(status);
        }
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment saved = appointmentRepository.save(appointment);

        if (newLockedSlot != null) {
            newLockedSlot.markBooked(saved);
            timeSlotRepository.save(newLockedSlot);
        }

        sendAppointmentChangeNotificationUseCase.execute(saved);
        return saved;
    }

    private int resolveMaxConcurrent(Long doctorId, LocalDateTime startAt) {
        ScheduleDayOfWeek day = ScheduleDayOfWeek.fromJava(startAt.getDayOfWeek());
        var cfg = doctorAvailabilityConfigurationRepository.findByDoctorIdAndDay(doctorId, day).orElse(null);
        return appointmentAvailabilityValidator.resolveMaxConcurrent(cfg, 1);
    }

    private void releaseSlotIfPresent(Long appointmentId) {
        timeSlotRepository.findByAppointmentId(appointmentId).ifPresent(slot -> {
            slot.markAvailable();
            timeSlotRepository.save(slot);
        });
    }

    private TimeSlot lockAndValidateNewSlot(Long doctorId, LocalDateTime startAt, LocalDateTime endAt, Long timeSlotId) {
        TimeSlot slot = loadLockedSlot(doctorId, startAt, timeSlotId);
        appointmentAvailabilityValidator.validateSlotMatchesAppointment(slot, startAt, endAt);
        List<DoctorAvailabilityBlock> blocks = doctorAvailabilityBlockRepository.findActiveByDoctorId(doctorId);
        appointmentAvailabilityValidator.assertNotBlocked(startAt, endAt, blocks);
        return slot;
    }

    private TimeSlot loadLockedSlot(Long doctorId, LocalDateTime startAt, Long timeSlotId) {
        if (timeSlotId != null) {
            TimeSlot slot = timeSlotRepository.findByIdForUpdate(timeSlotId)
                    .orElseThrow(() -> new SlotUnavailableException("Time slot not found: " + timeSlotId));
            if (!slot.getDoctor().getId().equals(doctorId)) {
                throw new SlotUnavailableException("Time slot does not belong to selected doctor");
            }
            return slot;
        }
        return timeSlotRepository.findForUpdateByDoctorAndStartAndStatus(doctorId, startAt, SlotStatus.AVAILABLE)
                .orElseThrow(() -> new SlotUnavailableException("No available slot for requested start time"));
    }
}
