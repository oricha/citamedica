package com.citamedica.backend.application.usecase;

import com.citamedica.backend.config.AvailabilityProperties;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.model.DoctorSpecialty;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.domain.repository.DoctorAvailabilityBlockRepository;
import com.citamedica.backend.domain.repository.DoctorAvailabilityConfigurationRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.DoctorSpecialtyRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import com.citamedica.backend.domain.service.AppointmentAvailabilityValidator;
import com.citamedica.backend.domain.service.AppointmentDomainService;
import com.citamedica.backend.domain.service.AvailabilityConflictDetectionService;
import com.citamedica.backend.exception.domain.ConflictingAppointmentException;
import com.citamedica.backend.exception.domain.DoctorNotQualifiedException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import com.citamedica.backend.exception.domain.SlotUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentDomainService appointmentDomainService;
    private final SendAppointmentConfirmationUseCase sendAppointmentConfirmationUseCase;
    private final AvailabilityProperties availabilityProperties;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorAvailabilityConfigurationRepository doctorAvailabilityConfigurationRepository;
    private final DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository;
    private final AppointmentAvailabilityValidator appointmentAvailabilityValidator;
    private final AvailabilityConflictDetectionService availabilityConflictDetectionService;
    private final ClinicOfferingRepository clinicOfferingRepository;
    private final DoctorSpecialtyRepository doctorSpecialtyRepository;
    private final CalculateOfferingPriceUseCase calculateOfferingPriceUseCase;

    public CreateAppointmentUseCase(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentDomainService appointmentDomainService,
            SendAppointmentConfirmationUseCase sendAppointmentConfirmationUseCase,
            AvailabilityProperties availabilityProperties,
            TimeSlotRepository timeSlotRepository,
            DoctorAvailabilityConfigurationRepository doctorAvailabilityConfigurationRepository,
            DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository,
            AppointmentAvailabilityValidator appointmentAvailabilityValidator,
            AvailabilityConflictDetectionService availabilityConflictDetectionService,
            ClinicOfferingRepository clinicOfferingRepository,
            DoctorSpecialtyRepository doctorSpecialtyRepository,
            CalculateOfferingPriceUseCase calculateOfferingPriceUseCase) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentDomainService = appointmentDomainService;
        this.sendAppointmentConfirmationUseCase = sendAppointmentConfirmationUseCase;
        this.availabilityProperties = availabilityProperties;
        this.timeSlotRepository = timeSlotRepository;
        this.doctorAvailabilityConfigurationRepository = doctorAvailabilityConfigurationRepository;
        this.doctorAvailabilityBlockRepository = doctorAvailabilityBlockRepository;
        this.appointmentAvailabilityValidator = appointmentAvailabilityValidator;
        this.availabilityConflictDetectionService = availabilityConflictDetectionService;
        this.clinicOfferingRepository = clinicOfferingRepository;
        this.doctorSpecialtyRepository = doctorSpecialtyRepository;
        this.calculateOfferingPriceUseCase = calculateOfferingPriceUseCase;
    }

    @Transactional
    public Appointment execute(
            Long doctorId,
            Long patientId,
            String type,
            LocalDateTime startAt,
            LocalDateTime endAtFromRequest,
            String calBookingId,
            String notes,
            Long timeSlotId,
            Long serviceOfferingId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));

        ClinicOffering offering = null;
        LocalDateTime endAt = endAtFromRequest;
        if (serviceOfferingId != null) {
            offering = clinicOfferingRepository.findById(serviceOfferingId)
                    .orElseThrow(() -> new EntityNotFoundDomainException("Service not found: " + serviceOfferingId));
            if (!offering.isActive()) {
                throw new InvalidDomainOperationException("Service is not active");
            }
            if (offering.getClinic() == null || doctor.getClinic() == null
                    || !offering.getClinic().getId().equals(doctor.getClinic().getId())) {
                throw new InvalidDomainOperationException("Service does not belong to the doctor's clinic");
            }
            if (offering.getMinRequiredSpecialty() != null) {
                Long sid = offering.getMinRequiredSpecialty().getId();
                if (!doctorSpecialtyRepository.existsByDoctorIdAndSpecialtyId(doctorId, sid)) {
                    throw new DoctorNotQualifiedException("Doctor is not qualified for this service");
                }
            }
            int minutes = resolveDurationMinutes(offering, doctorId);
            endAt = startAt.plusMinutes(minutes);
        }

        appointmentDomainService.validateTimes(startAt, endAt);

        boolean managedByCalCom = calBookingId != null && !calBookingId.isBlank();
        TimeSlot lockedSlot = null;
        if (availabilityProperties.isEnforced() && !managedByCalCom) {
            lockedSlot = lockAndValidateSlot(doctor, startAt, endAt, timeSlotId);
            long overlapping = appointmentRepository.countOverlappingScheduled(doctorId, startAt, endAt);
            if (availabilityConflictDetectionService.hasConflictWithConcurrentLimit(
                    overlapping, resolveMaxConcurrent(doctorId, startAt))) {
                throw new ConflictingAppointmentException("Doctor concurrent appointment limit reached for this time window");
            }
        }

        Appointment appointment = new Appointment(doctor, patient, type, startAt, endAt);
        appointment.setClinic(doctor.getClinic());
        if (offering != null) {
            appointment.setClinicOffering(offering);
            CalculateOfferingPriceUseCase.OfferingPriceResult price =
                    calculateOfferingPriceUseCase.execute(doctorId, offering.getId());
            appointment.setTotalAmount(price.effectivePrice());
        }

        if (managedByCalCom) {
            appointment.setCalBookingId(calBookingId);
        }
        if (notes != null) {
            appointment.setNotes(notes);
        }

        Appointment saved = appointmentRepository.save(appointment);

        if (lockedSlot != null) {
            lockedSlot.markBooked(saved);
            timeSlotRepository.save(lockedSlot);
        }

        sendAppointmentConfirmationUseCase.execute(saved);
        return saved;
    }

    private int resolveDurationMinutes(ClinicOffering offering, Long doctorId) {
        if (offering.getMinRequiredSpecialty() == null) {
            return offering.getDurationMinutes();
        }
        Long sid = offering.getMinRequiredSpecialty().getId();
        return doctorSpecialtyRepository.findByDoctorIdAndSpecialtyId(doctorId, sid)
                .map(DoctorSpecialty::getOverrideDurationMinutes)
                .filter(ov -> ov != null && ov > 0)
                .orElse(offering.getDurationMinutes());
    }

    private int resolveMaxConcurrent(Long doctorId, LocalDateTime startAt) {
        ScheduleDayOfWeek day = ScheduleDayOfWeek.fromJava(startAt.getDayOfWeek());
        var cfg = doctorAvailabilityConfigurationRepository.findByDoctorIdAndDay(doctorId, day).orElse(null);
        return appointmentAvailabilityValidator.resolveMaxConcurrent(cfg, 1);
    }

    private TimeSlot lockAndValidateSlot(Doctor doctor, LocalDateTime startAt, LocalDateTime endAt, Long timeSlotId) {
        TimeSlot slot = loadLockedSlot(doctor, startAt, timeSlotId);
        appointmentAvailabilityValidator.validateSlotMatchesAppointment(slot, startAt, endAt);
        List<DoctorAvailabilityBlock> blocks = doctorAvailabilityBlockRepository.findActiveByDoctorId(doctor.getId());
        appointmentAvailabilityValidator.assertNotBlocked(startAt, endAt, blocks);
        return slot;
    }

    private TimeSlot loadLockedSlot(Doctor doctor, LocalDateTime startAt, Long timeSlotId) {
        if (timeSlotId != null) {
            TimeSlot slot = timeSlotRepository.findByIdForUpdate(timeSlotId)
                    .orElseThrow(() -> new SlotUnavailableException("Time slot not found: " + timeSlotId));
            if (!slot.getDoctor().getId().equals(doctor.getId())) {
                throw new SlotUnavailableException("Time slot does not belong to selected doctor");
            }
            return slot;
        }
        return timeSlotRepository.findForUpdateByDoctorAndStartAndStatus(doctor.getId(), startAt, SlotStatus.AVAILABLE)
                .orElseThrow(() -> new SlotUnavailableException("No available slot for requested start time"));
    }
}
