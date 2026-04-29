package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.config.AvailabilityProperties;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.repository.DoctorAvailabilityBlockRepository;
import com.citamedica.backend.domain.repository.DoctorAvailabilityConfigurationRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import com.citamedica.backend.domain.model.TimeSlot;
import com.citamedica.backend.domain.service.TimeSlotGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Profile("!test")
@Component
public class TimeSlotGenerationJob {

    private static final Logger log = LoggerFactory.getLogger(TimeSlotGenerationJob.class);

    private final AvailabilityProperties availabilityProperties;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityConfigurationRepository configurationRepository;
    private final DoctorAvailabilityBlockRepository blockRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotGenerationService timeSlotGenerationService;

    public TimeSlotGenerationJob(
            AvailabilityProperties availabilityProperties,
            DoctorRepository doctorRepository,
            DoctorAvailabilityConfigurationRepository configurationRepository,
            DoctorAvailabilityBlockRepository blockRepository,
            TimeSlotRepository timeSlotRepository,
            TimeSlotGenerationService timeSlotGenerationService) {
        this.availabilityProperties = availabilityProperties;
        this.doctorRepository = doctorRepository;
        this.configurationRepository = configurationRepository;
        this.blockRepository = blockRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.timeSlotGenerationService = timeSlotGenerationService;
    }

    @Scheduled(cron = "${app.availability.slot-generation.cron:0 0 3 * * *}")
    public void run() {
        long jobStart = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        int horizon = availabilityProperties.getSlotGeneration().getHorizonDays();
        LocalDate rangeStart = now.toLocalDate();
        LocalDate rangeEnd = rangeStart.plusDays(horizon);
        LocalDateTime cutoff = now.minusDays(horizon);
        int doctorsProcessed = 0;
        int slotsCreated = 0;

        for (Doctor doctor : doctorRepository.findByActiveTrue()) {
            var configs = configurationRepository.findByDoctorId(doctor.getId());
            if (configs.isEmpty()) {
                continue;
            }
            doctorsProcessed++;
            timeSlotRepository.deleteOldSlots(
                    doctor.getId(),
                    cutoff,
                    List.of(SlotStatus.AVAILABLE, SlotStatus.BLOCKED));

            var blocks = blockRepository.findActiveByDoctorId(doctor.getId());
            List<TimeSlot> generated = timeSlotGenerationService.generateSlotsForDoctor(
                    doctor, configs, blocks, rangeStart, rangeEnd, now);

            int batchSize = availabilityProperties.getSlotGeneration().getBatchSize();
            List<TimeSlot> batch = new ArrayList<>();
            for (TimeSlot slot : generated) {
                if (!timeSlotRepository.existsByDoctorAndStart(doctor.getId(), slot.getStartTime())) {
                    batch.add(slot);
                    if (batch.size() >= batchSize) {
                        timeSlotRepository.saveAll(batch);
                        slotsCreated += batch.size();
                        batch.clear();
                    }
                }
            }
            if (!batch.isEmpty()) {
                timeSlotRepository.saveAll(batch);
                slotsCreated += batch.size();
            }
        }

        long elapsed = System.currentTimeMillis() - jobStart;
        log.info("TimeSlotGenerationJob finished: doctorsProcessed={} slotsCreatedApprox={} durationMs={}",
                doctorsProcessed, slotsCreated, elapsed);
        if (elapsed > availabilityProperties.getSlotGeneration().getMaxDurationAlertMs()) {
            log.warn("TimeSlotGenerationJob exceeded alert threshold: {}ms", elapsed);
        }
    }
}
