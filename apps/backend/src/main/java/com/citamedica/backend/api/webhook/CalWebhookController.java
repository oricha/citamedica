package com.citamedica.backend.api.webhook;

import com.citamedica.backend.integration.calcom.CalcomSignatureValidator;
import com.citamedica.backend.integration.calcom.CalcomWebhookHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/webhooks")
public class CalWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(CalWebhookController.class);

    private final CalcomSignatureValidator signatureValidator;
    private final CalcomWebhookHandler webhookHandler;

    public CalWebhookController(CalcomSignatureValidator signatureValidator, CalcomWebhookHandler webhookHandler) {
        this.signatureValidator = signatureValidator;
        this.webhookHandler = webhookHandler;
    }

    @PostMapping("/cal")
    public ResponseEntity<WebhookResponse> handleCalcomWebhook(
            @RequestHeader("x-cal-signature") String signature,
            @RequestBody String payload) {

        logger.info("Received webhook from Cal.com");

        if (!signatureValidator.validate(signature, payload)) {
            logger.warn("Invalid signature for webhook");
            return ResponseEntity.status(401).build();
        }

        try {
            webhookHandler.handle(payload);
            logger.info("Webhook processed successfully");
            return ResponseEntity.ok(new WebhookResponse(true));
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    public static class WebhookResponse {
        private boolean received;

        public WebhookResponse(boolean received) {
            this.received = received;
        }

        public boolean isReceived() {
            return received;
        }

        public void setReceived(boolean received) {
            this.received = received;
        }
    }
}