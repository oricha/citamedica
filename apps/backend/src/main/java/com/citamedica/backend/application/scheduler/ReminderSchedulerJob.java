package com.citamedica.backend.application.scheduler;

import com.citamedica.backend.application.usecase.ScheduleAppointmentReminderUseCase;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReminderSchedulerJob {

    private static final Logger log = LoggerFactory.getLogger(ReminderSchedulerJob.class);

    private final AppointmentRepository appointmentRepository;
    private final ScheduleAppointmentReminderUseCase reminderUseCase;

    public ReminderSchedulerJob(AppointmentRepository appointmentRepository, ScheduleAppointmentReminderUseCase reminderUseCase) {
        this.appointmentRepository = appointmentRepository;
        this.reminderUseCase = reminderUseCase;
    }

    @Scheduled(fixedDelay = 3600000)
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusHours(24);
        LocalDateTime to = now.plusHours(25);

        int processed = 0;
        for (var appointment : appointmentRepository.findByStartAtBetween(from, to)) {
            if (appointment.getStatus() == AppointmentStatus.SCHEDULED) {
                reminderUseCase.execute(appointment);
                processed++;
            }
        }
        log.info("Reminder job processed {} appointments", processed);
    }
}
