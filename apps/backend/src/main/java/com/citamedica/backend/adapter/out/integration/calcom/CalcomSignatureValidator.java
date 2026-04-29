package com.citamedica.backend.adapter.out.integration.calcom;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.codec.digest.HmacUtils;

@Component
public class CalcomSignatureValidator {

    private final String webhookSecret;

    public CalcomSignatureValidator(@Value("${calcom.webhook.secret}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public boolean validate(String signature, String payload) {
        if (signature == null || !signature.startsWith("sha256=")) {
            return false;
        }
        String expectedSignature = "sha256=" + HmacUtils.hmacSha256Hex(webhookSecret, payload);
        return signature.equals(expectedSignature);
    }
}