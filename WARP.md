# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

CitaMedica is a comprehensive SaaS platform for medical appointment management that integrates with Cal.com for scheduling. The system uses **Hexagonal Architecture** (Ports and Adapters) with **Domain-Driven Design** principles for clean separation of concerns.

### Architecture Pattern

The backend follows a strict layered architecture:
- **API Layer** (`api/v1/`, `api/webhook/`) - REST controllers and webhook handlers (Adapters In)
- **Application Layer** (`service/`) - Use cases and business logic orchestration
- **Domain Layer** (`domain/model/`, `domain/repository/`) - Core entities and repository interfaces
- **Infrastructure Layer** (`integration/`, `config/`) - External integrations and framework configuration (Adapters Out)

Key architectural principles:
- Domain models are rich entities with business logic
- Services orchestrate use cases and coordinate between layers
- Repositories are JPA-based and defined as interfaces in the domain layer
- External integrations (Cal.com, notifications) are isolated in the integration layer
- All webhook events from Cal.com are processed with HMAC signature validation
- Correlation IDs are used throughout for request tracing (via MDC)
- Audit logging tracks all state changes for compliance

## Development Commands

### Quick Start
```bash
# Start all services (backend, landing, web, Cal.com, databases)
make dev

# Check service health
make health

# View logs for all services
make logs
```

### Service-Specific Operations
```bash
# View logs for specific services
make backend-logs
make landing-logs
make calcom-logs

# Restart services
make restart
docker-compose restart backend-api
```

### Backend Development
```bash
# Run backend locally (requires PostgreSQL running)
cd apps/backend
./gradlew bootRun

# Run tests
make test-backend
cd apps/backend && ./gradlew test

# Run specific test class
cd apps/backend && ./gradlew test --tests AppointmentServiceTest

# Run with coverage report
cd apps/backend && ./gradlew test jacocoTestReport

# Build without tests
cd apps/backend && ./gradlew build -x test
```

### Frontend Development
```bash
# Landing page (React + Vite)
cd apps/landing
npm install
npm run dev          # Local dev at http://localhost:5173
npm run build
npm run test:e2e     # Playwright E2E tests
npm run test:e2e:ui  # Playwright UI mode

# Web app (Next.js)
cd apps/web
npm install
npm run dev          # Local dev at http://localhost:3000
npm run build
```

### Database Operations
```bash
# Access database shell
make db-shell

# Reset database (WARNING: deletes all data)
make db-reset

# Apply Flyway migrations
make db-migrate
cd apps/backend && ./gradlew flywayMigrate

# Load seed data (creates sample clinic, doctors, patients, appointments)
make seed
```

### Testing & Quality
```bash
# Run all tests
make test

# Backend tests only
make test-backend

# Frontend tests only
make test-frontend

# Format code (if configured)
make format

# Run linters (if configured)
make lint
```

### Docker Operations
```bash
# Stop all services
make down

# Clean volumes and stop services
make clean

# Rebuild all images
make rebuild

# Build specific service
docker-compose build backend-api
docker-compose build landing

# Check service status
make status
docker-compose ps
```

## Codebase Architecture

### Backend Package Structure
```
com.citamedica.backend/
├── api/                    # REST endpoints
│   ├── v1/                 # API v1 controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── AppointmentController.java
│   │   ├── DoctorController.java
│   │   ├── PatientController.java
│   │   └── AuthController.java
│   └── webhook/
│       └── CalWebhookController.java  # Cal.com webhook receiver
├── config/                 # Spring configuration
│   ├── SecurityConfig.java           # JWT auth, CORS, security
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── GlobalExceptionHandler.java   # RFC 7807 error responses
│   ├── AuditAspect.java             # AOP-based audit logging
│   └── CorrelationIdFilter.java     # Request tracing
├── domain/                 # Core business domain
│   ├── model/              # JPA entities (Appointment, Doctor, Patient, Clinic, etc.)
│   └── repository/         # Spring Data JPA repositories
├── service/                # Business logic use cases
│   ├── AppointmentService.java
│   ├── DoctorService.java
│   ├── PatientService.java
│   ├── AuditService.java
│   └── SeedDataService.java
├── integration/            # External system adapters
│   ├── calcom/             # Cal.com webhook handling
│   │   ├── CalcomWebhookHandler.java      # Main webhook processor
│   │   ├── CalcomSignatureValidator.java   # HMAC validation
│   │   └── WebhookEvent.java
│   └── notification/       # Email/SMS notifications
│       ├── NotificationAdapter.java
│       ├── NotificationPort.java
│       ├── EmailNotification.java
│       └── SMSNotification.java
└── util/                   # Utilities
```

### Database Migrations
Flyway migrations are in `apps/backend/src/main/resources/db/migration/`:
- `V1__create_clinic_table.sql`
- `V2__create_doctor_table.sql`
- `V3__create_patient_table.sql`
- `V4__create_appointment_table.sql`
- `V5__create_audit_log_table.sql`
- `V6__create_consent_table.sql`

When creating new migrations, follow the naming pattern: `V{n}__description.sql`

### Frontend Applications

**Landing Page** (`apps/landing/`) - React + Vite + TypeScript
- Uses shadcn/ui components with Tailwind CSS
- Embeds Cal.com booking widget
- Playwright E2E tests in `e2e/`

**Web App** (`apps/web/`) - Next.js + TypeScript
- Consumes backend REST API (no direct database access)
- API client in `src/lib/api/client.ts`
- Type definitions in `src/lib/api/types.ts`
- Uses NextAuth for authentication

## Key Development Patterns

### Cal.com Webhook Integration
Webhooks arrive at `/webhooks/cal` and are processed as follows:
1. **Signature validation** - HMAC SHA-256 signature verification using `CALCOM_WEBHOOK_SECRET`
2. **Event routing** - Handler dispatches to specific methods based on event type:
   - `BOOKING_CREATED` → Creates appointment and patient (if new)
   - `BOOKING_RESCHEDULED` → Updates appointment times
   - `BOOKING_CANCELLED` → Marks appointment as cancelled
3. **Transactional processing** - All webhook handlers are `@Transactional`
4. **Audit trail** - Every state change is logged via `AuditService`
5. **Correlation tracking** - Each request has a correlation ID in MDC for log tracing

### Testing Strategy
- **Unit tests** - Mock dependencies, test business logic (e.g., `AppointmentServiceTest`)
- **Integration tests** - Use H2 in-memory database for repository tests
- **Contract tests** - Verify webhook payload handling (`CalcomWebhookHandlerTest`)
- **E2E tests** - Playwright tests for frontend flows
- Test coverage goal: 70% overall, 90%+ for critical paths, 100% for webhook handlers

### Authentication & Security
- JWT-based authentication with `JWT_SECRET` env var
- Tokens expire based on `JWT_EXPIRATION` (default 24 hours)
- CORS configured via `CORS_ALLOWED_ORIGINS`
- All API endpoints except `/webhooks/cal`, `/actuator/*`, and auth endpoints require JWT
- Webhook endpoint validates HMAC signature instead of JWT

### Logging & Observability
- Structured JSON logging via Logback (configured in `logback-spring.xml`)
- Correlation IDs in all log entries (`MDC.get("correlationId")`)
- Spring Boot Actuator endpoints:
  - `/actuator/health` - Health checks
  - `/actuator/metrics` - Application metrics
  - `/actuator/prometheus` - Prometheus metrics export
- All exceptions handled by `GlobalExceptionHandler` with RFC 7807 Problem Details format

### Environment Configuration
Critical environment variables (see `.env.example`):
- **Database**: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- **Cal.com**: `CALCOM_API_URL`, `CALCOM_API_KEY`, `CALCOM_WEBHOOK_SECRET`
- **Security**: `JWT_SECRET`, `NEXTAUTH_SECRET`, `CALENDSO_ENCRYPTION_KEY`
- **Features**: `NOTIFICATIONS_ENABLED`, `SPRING_PROFILES_ACTIVE`

## Important Files

### Configuration
- `docker-compose.yml` - Multi-container orchestration (backend, landing, web, Cal.com, PostgreSQL)
- `Makefile` - Convenience commands for common operations
- `apps/backend/src/main/resources/application.yml` - Spring Boot configuration
- `.env.example` - Environment variable template

### Documentation
- `README.md` - Comprehensive project documentation
- `apps/backend/SEED_DATA.md` - Seed data documentation
- `docs/api/CitaMedica.postman_collection.json` - Postman API collection
- `docs/architecture/` - Architecture diagrams and design docs

### CI/CD
- `.github/workflows/docker-build-push.yml` - Docker image build and push workflow

## Docker Services

The `docker-compose.yml` defines these services:
- **postgres-clinic** - Main PostgreSQL database (port 5432)
- **backend-api** - Spring Boot API (port 8080)
- **landing** - React landing page (port 3001)
- **postgres-cal** - Cal.com PostgreSQL database (port 5433)
- **calcom** - Self-hosted Cal.com instance (port 3000)

All services are connected via `citamedica-network` bridge network.

## Common Development Workflows

### Adding a New API Endpoint
1. Create DTO in `api/v1/dto/`
2. Add controller method in appropriate controller (e.g., `AppointmentController`)
3. Implement service method in service layer (e.g., `AppointmentService`)
4. Update repository if needed in `domain/repository/`
5. Add unit tests for service
6. Add integration tests for controller
7. Update Postman collection in `docs/api/`

### Adding a New Database Table
1. Create migration file in `apps/backend/src/main/resources/db/migration/`
2. Name it `V{next_number}__description.sql`
3. Create corresponding entity in `domain/model/`
4. Create repository interface in `domain/repository/`
5. Run migrations: `make db-migrate` or restart backend
6. Test with integration tests

### Debugging Webhook Issues
1. Check webhook configuration in Cal.com settings
2. Verify `CALCOM_WEBHOOK_SECRET` matches in both `.env` and Cal.com
3. Watch backend logs: `make backend-logs | grep webhook`
4. Check for correlation IDs and `calBookingId` in logs
5. Verify signature validation is passing
6. Check that doctor email in Cal.com matches database

### Testing the Full Integration
1. Start all services: `make dev`
2. Load seed data: `make seed`
3. Access Cal.com at http://localhost:3000
4. Create a test booking
5. Check logs: `make backend-logs`
6. Query API: `curl "http://localhost:8080/api/v1/appointments?doctorId=1&date=$(date +%Y-%m-%d)"`

## Technology Stack

### Backend
- Java 21 with Spring Boot 3.5
- Spring Data JPA + PostgreSQL 16
- Flyway for database migrations
- Spring Security + JWT authentication
- Logback with structured JSON logging
- Spring Boot Actuator + Micrometer + Prometheus

### Frontend
- **Landing**: React 18 + Vite + TypeScript + Tailwind CSS + shadcn/ui
- **Web**: Next.js 15 + React 19 + TypeScript + NextAuth

### Infrastructure
- Docker + Docker Compose
- PostgreSQL 16
- Cal.com (self-hosted)
- GitHub Actions for CI/CD

### Testing
- JUnit 5 + Mockito for backend
- H2 for in-memory database tests
- Playwright for E2E frontend tests
