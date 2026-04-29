package com.citamedica.backend.adapter.in.webhook;

import com.citamedica.backend.application.usecase.ProcessBookingEventUseCase;
import com.citamedica.backend.adapter.out.integration.calcom.CalcomSignatureValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Controller for handling Cal.com webhooks.
 * Validates HMAC signatures and processes booking events.
 */
@RestController
@RequestMapping("/webhooks")
public class CalWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(CalWebhookController.class);

    private final CalcomSignatureValidator signatureValidator;
    private final ProcessBookingEventUseCase processBookingEventUseCase;

    public CalWebhookController(
            CalcomSignatureValidator signatureValidator,
            ProcessBookingEventUseCase processBookingEventUseCase) {
        this.signatureValidator = signatureValidator;
        this.processBookingEventUseCase = processBookingEventUseCase;
    }

    /**
     * Handles incoming webhooks from Cal.com.
     * Verifies HMAC signature and processes the event.
     */
    @PostMapping("/cal")
    public ResponseEntity<WebhookResponse> handleCalcomWebhook(
            @RequestHeader(value = "x-cal-signature", required = false) String signature,
            @RequestBody String payload) {

        String correlationId = MDC.get("correlationId");
        logger.info("Received webhook from Cal.com - correlationId: {}", correlationId);

        // Validate signature
        if (signature == null || signature.isEmpty()) {
            logger.warn("Missing x-cal-signature header - correlationId: {}", correlationId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponse(false, "Missing signature"));
        }

        if (!signatureValidator.validate(signature, payload)) {
            logger.warn("Invalid signature for webhook - correlationId: {}", correlationId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponse(false, "Invalid signature"));
        }

        // Process webhook
        try {
            processBookingEventUseCase.execute(payload);
            logger.info("Webhook processed successfully - correlationId: {}", correlationId);
            return ResponseEntity.ok(new WebhookResponse(true, "Webhook processed"));
        } catch (Exception e) {
            logger.error("Error processing webhook - correlationId: {}, error: {}",
                correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponse(false, "Error processing webhook"));
        }
    }

    /**
     * Response DTO for webhook endpoint
     */
    public static class WebhookResponse {
        private boolean received;
        private String message;

        public WebhookResponse(boolean received, String message) {
            this.received = received;
            this.message = message;
        }

        public boolean isReceived() {
            return received;
        }

        public void setReceived(boolean received) {
            this.received = received;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
