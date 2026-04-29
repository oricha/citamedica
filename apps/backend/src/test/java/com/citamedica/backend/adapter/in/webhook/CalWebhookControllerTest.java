package com.citamedica.backend.adapter.in.webhook;

import com.citamedica.backend.adapter.out.integration.calcom.CalcomSignatureValidator;
import com.citamedica.backend.application.usecase.ProcessBookingEventUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CalWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalcomSignatureValidator signatureValidator;

    @MockBean
    private ProcessBookingEventUseCase processBookingEventUseCase;

    @Test
    void handleCalcomWebhook_missingSignature_returns401() throws Exception {
        String payload = "{\"triggerEvent\":\"BOOKING_CREATED\"}";

        mockMvc.perform(post("/webhooks/cal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.received").value(false))
                .andExpect(jsonPath("$.message").value("Missing signature"));

        verify(signatureValidator, never()).validate(anyString(), anyString());
        verify(processBookingEventUseCase, never()).execute(anyString());
    }

    @Test
    void handleCalcomWebhook_invalidSignature_returns401() throws Exception {
        String payload = "{\"triggerEvent\":\"BOOKING_CREATED\"}";
        when(signatureValidator.validate("sha256=invalid", payload)).thenReturn(false);

        mockMvc.perform(post("/webhooks/cal")
                        .header("x-cal-signature", "sha256=invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.received").value(false))
                .andExpect(jsonPath("$.message").value("Invalid signature"));

        verify(signatureValidator, times(1)).validate("sha256=invalid", payload);
        verify(processBookingEventUseCase, never()).execute(anyString());
    }

    @Test
    void handleCalcomWebhook_validSignature_returns200AndProcessesEvent() throws Exception {
        String payload = "{\"triggerEvent\":\"BOOKING_CREATED\"}";
        when(signatureValidator.validate("sha256=valid", payload)).thenReturn(true);

        mockMvc.perform(post("/webhooks/cal")
                        .header("x-cal-signature", "sha256=valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.received").value(true))
                .andExpect(jsonPath("$.message").value("Webhook processed"));

        verify(signatureValidator, times(1)).validate("sha256=valid", payload);
        verify(processBookingEventUseCase, times(1)).execute(payload);
    }
}
