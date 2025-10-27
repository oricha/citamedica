# Data Model - CitaMedica

## Overview

CitaMedica's data model is designed to support medical appointment management with a focus on:
- **Multi-clinic support**: Multiple clinics can use the platform
- **Doctor-patient relationships**: Track appointments between doctors and patients
- **Cal.com integration**: Link appointments with external booking system
- **Audit trail**: Complete history of all operations
- **Consent management**: GDPR-compliant consent tracking

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Database Schema                              │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────┐
│     Clinic       │
│──────────────────│
│ id (PK)          │◄──────────┐
│ name             │            │
│ slug             │            │ 1
│ address          │            │
│ phone            │            │
│ email            │            │
│ cal_team_id      │            │
│ active           │            │
│ created_at       │            │
│ updated_at       │            │
└──────────────────┘            │
                                │
                                │
                                │ N
┌──────────────────┐            │
│     Doctor       │            │
│──────────────────│            │
│ id (PK)          │            │
│ clinic_id (FK)   │────────────┘
│ first_name       │
│ last_name        │
│ specialty        │
│ email            │
│ phone            │
│ cal_username     │
│ active           │
│ created_at       │
│ updated_at       │
└────────┬─────────┘
         │
         │ 1
         │
         │
         │ N
┌────────▼─────────┐            ┌──────────────────┐
│   Appointment    │            │     Patient      │
│──────────────────│            │──────────────────│
│ id (PK)          │            │ id (PK)          │
│ doctor_id (FK)   │            │ first_name       │
│ patient_id (FK)  │────────────┤ last_name        │◄──┐
│ cal_booking_id   │         N  │ email            │   │
│ appointment_type │            │ phone            │   │
│ start_time       │            │ date_of_birth    │   │
│ end_time         │            │ insurance_provider│  │
│ status           │            │ insurance_number │   │
│ notes            │            │ created_at       │   │
│ created_at       │            │ updated_at       │   │
│ updated_at       │            └──────────────────┘   │
└────────┬─────────┘                                    │
         │                                              │
         │ 1                                            │
         │                                              │
         │                                              │
         │ N                                            │
┌────────▼─────────┐                                    │
│     Consent      │                                    │
│──────────────────│                                    │
│ id (PK)          │                                    │
│ patient_id (FK)  │────────────────────────────────────┘
│ consent_type     │                                 1
│ given            │
│ given_at         │
│ withdrawn_at     │
│ ip_address       │
│ user_agent       │
│ created_at       │
│ updated_at       │
└──────────────────┘


┌──────────────────┐
│    AuditLog      │
│──────────────────│
│ id (PK)          │
│ actor            │
│ action           │
│ entity_type      │
│ entity_id        │
│ metadata         │
│ ip_address       │
│ user_agent       │
│ correlation_id   │
│ created_at       │
└──────────────────┘
```

## Entity Descriptions

### Clinic

Represents a medical clinic or healthcare facility.

**Table**: `clinic`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| name | VARCHAR(255) | NOT NULL | Clinic name |
| slug | VARCHAR(100) | NOT NULL, UNIQUE | URL-friendly identifier |
| address | TEXT | | Physical address |
| phone | VARCHAR(20) | | Contact phone |
| email | VARCHAR(255) | | Contact email |
| cal_team_id | VARCHAR(100) | | Cal.com team identifier |
| active | BOOLEAN | NOT NULL, DEFAULT true | Whether clinic is active |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Indexes**:
- `idx_clinic_slug` on `slug`
- `idx_clinic_active` on `active`

**Business Rules**:
- Slug must be unique across all clinics
- Name is required and cannot be empty
- At least one contact method (phone or email) should be provided

**Example**:
```sql
INSERT INTO clinic (name, slug, address, phone, email, cal_team_id, active)
VALUES (
    'Clínica Demo CitaMedica',
    'clinica-demo',
    'Calle Principal 123, Madrid, 28001',
    '+34 912 345 678',
    'info@clinicademo.com',
    'demo-team-id',
    true
);
```

### Doctor

Represents a medical professional working at a clinic.

**Table**: `doctor`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| clinic_id | BIGINT | NOT NULL, FOREIGN KEY → clinic(id) | Associated clinic |
| first_name | VARCHAR(100) | NOT NULL | Doctor's first name |
| last_name | VARCHAR(100) | NOT NULL | Doctor's last name |
| specialty | VARCHAR(100) | | Medical specialty |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Professional email |
| phone | VARCHAR(20) | | Contact phone |
| cal_username | VARCHAR(100) | UNIQUE | Cal.com username |
| active | BOOLEAN | NOT NULL, DEFAULT true | Whether doctor is active |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Indexes**:
- `idx_doctor_clinic_id` on `clinic_id`
- `idx_doctor_email` on `email`
- `idx_doctor_cal_username` on `cal_username`
- `idx_doctor_active` on `active`

**Business Rules**:
- Email must be unique across all doctors
- Cal.com username must be unique if provided
- Doctor must be associated with exactly one clinic
- Only active doctors can have new appointments

**Example**:
```sql
INSERT INTO doctor (clinic_id, first_name, last_name, specialty, email, phone, cal_username, active)
VALUES (
    1,
    'María',
    'García López',
    'Cardiología',
    'maria.garcia@clinicademo.com',
    '+34 612 345 678',
    'dr-maria-garcia',
    true
);
```

### Patient

Represents a patient who can book appointments.

**Table**: `patient`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| first_name | VARCHAR(100) | NOT NULL | Patient's first name |
| last_name | VARCHAR(100) | NOT NULL | Patient's last name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Contact email |
| phone | VARCHAR(20) | | Contact phone |
| date_of_birth | DATE | | Date of birth |
| insurance_provider | VARCHAR(100) | | Insurance company name |
| insurance_number | VARCHAR(50) | | Insurance policy number |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Indexes**:
- `idx_patient_email` on `email`
- `idx_patient_phone` on `phone`

**Business Rules**:
- Email must be unique across all patients
- At least one contact method (email or phone) is required
- Date of birth is optional but recommended for age verification

**Example**:
```sql
INSERT INTO patient (first_name, last_name, email, phone, date_of_birth, insurance_provider, insurance_number)
VALUES (
    'Ana',
    'Rodríguez Sánchez',
    'ana.rodriguez@email.com',
    '+34 634 567 890',
    '1985-03-15',
    'Sanitas',
    'SAN123456'
);
```

### Appointment

Represents a scheduled medical appointment.

**Table**: `appointment`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| doctor_id | BIGINT | NOT NULL, FOREIGN KEY → doctor(id) | Assigned doctor |
| patient_id | BIGINT | NOT NULL, FOREIGN KEY → patient(id) | Patient |
| cal_booking_id | VARCHAR(100) | UNIQUE | Cal.com booking ID |
| appointment_type | VARCHAR(100) | NOT NULL | Type of appointment |
| start_time | TIMESTAMP | NOT NULL | Appointment start time |
| end_time | TIMESTAMP | NOT NULL | Appointment end time |
| status | VARCHAR(20) | NOT NULL | Current status |
| notes | TEXT | | Additional notes |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Indexes**:
- `idx_appointment_doctor_id` on `doctor_id`
- `idx_appointment_patient_id` on `patient_id`
- `idx_appointment_cal_booking_id` on `cal_booking_id`
- `idx_appointment_start_time` on `start_time`
- `idx_appointment_status` on `status`
- `idx_appointment_doctor_date` on `(doctor_id, DATE(start_time))`

**Status Values** (Enum: `AppointmentStatus`):
- `SCHEDULED`: Appointment is confirmed and scheduled
- `COMPLETED`: Appointment has been completed
- `CANCELLED`: Appointment was cancelled
- `NO_SHOW`: Patient didn't show up

**Business Rules**:
- End time must be after start time
- No overlapping appointments for the same doctor
- Cal.com booking ID must be unique if provided
- Status transitions:
  - `SCHEDULED` → `COMPLETED`, `CANCELLED`, `NO_SHOW`
  - `CANCELLED` → (terminal state)
  - `COMPLETED` → (terminal state)
  - `NO_SHOW` → (terminal state)

**Example**:
```sql
INSERT INTO appointment (doctor_id, patient_id, cal_booking_id, appointment_type, start_time, end_time, status, notes)
VALUES (
    1,
    1,
    'abc123def456',
    'Consulta de Cardiología',
    '2025-10-28 15:00:00',
    '2025-10-28 15:30:00',
    'SCHEDULED',
    'Primera consulta'
);
```

### Consent

Tracks patient consent for data processing (GDPR compliance).

**Table**: `consent`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| patient_id | BIGINT | NOT NULL, FOREIGN KEY → patient(id) | Patient who gave consent |
| consent_type | VARCHAR(50) | NOT NULL | Type of consent |
| given | BOOLEAN | NOT NULL | Whether consent is given |
| given_at | TIMESTAMP | | When consent was given |
| withdrawn_at | TIMESTAMP | | When consent was withdrawn |
| ip_address | VARCHAR(45) | | IP address of consent action |
| user_agent | TEXT | | Browser user agent |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Indexes**:
- `idx_consent_patient_id` on `patient_id`
- `idx_consent_type` on `consent_type`
- `idx_consent_given` on `given`

**Consent Types** (Enum: `ConsentType`):
- `DATA_PROCESSING`: General data processing consent
- `MARKETING`: Marketing communications consent
- `THIRD_PARTY_SHARING`: Sharing data with third parties

**Business Rules**:
- Each patient can have multiple consent records (one per type)
- Consent can be given and withdrawn multiple times
- IP address and user agent should be recorded for audit purposes
- `given_at` is set when `given` is true
- `withdrawn_at` is set when `given` changes from true to false

**Example**:
```sql
INSERT INTO consent (patient_id, consent_type, given, given_at, ip_address, user_agent)
VALUES (
    1,
    'DATA_PROCESSING',
    true,
    NOW(),
    '192.168.1.100',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
);
```

### AuditLog

Records all significant operations for compliance and debugging.

**Table**: `audit_log`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| actor | VARCHAR(255) | NOT NULL | Who performed the action |
| action | VARCHAR(100) | NOT NULL | What action was performed |
| entity_type | VARCHAR(100) | NOT NULL | Type of entity affected |
| entity_id | BIGINT | | ID of affected entity |
| metadata | JSONB | | Additional context data |
| ip_address | VARCHAR(45) | | IP address of actor |
| user_agent | TEXT | | Browser user agent |
| correlation_id | VARCHAR(100) | | Request correlation ID |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | When action occurred |

**Indexes**:
- `idx_audit_log_actor` on `actor`
- `idx_audit_log_action` on `action`
- `idx_audit_log_entity` on `(entity_type, entity_id)`
- `idx_audit_log_correlation_id` on `correlation_id`
- `idx_audit_log_created_at` on `created_at`

**Common Actions**:
- `CREATE_APPOINTMENT`
- `UPDATE_APPOINTMENT`
- `CANCEL_APPOINTMENT`
- `CREATE_PATIENT`
- `UPDATE_PATIENT`
- `WEBHOOK_RECEIVED`
- `LOGIN`
- `LOGOUT`

**Business Rules**:
- All sensitive operations must be audited
- Audit logs are immutable (no updates or deletes)
- Metadata should contain relevant context (old/new values, etc.)
- Correlation ID links related operations

**Example**:
```sql
INSERT INTO audit_log (actor, action, entity_type, entity_id, metadata, ip_address, correlation_id)
VALUES (
    'system',
    'CREATE_APPOINTMENT',
    'Appointment',
    1,
    '{"cal_booking_id": "abc123", "doctor_id": 1, "patient_id": 1}'::jsonb,
    '10.0.0.1',
    '550e8400-e29b-41d4-a716-446655440000'
);
```

## Database Migrations

All schema changes are managed through Flyway migrations located in:
`apps/backend/src/main/resources/db/migration/`

### Migration Files

1. **V1__create_clinic_table.sql**: Creates clinic table
2. **V2__create_doctor_table.sql**: Creates doctor table with FK to clinic
3. **V3__create_patient_table.sql**: Creates patient table
4. **V4__create_appointment_table.sql**: Creates appointment table with FKs
5. **V5__create_consent_table.sql**: Creates consent table
6. **V6__create_audit_log_table.sql**: Creates audit_log table
7. **V7__create_indexes.sql**: Creates performance indexes

### Migration Best Practices

- **Never modify existing migrations**: Create new ones for changes
- **Use transactions**: Wrap DDL in transactions when possible
- **Test rollback**: Ensure migrations can be rolled back if needed
- **Add indexes carefully**: Consider impact on write performance
- **Document changes**: Add comments explaining complex migrations

## Relationships

### One-to-Many Relationships

1. **Clinic → Doctor**: One clinic has many doctors
   - Foreign Key: `doctor.clinic_id → clinic.id`
   - Cascade: ON DELETE RESTRICT (cannot delete clinic with doctors)

2. **Doctor → Appointment**: One doctor has many appointments
   - Foreign Key: `appointment.doctor_id → doctor.id`
   - Cascade: ON DELETE RESTRICT (cannot delete doctor with appointments)

3. **Patient → Appointment**: One patient has many appointments
   - Foreign Key: `appointment.patient_id → patient.id`
   - Cascade: ON DELETE RESTRICT (cannot delete patient with appointments)

4. **Patient → Consent**: One patient has many consent records
   - Foreign Key: `consent.patient_id → patient.id`
   - Cascade: ON DELETE CASCADE (delete consents when patient is deleted)

### Unique Constraints

- `clinic.slug`: Ensures unique URL identifiers
- `doctor.email`: Prevents duplicate doctor accounts
- `doctor.cal_username`: Links to unique Cal.com user
- `patient.email`: Prevents duplicate patient accounts
- `appointment.cal_booking_id`: Links to unique Cal.com booking

## Query Patterns

### Common Queries

**Get doctor's appointments for a specific date**:
```sql
SELECT a.*, p.first_name, p.last_name, p.email
FROM appointment a
JOIN patient p ON a.patient_id = p.id
WHERE a.doctor_id = ?
  AND DATE(a.start_time) = ?
  AND a.status = 'SCHEDULED'
ORDER BY a.start_time;
```

**Find available doctors in a clinic**:
```sql
SELECT d.*
FROM doctor d
WHERE d.clinic_id = ?
  AND d.active = true
ORDER BY d.last_name, d.first_name;
```

**Get patient's appointment history**:
```sql
SELECT a.*, d.first_name, d.last_name, d.specialty
FROM appointment a
JOIN doctor d ON a.doctor_id = d.id
WHERE a.patient_id = ?
ORDER BY a.start_time DESC;
```

**Find appointment by Cal.com booking ID**:
```sql
SELECT a.*, d.*, p.*
FROM appointment a
JOIN doctor d ON a.doctor_id = d.id
JOIN patient p ON a.patient_id = p.id
WHERE a.cal_booking_id = ?;
```

**Audit trail for an entity**:
```sql
SELECT *
FROM audit_log
WHERE entity_type = 'Appointment'
  AND entity_id = ?
ORDER BY created_at DESC;
```

## Performance Considerations

### Indexes

All frequently queried columns have indexes:
- Foreign keys (for joins)
- Unique constraints (for lookups)
- Date/time columns (for range queries)
- Status columns (for filtering)

### Query Optimization

1. **Use composite indexes** for common query patterns:
   - `(doctor_id, start_time)` for doctor's schedule
   - `(patient_id, start_time)` for patient history

2. **Avoid N+1 queries**: Use JPA fetch joins
   ```java
   @Query("SELECT a FROM Appointment a " +
          "JOIN FETCH a.doctor " +
          "JOIN FETCH a.patient " +
          "WHERE a.doctorId = :doctorId")
   List<Appointment> findByDoctorWithDetails(@Param("doctorId") Long doctorId);
   ```

3. **Pagination**: Use `LIMIT` and `OFFSET` for large result sets

4. **Caching**: Cache frequently accessed, rarely changed data (clinics, doctors)

## Data Integrity

### Constraints

- **NOT NULL**: Required fields cannot be null
- **UNIQUE**: Prevents duplicate values
- **FOREIGN KEY**: Ensures referential integrity
- **CHECK**: Validates data (e.g., `end_time > start_time`)

### Triggers

Consider adding triggers for:
- Automatic `updated_at` timestamp updates
- Audit log generation
- Constraint validation

### Transactions

All multi-step operations use transactions:
```java
@Transactional
public Appointment createAppointment(CreateAppointmentRequest request) {
    // All operations in single transaction
    Appointment appointment = appointmentRepository.save(appointment);
    auditService.logOperation("CREATE_APPOINTMENT", appointment.getId());
    return appointment;
}
```

## Backup and Recovery

### Backup Strategy

1. **Daily full backups**: Complete database dump
2. **Continuous WAL archiving**: Point-in-time recovery
3. **Retention**: Keep 30 days of backups

### Backup Commands

```bash
# Full backup
pg_dump -h localhost -U citamedica citamedica > backup_$(date +%Y%m%d).sql

# Restore
psql -h localhost -U citamedica citamedica < backup_20251027.sql
```

## Security

### Data Protection

- **Encryption at rest**: Database files encrypted
- **Encryption in transit**: SSL/TLS for connections
- **Access control**: Role-based database permissions
- **Audit logging**: All access logged

### Sensitive Data

- Patient information (PII)
- Medical records
- Insurance details
- Contact information

### GDPR Compliance

- **Right to access**: Patients can request their data
- **Right to erasure**: Patients can request deletion
- **Consent tracking**: All consents recorded in `consent` table
- **Audit trail**: All operations logged in `audit_log` table

## Conclusion

The CitaMedica data model provides:
- **Scalable structure** for multi-clinic operations
- **Flexible relationships** between entities
- **Complete audit trail** for compliance
- **Integration support** with Cal.com
- **GDPR compliance** through consent tracking

The schema is designed to be maintainable, performant, and compliant with healthcare data regulations.