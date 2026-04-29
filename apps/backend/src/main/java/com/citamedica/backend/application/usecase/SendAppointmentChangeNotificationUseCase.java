package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class SendAppointmentChangeNotificationUseCase {

    private final NotificationOrchestrator orchestrator;

    public SendAppointmentChangeNotificationUseCase(NotificationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void execute(Appointment appointment) {
        orchestrator.notifyAppointmentEvent(appointment, NotificationType.CHANGE);
    }
}
