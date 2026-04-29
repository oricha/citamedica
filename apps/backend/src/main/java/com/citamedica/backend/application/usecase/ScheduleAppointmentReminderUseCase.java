package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class ScheduleAppointmentReminderUseCase {

    private final NotificationOrchestrator orchestrator;

    public ScheduleAppointmentReminderUseCase(NotificationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void execute(Appointment appointment) {
        orchestrator.notifyAppointmentEvent(appointment, NotificationType.REMINDER);
    }
}
