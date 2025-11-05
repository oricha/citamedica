# Hexagonal Architecture - CitaMedica Backend

## Overview

CitaMedica backend follows the **Hexagonal Architecture** (also known as Ports and Adapters) pattern, which provides a clean separation between business logic and external concerns.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          External World                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │   REST API   │  │   Webhooks   │  │   Scheduler  │             │
│  │   Clients    │  │   (Cal.com)  │  │   (Cron)     │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
└─────────┼──────────────────┼──────────────────┼────────────────────┘
          │                  │                  │
          │ HTTP             │ HTTP             │ Time-based
          │                  │                  │
┌─────────▼──────────────────▼──────────────────▼────────────────────┐
│                      Adapters (Input Ports)                         │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              api/v1 (REST Controllers)                        │  │
│  │  • PatientController      • DoctorController                 │  │
│  │  • AppointmentController  • AuthController                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              api/webhook (Webhook Controllers)                │  │
│  │  • CalWebhookController                                       │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              config (Cross-cutting Concerns)                  │  │
│  │  • SecurityConfig         • GlobalExceptionHandler           │  │
│  │  • JwtAuthenticationFilter • CorrelationIdFilter             │  │
│  │  • AuditAspect                                                │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              │ DTOs
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                    Application Layer (Use Cases)                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                      Service Layer                            │  │
│  │  • PatientService         • DoctorService                    │  │
│  │  • AppointmentService     • NotificationService              │  │
│  │  • AuditService           • SeedDataService                  │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  Business Logic:                                                     │
│  • Create/Update/Query operations                                   │
│  • Business rules validation                                        │
│  • Orchestration of domain operations                               │
│  • Transaction management                                           │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              │ Domain Objects
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                        Domain Layer (Core)                           │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    Domain Models                              │  │
│  │  • Patient            • Doctor           • Appointment        │  │
│  │  • Clinic             • Consent          • AuditLog           │  │
│  │  • AppointmentStatus  • ConsentType                           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                Repository Interfaces (Output Ports)           │  │
│  │  • PatientRepository      • DoctorRepository                 │  │
│  │  • AppointmentRepository  • ClinicRepository                 │  │
│  │  • ConsentRepository      • AuditLogRepository               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  Domain Rules:                                                       │
│  • Entity relationships and constraints                             │
│  • Business invariants                                              │
│  • Domain events                                                    │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              │ JPA Entities
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                   Infrastructure Layer (Adapters)                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              Persistence (Output Adapters)                    │  │
│  │  • Spring Data JPA Repositories                              │  │
│  │  • Flyway Migrations                                          │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │           External Integrations (Output Adapters)             │  │
│  │  • CalcomClient           • CalcomWebhookHandler             │  │
│  │  • CalcomSignatureValidator                                   │  │
│  │  • NotificationAdapter    • EmailNotification                │  │
│  │  • SMSNotification                                            │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                        External Systems                              │
│  ┌──────────────┐  ┌──────────────┐                               │
│  │  PostgreSQL  │  │   Cal.com    │                               │
│  │   Database   │  │     API      │                               │
│  └──────────────┘  └──────────────┘                               │
└─────────────────────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### 1. Domain Layer (Core)

**Location**: `com.citamedica.backend.domain`

**Purpose**: Contains the core business logic and domain models. This layer is independent of any framework or external system.

**Components**:
- **Entities**: Rich domain models with business logic
  - `Patient`, `Doctor`, `Appointment`, `Clinic`, `Consent`, `AuditLog`
- **Value Objects**: Immutable objects representing domain concepts
  - `AppointmentStatus`, `ConsentType`
- **Repository Interfaces**: Define contracts for data access (ports)
  - `PatientRepository`, `DoctorRepository`, `AppointmentRepository`, etc.

**Rules**:
- ✅ Can depend on: Nothing (pure Java)
- ❌ Cannot depend on: Application, Infrastructure, or Adapter layers
- ✅ Contains: Business rules, domain logic, entity relationships
- ❌ Does not contain: Framework code, database details, HTTP concerns

### 2. Application Layer (Use Cases)

**Location**: `com.citamedica.backend.service`

**Purpose**: Orchestrates domain operations and implements use cases. Contains application-specific business logic.

**Components**:
- **Services**: Implement use cases and orchestrate domain operations
  - `PatientService`, `DoctorService`, `AppointmentService`
  - `NotificationService`, `AuditService`, `SeedDataService`

**Responsibilities**:
- Coordinate domain objects to fulfill use cases
- Manage transactions
- Implement application-specific business rules
- Call external services through ports

**Rules**:
- ✅ Can depend on: Domain layer
- ❌ Cannot depend on: Infrastructure or Adapter layers directly
- ✅ Uses: Repository interfaces (ports) defined in domain
- ✅ Calls: External service interfaces (ports)

### 3. Adapter Layer (Input/Output)

**Location**: `com.citamedica.backend.api`, `com.citamedica.backend.integration`

**Purpose**: Adapts external requests/responses to domain operations and vice versa.

#### Input Adapters (Driving Side)

**Location**: `com.citamedica.backend.api`

**Components**:
- **REST Controllers**: Handle HTTP requests
  - `PatientController`, `DoctorController`, `AppointmentController`, `AuthController`
- **Webhook Controllers**: Handle external webhooks
  - `CalWebhookController`
- **DTOs**: Data Transfer Objects for API contracts
  - Request/Response objects in `api/v1/dto`

**Responsibilities**:
- Receive external requests (HTTP, webhooks)
- Validate input data
- Convert DTOs to domain objects
- Call application services
- Convert domain objects to DTOs
- Handle HTTP-specific concerns (status codes, headers)

#### Output Adapters (Driven Side)

**Location**: `com.citamedica.backend.integration`

**Components**:
- **External Service Clients**: Integrate with external systems
  - `CalcomClient`, `CalcomWebhookHandler`, `CalcomSignatureValidator`
- **Notification Adapters**: Send notifications
  - `NotificationAdapter`, `EmailNotification`, `SMSNotification`

**Responsibilities**:
- Implement repository interfaces (via Spring Data JPA)
- Call external APIs
- Handle external service protocols
- Convert between domain and external formats

### 4. Infrastructure Layer

**Location**: `com.citamedica.backend.config`, database migrations

**Purpose**: Provides technical capabilities and framework configuration.

**Components**:
- **Configuration**: Spring Boot configuration classes
  - `SecurityConfig`, `RestTemplateConfig`
- **Security**: Authentication and authorization
  - `JwtAuthenticationFilter`, `JwtTokenProvider`, `UserPrincipal`
- **Cross-cutting Concerns**: Aspects and filters
  - `AuditAspect`, `CorrelationIdFilter`, `GlobalExceptionHandler`
- **Database Migrations**: Flyway scripts
  - `src/main/resources/db/migration/V*.sql`

**Responsibilities**:
- Framework configuration
- Security setup
- Database schema management
- Logging and monitoring
- Exception handling

## Dependency Rules

The key principle of Hexagonal Architecture is the **Dependency Rule**:

```
External → Adapters → Application → Domain
```

**Dependencies flow inward**:
- Outer layers can depend on inner layers
- Inner layers cannot depend on outer layers
- Domain layer has no dependencies (pure business logic)

### Example: Creating an Appointment

```
1. REST Client
   ↓ HTTP POST /api/v1/appointments
2. AppointmentController (Input Adapter)
   ↓ Converts DTO to domain object
3. AppointmentService (Application)
   ↓ Orchestrates business logic
4. Appointment (Domain Entity)
   ↓ Business rules validation
5. AppointmentRepository (Domain Interface)
   ↓ Implemented by Spring Data JPA
6. PostgreSQL (Infrastructure)
```

## Benefits

### 1. **Testability**
- Domain logic can be tested without frameworks
- Services can be tested with mock repositories
- Controllers can be tested with mock services

### 2. **Flexibility**
- Easy to swap implementations (e.g., change database)
- Can add new adapters without changing core logic
- Framework-independent business logic

### 3. **Maintainability**
- Clear separation of concerns
- Easy to locate and modify code
- Reduced coupling between layers

### 4. **Business Focus**
- Domain layer reflects business concepts
- Business rules are explicit and centralized
- Technical details don't pollute business logic

## Ports and Adapters

### Input Ports (Driving)
- **REST API**: Controllers receive HTTP requests
- **Webhooks**: Controllers receive webhook events
- **Schedulers**: Cron jobs trigger operations

### Output Ports (Driven)
- **Repositories**: Data persistence interfaces
- **External APIs**: Cal.com integration
- **Notifications**: Email/SMS sending

### Port Interfaces

```java
// Output Port (defined in domain)
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdAndStartTimeBetween(
        Long doctorId, 
        LocalDateTime start, 
        LocalDateTime end
    );
}

// Adapter (implemented in infrastructure)
// Spring Data JPA automatically implements this interface
```

## Package Structure

```
com.citamedica.backend/
├── api/                          # Input Adapters
│   ├── v1/                       # REST API v1
│   │   ├── AppointmentController.java
│   │   ├── DoctorController.java
│   │   ├── PatientController.java
│   │   ├── AuthController.java
│   │   └── dto/                  # Data Transfer Objects
│   └── webhook/                  # Webhook endpoints
│       └── CalWebhookController.java
│
├── config/                       # Infrastructure
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── GlobalExceptionHandler.java
│   ├── CorrelationIdFilter.java
│   └── AuditAspect.java
│
├── domain/                       # Domain Layer (Core)
│   ├── model/                    # Entities and Value Objects
│   │   ├── Appointment.java
│   │   ├── Doctor.java
│   │   ├── Patient.java
│   │   ├── Clinic.java
│   │   ├── Consent.java
│   │   ├── AuditLog.java
│   │   ├── AppointmentStatus.java
│   │   └── ConsentType.java
│   └── repository/               # Repository Interfaces (Output Ports)
│       ├── AppointmentRepository.java
│       ├── DoctorRepository.java
│       ├── PatientRepository.java
│       ├── ClinicRepository.java
│       ├── ConsentRepository.java
│       └── AuditLogRepository.java
│
├── service/                      # Application Layer
│   ├── AppointmentService.java
│   ├── DoctorService.java
│   ├── PatientService.java
│   ├── ClinicService.java
│   ├── NotificationService.java
│   ├── AuditService.java
│   └── SeedDataService.java
│
└── integration/                  # Output Adapters
    ├── calcom/                   # Cal.com integration
    │   ├── CalcomClient.java
    │   ├── CalcomWebhookHandler.java
    │   ├── CalcomSignatureValidator.java
    │   ├── CalcomApiException.java
    │   └── WebhookEvent.java
    └── notification/             # Notification adapters
        ├── NotificationPort.java
        ├── NotificationAdapter.java
        ├── EmailNotification.java
        └── SMSNotification.java
```

## Testing Strategy

### Unit Tests
- **Domain Layer**: Test entities and business rules in isolation
- **Application Layer**: Test services with mocked repositories
- **Adapters**: Test controllers with mocked services

### Integration Tests
- **Repository Tests**: Test database operations with TestContainers
- **API Tests**: Test REST endpoints with MockMvc
- **Webhook Tests**: Test webhook processing end-to-end

### Example Test Structure

```java
// Domain Layer Test (Pure Java)
@Test
void appointment_shouldNotAllowOverlappingTimes() {
    Appointment existing = new Appointment(/* ... */);
    Appointment newAppointment = new Appointment(/* overlapping time */);
    
    assertThrows(BusinessException.class, () -> {
        existing.validateNoOverlap(newAppointment);
    });
}

// Application Layer Test (Mocked Dependencies)
@Test
void createAppointment_shouldSaveAndNotify() {
    // Given
    when(appointmentRepository.save(any())).thenReturn(appointment);
    
    // When
    service.createAppointment(request);
    
    // Then
    verify(appointmentRepository).save(any());
    verify(notificationService).sendConfirmation(any());
}

// Adapter Layer Test (Integration)
@Test
void createAppointment_shouldReturn201() throws Exception {
    mockMvc.perform(post("/api/v1/appointments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
}
```

## Best Practices

### 1. Keep Domain Pure
- No framework annotations in domain entities (except JPA when necessary)
- No infrastructure concerns in domain logic
- Business rules belong in domain layer

### 2. Use Interfaces for Ports
- Define repository interfaces in domain layer
- Define external service interfaces as ports
- Implement in infrastructure/adapter layers

### 3. DTOs for API Boundaries
- Never expose domain entities directly via API
- Use DTOs to decouple API contracts from domain
- Convert between DTOs and domain objects in controllers

### 4. Dependency Injection
- Use constructor injection for required dependencies
- Inject interfaces, not implementations
- Keep constructors simple

### 5. Transaction Management
- Manage transactions at service layer
- Use `@Transactional` on service methods
- Keep transactions as short as possible

## Common Patterns

### 1. Repository Pattern
```java
// Domain defines the interface
public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByDoctorAndDate(Long doctorId, LocalDate date);
}

// Spring Data JPA implements it automatically
```

### 2. Service Pattern
```java
@Service
@Transactional
public class AppointmentService {
    private final AppointmentRepository repository;
    private final NotificationService notificationService;
    
    public Appointment createAppointment(CreateAppointmentRequest request) {
        // 1. Convert DTO to domain
        Appointment appointment = request.toDomain();
        
        // 2. Apply business rules
        appointment.validate();
        
        // 3. Save
        appointment = repository.save(appointment);
        
        // 4. Trigger side effects
        notificationService.sendConfirmation(appointment);
        
        return appointment;
    }
}
```

### 3. Adapter Pattern
```java
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final AppointmentService service;
    
    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
        @RequestBody @Valid CreateAppointmentRequest request
    ) {
        // Adapter converts between HTTP and domain
        Appointment appointment = service.createAppointment(request);
        AppointmentResponse response = AppointmentResponse.from(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

## Conclusion

Hexagonal Architecture provides CitaMedica with:
- **Clean separation** between business logic and technical concerns
- **Testable** code at all layers
- **Flexible** design that can adapt to changing requirements
- **Maintainable** codebase with clear boundaries

The architecture ensures that business rules remain at the center, protected from external changes, while allowing easy integration with external systems through well-defined ports and adapters.