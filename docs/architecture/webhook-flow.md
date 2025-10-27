# Webhook Flow - Cal.com Integration

## Overview

CitaMedica integrates with Cal.com through webhooks to automatically synchronize appointment bookings. When a user creates, reschedules, or cancels an appointment in Cal.com, a webhook event is sent to CitaMedica's backend, which processes the event and updates the database accordingly.

## Webhook Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Cal.com Platform                            │
│                                                                       │
│  User Action:                                                        │
│  • Creates booking                                                   │
│  • Reschedules booking                                               │
│  • Cancels booking                                                   │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │              Cal.com Webhook System                         │    │
│  │  1. Detect booking event                                    │    │
│  │  2. Prepare webhook payload                                 │    │
│  │  3. Generate HMAC SHA256 signature                          │    │
│  │  4. Send HTTP POST request                                  │    │
│  └────────────────────────────────────────────────────────────┘    │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                │ HTTP POST
                                │ X-Cal-Signature-256: <signature>
                                │ Content-Type: application/json
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│                    CitaMedica Backend API                            │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │         Step 1: CorrelationIdFilter                         │    │
│  │  • Generate/Extract correlation ID                          │    │
│  │  • Add to MDC (Mapped Diagnostic Context)                   │    │
│  │  • Log request received                                     │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                │                                     │
│  ┌────────────────────────────▼───────────────────────────────┐    │
│  │         Step 2: CalWebhookController                        │    │
│  │  • Receive POST /webhooks/cal                               │    │
│  │  • Extract signature from header                            │    │
│  │  • Extract payload from body                                │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                │                                     │
│  ┌────────────────────────────▼───────────────────────────────┐    │
│  │         Step 3: CalcomSignatureValidator                    │    │
│  │  • Compute HMAC SHA256 of payload                           │    │
│  │  • Compare with received signature                          │    │
│  │  • Throw exception if invalid                               │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                │                                     │
│                                │ ✓ Signature Valid                   │
│                                │                                     │
│  ┌────────────────────────────▼───────────────────────────────┐    │
│  │         Step 4: CalcomWebhookHandler                        │    │
│  │  • Parse webhook event type                                 │    │
│  │  • Extract booking data                                     │    │
│  │  • Route to appropriate handler                             │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                │                                     │
│                    ┌───────────┴───────────┐                        │
│                    │                       │                        │
│         ┌──────────▼──────────┐ ┌─────────▼─────────┐             │
│         │  BOOKING_CREATED    │ │ BOOKING_RESCHEDULED│             │
│         └──────────┬──────────┘ └─────────┬─────────┘             │
│                    │                       │                        │
│         ┌──────────▼──────────────────────▼─────────┐             │
│         │         BOOKING_CANCELLED                  │             │
│         └──────────┬───────────────────────────────┘             │
│                    │                                               │
│  ┌────────────────▼────────────────────────────────────────┐     │
│  │         Step 5: AppointmentService                       │     │
│  │  • Validate business rules                               │     │
│  │  • Create/Update/Cancel appointment                      │     │
│  │  • Map cal_booking_id                                    │     │
│  │  • Save to database                                      │     │
│  └────────────────────────────────────────────────────────┘     │
│                    │                                               │
│  ┌────────────────▼────────────────────────────────────────┐     │
│  │         Step 6: AuditService                             │     │
│  │  • Log operation details                                 │     │
│  │  • Record actor, action, entity                          │     │
│  │  • Store metadata                                        │     │
│  └────────────────────────────────────────────────────────┘     │
│                    │                                               │
│  ┌────────────────▼────────────────────────────────────────┐     │
│  │         Step 7: NotificationService (Optional)           │     │
│  │  • Send confirmation email                               │     │
│  │  • Send SMS notification                                 │     │
│  │  • Log notification sent                                 │     │
│  └────────────────────────────────────────────────────────┘     │
│                    │                                               │
│  ┌────────────────▼────────────────────────────────────────┐     │
│  │         Step 8: Response                                 │     │
│  │  • Return 200 OK to Cal.com                              │     │
│  │  • Include correlation ID in logs                        │     │
│  └────────────────────────────────────────────────────────┘     │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                │ HTTP 200 OK
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│                          Cal.com Platform                            │
│  • Marks webhook as delivered                                       │
│  • Continues with booking flow                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Webhook Events

### 1. BOOKING_CREATED

Triggered when a new appointment is booked in Cal.com.

**Payload Example**:
```json
{
  "triggerEvent": "BOOKING_CREATED",
  "createdAt": "2025-10-27T10:30:00.000Z",
  "payload": {
    "id": 123,
    "uid": "abc123def456",
    "title": "Consulta Médica",
    "description": "Consulta general",
    "startTime": "2025-10-28T15:00:00.000Z",
    "endTime": "2025-10-28T15:30:00.000Z",
    "attendees": [
      {
        "email": "patient@example.com",
        "name": "John Doe",
        "timeZone": "Europe/Madrid",
        "locale": "es"
      }
    ],
    "organizer": {
      "id": 1,
      "email": "doctor@clinic.com",
      "name": "Dr. Smith",
      "timeZone": "Europe/Madrid",
      "username": "dr-smith"
    },
    "metadata": {
      "doctorId": "1",
      "patientId": "1",
      "appointmentType": "Consulta General"
    }
  }
}
```

**Processing**:
1. Extract booking ID (`payload.id`)
2. Find or create patient from attendee data
3. Find doctor by Cal.com username or metadata
4. Create new `Appointment` entity
5. Set `cal_booking_id` to link with Cal.com
6. Set status to `SCHEDULED`
7. Save to database
8. Send confirmation notification

### 2. BOOKING_RESCHEDULED

Triggered when an existing appointment is rescheduled.

**Payload Example**:
```json
{
  "triggerEvent": "BOOKING_RESCHEDULED",
  "createdAt": "2025-10-27T11:00:00.000Z",
  "payload": {
    "id": 123,
    "uid": "abc123def456",
    "title": "Consulta Médica",
    "startTime": "2025-10-29T16:00:00.000Z",
    "endTime": "2025-10-29T16:30:00.000Z",
    "rescheduleReason": "Patient requested different time",
    "attendees": [...],
    "organizer": {...}
  }
}
```

**Processing**:
1. Find existing appointment by `cal_booking_id`
2. Update `startTime` and `endTime`
3. Update `updatedAt` timestamp
4. Keep status as `SCHEDULED`
5. Save changes
6. Send rescheduling notification

### 3. BOOKING_CANCELLED

Triggered when an appointment is cancelled.

**Payload Example**:
```json
{
  "triggerEvent": "BOOKING_CANCELLED",
  "createdAt": "2025-10-27T12:00:00.000Z",
  "payload": {
    "id": 123,
    "uid": "abc123def456",
    "title": "Consulta Médica",
    "cancellationReason": "Patient requested cancellation",
    "cancelledBy": "patient@example.com"
  }
}
```

**Processing**:
1. Find existing appointment by `cal_booking_id`
2. Update status to `CANCELLED`
3. Record cancellation reason in notes
4. Update `updatedAt` timestamp
5. Save changes
6. Send cancellation notification

## Security: Signature Verification

### HMAC SHA256 Signature

Cal.com signs each webhook request with HMAC SHA256 to ensure authenticity and integrity.

**Signature Generation (Cal.com side)**:
```
signature = HMAC_SHA256(webhook_secret, request_body)
```

**Signature Verification (CitaMedica side)**:
```java
public boolean verifySignature(String payload, String receivedSignature) {
    try {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
            webhookSecret.getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        hmac.init(secretKey);
        
        byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String computedSignature = Hex.encodeHexString(hash);
        
        return MessageDigest.isEqual(
            computedSignature.getBytes(),
            receivedSignature.getBytes()
        );
    } catch (Exception e) {
        log.error("Error verifying webhook signature", e);
        return false;
    }
}
```

**Security Benefits**:
- Prevents unauthorized webhook calls
- Ensures payload hasn't been tampered with
- Protects against replay attacks (when combined with timestamp validation)

### Request Headers

```http
POST /webhooks/cal HTTP/1.1
Host: backend-api:8080
Content-Type: application/json
X-Cal-Signature-256: a1b2c3d4e5f6...
X-Correlation-ID: 550e8400-e29b-41d4-a716-446655440000
Content-Length: 1234

{...payload...}
```

## Error Handling

### Invalid Signature

```
Status: 401 Unauthorized

{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Invalid webhook signature",
  "instance": "/webhooks/cal",
  "timestamp": "2025-10-27T10:30:00.000Z"
}
```

**Action**: Cal.com will retry the webhook

### Booking Not Found (Reschedule/Cancel)

```
Status: 404 Not Found

{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Appointment with cal_booking_id 123 not found",
  "instance": "/webhooks/cal",
  "timestamp": "2025-10-27T10:30:00.000Z"
}
```

**Action**: Log error, return 404 (Cal.com will retry)

### Database Error

```
Status: 500 Internal Server Error

{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Error processing webhook",
  "instance": "/webhooks/cal",
  "timestamp": "2025-10-27T10:30:00.000Z"
}
```

**Action**: Cal.com will retry with exponential backoff

## Logging and Observability

### Correlation ID

Every webhook request gets a unique correlation ID that flows through all operations:

```
2025-10-27 10:30:00.123 [http-nio-8080-exec-1] INFO  CalWebhookController - [550e8400-e29b-41d4-a716-446655440000] Received webhook from Cal.com
2025-10-27 10:30:00.125 [http-nio-8080-exec-1] INFO  CalcomSignatureValidator - [550e8400-e29b-41d4-a716-446655440000] Webhook signature verified successfully
2025-10-27 10:30:00.130 [http-nio-8080-exec-1] INFO  CalcomWebhookHandler - [550e8400-e29b-41d4-a716-446655440000] Processing BOOKING_CREATED event for booking ID: 123
2025-10-27 10:30:00.145 [http-nio-8080-exec-1] INFO  AppointmentService - [550e8400-e29b-41d4-a716-446655440000] Created appointment with cal_booking_id: 123
2025-10-27 10:30:00.150 [http-nio-8080-exec-1] INFO  AuditService - [550e8400-e29b-41d4-a716-446655440000] Logged operation: CREATE_APPOINTMENT
```

### Structured Logging

All webhook operations are logged in JSON format for easy parsing:

```json
{
  "timestamp": "2025-10-27T10:30:00.123Z",
  "level": "INFO",
  "logger": "CalWebhookController",
  "message": "Received webhook from Cal.com",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "calBookingId": "123",
  "eventType": "BOOKING_CREATED",
  "processingTimeMs": 27
}
```

### Metrics

Key metrics tracked:
- `webhook.received.total` - Total webhooks received
- `webhook.processed.success` - Successfully processed webhooks
- `webhook.processed.failure` - Failed webhook processing
- `webhook.signature.invalid` - Invalid signature attempts
- `webhook.processing.duration` - Processing time histogram

## Retry Strategy

### Cal.com Retry Behavior

If CitaMedica returns a non-2xx status code, Cal.com will retry:

1. **Immediate retry** after 1 second
2. **Second retry** after 5 seconds
3. **Third retry** after 30 seconds
4. **Fourth retry** after 2 minutes
5. **Fifth retry** after 10 minutes
6. **Final retry** after 1 hour

After 6 failed attempts, the webhook is marked as failed and requires manual intervention.

### CitaMedica Response Strategy

- **200 OK**: Webhook processed successfully
- **400 Bad Request**: Invalid payload (won't retry)
- **401 Unauthorized**: Invalid signature (won't retry)
- **404 Not Found**: Resource not found (will retry)
- **500 Internal Server Error**: Processing error (will retry)

### Idempotency

Webhook processing is **idempotent** - processing the same webhook multiple times produces the same result:

```java
@Transactional
public void handleBookingCreated(WebhookEvent event) {
    String calBookingId = event.getPayload().getId();
    
    // Check if appointment already exists
    Optional<Appointment> existing = appointmentRepository
        .findByCalBookingId(calBookingId);
    
    if (existing.isPresent()) {
        log.info("Appointment with cal_booking_id {} already exists, skipping", 
                 calBookingId);
        return; // Idempotent - no duplicate created
    }
    
    // Create new appointment
    Appointment appointment = createFromWebhook(event);
    appointmentRepository.save(appointment);
}
```

## Testing Webhooks

### Manual Testing with cURL

```bash
# Generate signature
PAYLOAD='{"triggerEvent":"BOOKING_CREATED","payload":{...}}'
SECRET="your-webhook-secret"
SIGNATURE=$(echo -n "$PAYLOAD" | openssl dgst -sha256 -hmac "$SECRET" | cut -d' ' -f2)

# Send webhook
curl -X POST http://localhost:8080/webhooks/cal \
  -H "Content-Type: application/json" \
  -H "X-Cal-Signature-256: $SIGNATURE" \
  -d "$PAYLOAD"
```

### Integration Tests

```java
@Test
void shouldProcessBookingCreatedWebhook() {
    // Given
    String payload = loadTestPayload("booking_created.json");
    String signature = generateSignature(payload);
    
    // When
    mockMvc.perform(post("/webhooks/cal")
        .header("X-Cal-Signature-256", signature)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isOk());
    
    // Then
    Appointment appointment = appointmentRepository
        .findByCalBookingId("123")
        .orElseThrow();
    
    assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    assertThat(appointment.getCalBookingId()).isEqualTo("123");
}
```

### Contract Tests

Verify that CitaMedica correctly handles Cal.com's webhook format:

```java
@Test
void shouldHandleCalcomWebhookContract() {
    // Load actual Cal.com webhook example
    String calcomPayload = loadFromCalcomDocs("booking_created_example.json");
    
    // Verify we can parse it
    WebhookEvent event = objectMapper.readValue(calcomPayload, WebhookEvent.class);
    
    // Verify we extract all required fields
    assertThat(event.getTriggerEvent()).isNotNull();
    assertThat(event.getPayload().getId()).isNotNull();
    assertThat(event.getPayload().getStartTime()).isNotNull();
}
```

## Monitoring and Alerts

### Key Metrics to Monitor

1. **Webhook Success Rate**: Should be > 99%
2. **Processing Time**: Should be < 500ms (p95)
3. **Invalid Signatures**: Should be near 0 (indicates attack or misconfiguration)
4. **Retry Rate**: Should be < 1% (indicates system issues)

### Alerts

```yaml
alerts:
  - name: HighWebhookFailureRate
    condition: webhook.processed.failure.rate > 0.05
    severity: critical
    message: "Webhook failure rate above 5%"
    
  - name: SlowWebhookProcessing
    condition: webhook.processing.duration.p95 > 1000ms
    severity: warning
    message: "Webhook processing is slow"
    
  - name: InvalidSignatureSpike
    condition: webhook.signature.invalid.rate > 0.01
    severity: warning
    message: "Unusual number of invalid webhook signatures"
```

## Troubleshooting

### Webhook Not Received

1. Check Cal.com webhook configuration
2. Verify webhook URL is correct
3. Check firewall/network rules
4. Review Cal.com webhook logs

### Invalid Signature Errors

1. Verify `CALCOM_WEBHOOK_SECRET` matches Cal.com configuration
2. Check for whitespace in secret
3. Ensure secret is exactly as configured in Cal.com
4. Test with manual signature generation

### Appointments Not Created

1. Check backend logs for errors
2. Verify doctor mapping (Cal.com username → doctor ID)
3. Check patient data extraction
4. Review database constraints

### Duplicate Appointments

1. Verify idempotency logic
2. Check `cal_booking_id` uniqueness constraint
3. Review transaction boundaries
4. Check for race conditions

## Best Practices

1. **Always verify signatures** - Never process unverified webhooks
2. **Use correlation IDs** - Track requests end-to-end
3. **Make processing idempotent** - Handle retries gracefully
4. **Respond quickly** - Process asynchronously if needed
5. **Log everything** - Structured logs for debugging
6. **Monitor metrics** - Track success rates and performance
7. **Test thoroughly** - Unit, integration, and contract tests
8. **Handle errors gracefully** - Return appropriate status codes

## Conclusion

The webhook integration between Cal.com and CitaMedica provides:
- **Real-time synchronization** of appointment data
- **Secure communication** via HMAC signatures
- **Reliable processing** with retry logic and idempotency
- **Full observability** through logging and metrics
- **Robust error handling** for production reliability

This architecture ensures that appointments created in Cal.com are automatically and reliably synchronized to CitaMedica's database, providing a seamless experience for users.