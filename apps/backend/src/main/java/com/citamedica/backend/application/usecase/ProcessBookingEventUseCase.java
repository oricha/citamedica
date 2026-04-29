package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.out.integration.calcom.CalcomWebhookHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessBookingEventUseCase {

    private final CalcomWebhookHandler webhookHandler;

    public ProcessBookingEventUseCase(CalcomWebhookHandler webhookHandler) {
        this.webhookHandler = webhookHandler;
    }

    @Transactional
    public void execute(String payload) {
        webhookHandler.handle(payload);
    }
}
