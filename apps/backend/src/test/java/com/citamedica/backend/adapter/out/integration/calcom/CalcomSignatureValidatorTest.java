package com.citamedica.backend.adapter.out.integration.calcom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.commons.codec.digest.HmacUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CalcomSignatureValidator.
 * Tests HMAC SHA256 signature verification for Cal.com webhooks.
 */
class CalcomSignatureValidatorTest {

    private CalcomSignatureValidator validator;
    private static final String TEST_SECRET = "test-webhook-secret";
    private static final String TEST_PAYLOAD = "{\"triggerEvent\":\"BOOKING_CREATED\",\"payload\":{\"bookingId\":123}}";

    @BeforeEach
    void setUp() {
        validator = new CalcomSignatureValidator(TEST_SECRET);
    }

    @Test
    void validate_WithValidSignature_ReturnsTrue() {
        // Arrange
        String expectedSignature = "sha256=" + HmacUtils.hmacSha256Hex(TEST_SECRET, TEST_PAYLOAD);

        // Act
        boolean result = validator.validate(expectedSignature, TEST_PAYLOAD);

        // Assert
        assertTrue(result, "Valid signature should be accepted");
    }

    @Test
    void validate_WithInvalidSignature_ReturnsFalse() {
        // Arrange
        String invalidSignature = "sha256=invalid_signature_hash";

        // Act
        boolean result = validator.validate(invalidSignature, TEST_PAYLOAD);

        // Assert
        assertFalse(result, "Invalid signature should be rejected");
    }

    @Test
    void validate_WithNullSignature_ReturnsFalse() {
        // Act
        boolean result = validator.validate(null, TEST_PAYLOAD);

        // Assert
        assertFalse(result, "Null signature should be rejected");
    }

    @Test
    void validate_WithEmptySignature_ReturnsFalse() {
        // Act
        boolean result = validator.validate("", TEST_PAYLOAD);

        // Assert
        assertFalse(result, "Empty signature should be rejected");
    }

    @Test
    void validate_WithoutSha256Prefix_ReturnsFalse() {
        // Arrange
        String signatureWithoutPrefix = HmacUtils.hmacSha256Hex(TEST_SECRET, TEST_PAYLOAD);

        // Act
        boolean result = validator.validate(signatureWithoutPrefix, TEST_PAYLOAD);

        // Assert
        assertFalse(result, "Signature without 'sha256=' prefix should be rejected");
    }

    @Test
    void validate_WithDifferentPayload_ReturnsFalse() {
        // Arrange
        String signature = "sha256=" + HmacUtils.hmacSha256Hex(TEST_SECRET, TEST_PAYLOAD);
        String differentPayload = "{\"triggerEvent\":\"BOOKING_CANCELLED\"}";

        // Act
        boolean result = validator.validate(signature, differentPayload);

        // Assert
        assertFalse(result, "Signature for different payload should be rejected");
    }

    @Test
    void validate_WithDifferentSecret_ReturnsFalse() {
        // Arrange
        String differentSecret = "different-secret";
        String signature = "sha256=" + HmacUtils.hmacSha256Hex(differentSecret, TEST_PAYLOAD);

        // Act
        boolean result = validator.validate(signature, TEST_PAYLOAD);

        // Assert
        assertFalse(result, "Signature generated with different secret should be rejected");
    }

    @Test
    void validate_WithSpecialCharactersInPayload_ReturnsTrue() {
        // Arrange
        String specialPayload = "{\"name\":\"José García\",\"email\":\"test@example.com\",\"notes\":\"Cita médica: 10:30\"}";
        String signature = "sha256=" + HmacUtils.hmacSha256Hex(TEST_SECRET, specialPayload);

        // Act
        boolean result = validator.validate(signature, specialPayload);

        // Assert
        assertTrue(result, "Valid signature with special characters should be accepted");
    }

    @Test
    void validate_WithLargePayload_ReturnsTrue() {
        // Arrange
        StringBuilder largePayload = new StringBuilder("{\"data\":");
        for (int i = 0; i < 1000; i++) {
            largePayload.append("\"item").append(i).append("\",");
        }
        largePayload.append("\"end\"}");
        String payload = largePayload.toString();
        String signature = "sha256=" + HmacUtils.hmacSha256Hex(TEST_SECRET, payload);

        // Act
        boolean result = validator.validate(signature, payload);

        // Assert
        assertTrue(result, "Valid signature with large payload should be accepted");
    }

    @Test
    void validate_CaseSensitiveSignature_ReturnsFalse() {
        // Arrange
        String validSignature = "sha256=" + HmacUtils.hmacSha256Hex(TEST_SECRET, TEST_PAYLOAD);
        String uppercaseSignature = validSignature.toUpperCase();

        // Act
        boolean result = validator.validate(uppercaseSignature, TEST_PAYLOAD);

        // Assert
        assertFalse(result, "Signature comparison should be case-sensitive");
    }
}