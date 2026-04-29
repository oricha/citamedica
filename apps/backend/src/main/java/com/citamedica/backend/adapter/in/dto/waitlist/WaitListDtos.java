package com.citamedica.backend.adapter.in.dto.waitlist;

import com.citamedica.backend.domain.model.AppointmentWaitListEntry;
import com.citamedica.backend.domain.model.WaitListEntryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class WaitListDtos {
    private WaitListDtos() {}

    public record JoinWaitListRequest(
            @NotNull Long doctorId,
            Long clinicId,
            Long serviceId,
            LocalDate preferredStartDate,
            LocalDate preferredEndDate,
            @Size(max = 255) String appointmentType,
            @Size(max = 2000) String notes
    ) {}

    public record UpdateWaitListStatusRequest(
            @NotNull WaitListEntryStatus status
    ) {}

    public record WaitListEntryResponse(
            Long id,
            Long patientId,
            Long doctorId,
            Long clinicId,
            Long serviceId,
            LocalDate preferredStartDate,
            LocalDate preferredEndDate,
            String appointmentType,
            String notes,
            WaitListEntryStatus status,
            LocalDateTime createdAt
    ) {
        public static WaitListEntryResponse from(AppointmentWaitListEntry e) {
            return new WaitListEntryResponse(
                    e.getId(),
                    e.getPatient().getId(),
                    e.getDoctor().getId(),
                    e.getClinic() != null ? e.getClinic().getId() : null,
                    e.getService() != null ? e.getService().getId() : null,
                    e.getPreferredStartDate(),
                    e.getPreferredEndDate(),
                    e.getAppointmentType(),
                    e.getNotes(),
                    e.getStatus(),
                    e.getCreatedAt());
        }
    }
}
