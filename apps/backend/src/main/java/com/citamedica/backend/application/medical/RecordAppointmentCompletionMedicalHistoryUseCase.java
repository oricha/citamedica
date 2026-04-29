package com.citamedica.backend.application.medical;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditAction;
import com.citamedica.backend.domain.model.medical.MedicalHistorySourceTypes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RecordAppointmentCompletionMedicalHistoryUseCase {

    private final MedicalHistoryRecorder medicalHistoryRecorder;

    public RecordAppointmentCompletionMedicalHistoryUseCase(MedicalHistoryRecorder medicalHistoryRecorder) {
        this.medicalHistoryRecorder = medicalHistoryRecorder;
    }

    @Transactional
    public void execute(Appointment appointment) {
        if (appointment.getPatient() == null) {
            return;
        }
        String actor = resolveActor();
        String desc = buildDescription(appointment);
        medicalHistoryRecorder.appendTimelineEvent(
                appointment.getPatient(),
                "APPOINTMENT_COMPLETED",
                appointment.getEndAt() != null ? appointment.getEndAt() : LocalDateTime.now(),
                "Appointment completed",
                desc,
                appointment.getId(),
                MedicalHistorySourceTypes.APPOINTMENT);
        medicalHistoryRecorder.audit(
                appointment.getPatient(),
                MedicalHistorySourceTypes.APPOINTMENT,
                appointment.getId(),
                MedicalHistoryAuditAction.UPDATE,
                "{\"status\":\"COMPLETED\"}",
                actor,
                null);
    }

    private static String buildDescription(Appointment a) {
        String type = a.getType() != null ? a.getType() : "Visit";
        String notes = a.getNotes() != null && !a.getNotes().isBlank() ? a.getNotes() : "";
        return type + (notes.isEmpty() ? "" : " — " + notes);
    }

    private static String resolveActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? auth.getName() : "system";
    }
}
