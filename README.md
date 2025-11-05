# CitaMedica

CitaMedica is a comprehensive SaaS platform for medical appointment management that seamlessly integrates with Cal.com for scheduling and provides a robust backend for clinical operations.

## 📋 Table of Contents

- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Cal.com Webhook Setup](#calcom-webhook-setup)
- [Running the Application](#running-the-application)
- [End-to-End Testing Flow](#end-to-end-testing-flow)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Development](#development)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🏗️ Architecture

CitaMedica follows a **Hexagonal Architecture** (Ports and Adapters) pattern for the backend, ensuring clean separation of concerns and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend Layer                        │
│  ┌──────────────────┐              ┌──────────────────┐    │
│  │  Landing Page    │              │   Web App        │    │
│  │  (React + Vite)  │              │   (Future)       │    │
│  └──────────────────┘              └──────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      Backend API Layer                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              REST Controllers (Adapters)              │  │
│  │  • PatientController  • DoctorController             │  │
│  │  • AppointmentController  • AuthController           │  │
│  └──────────────────────────────────────────────────────┘  │
│                            │                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           Application Layer (Use Cases)               │  │
│  │  • CreatePatient  • GetDoctorsByClinic               │  │
│  │  • CreateAppointment  • ProcessBookingEvent          │  │
│  └──────────────────────────────────────────────────────┘  │
│                            │                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Domain Layer (Entities)                  │  │
│  │  • Patient  • Doctor  • Appointment                  │  │
│  │  • Clinic  • Consent  • AuditLog                     │  │
│  └──────────────────────────────────────────────────────┘  │
│                            │                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Infrastructure Layer (Adapters)               │  │
│  │  • JPA Repositories  • Cal.com Client                │  │
│  │  • Notification Service  • Security                  │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    External Services                         │
│  ┌──────────────┐  ┌──────────────┐                        │
│  │  PostgreSQL  │  │   Cal.com    │                        │
│  └──────────────┘  └──────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

- **Hexagonal Architecture**: Clean separation between business logic and infrastructure
- **Domain-Driven Design**: Rich domain models with business logic encapsulation
- **Event-Driven Integration**: Webhook-based integration with Cal.com
- **CQRS Pattern**: Separate read and write operations for better scalability
- **Audit Trail**: Complete audit logging for compliance and traceability

## 🛠️ Technology Stack

### Backend
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.2**: Enterprise-grade framework
- **Spring Data JPA**: ORM and database abstraction
- **PostgreSQL 15**: Robust relational database
- **Flyway**: Database migration management
- **Spring Security**: Authentication and authorization
- **JWT**: Stateless authentication tokens
- **Logback**: Structured JSON logging
- **Spring Boot Actuator**: Health checks and metrics

### Frontend
- **React 18**: Modern UI library
- **Vite**: Fast build tool and dev server
- **TypeScript**: Type-safe JavaScript
- **Tailwind CSS**: Utility-first CSS framework
- **shadcn/ui**: High-quality React components
- **React Router**: Client-side routing

### Infrastructure
- **Docker & Docker Compose**: Containerization and orchestration
- **Cal.com**: Self-hosted scheduling platform
- **Nginx**: Reverse proxy and static file serving

### Testing
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: Integration testing
- **Playwright**: End-to-end testing for frontend
- **TestContainers**: Database testing with containers

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Docker Desktop** (v20.10+) and **Docker Compose** (v2.0+)
  - [Install Docker Desktop](https://www.docker.com/products/docker-desktop)
  - Verify: `docker --version` and `docker-compose --version`

- **Java 21** (for local backend development)
  - [Install Java 21 JDK](https://adoptium.net/)
  - Verify: `java -version`

- **Node.js 18+** and **npm** (for frontend development)
  - [Install Node.js](https://nodejs.org/)
  - Verify: `node --version` and `npm --version`

- **Git** (for version control)
  - [Install Git](https://git-scm.com/)
  - Verify: `git --version`

- **Make** (optional, for convenience commands)
  - macOS/Linux: Usually pre-installed
  - Windows: Install via [Chocolatey](https://chocolatey.org/) or use Git Bash

### System Requirements

- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: At least 10GB free
- **OS**: macOS, Linux, or Windows 10/11 with WSL2

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/citamedica.git
cd citamedica
```

### 2. Configure Environment Variables

Copy the example environment file and update with your values:

```bash
cp .env.example .env
```

Edit the `.env` file with your preferred text editor. See [Configuration](#configuration) section for details.

### 3. Start All Services

Using Make (recommended):

```bash
make dev
```

Or using Docker Compose directly:

```bash
docker-compose up -d
```

The first startup will take 5-10 minutes as Docker downloads images and builds containers.

### 4. Verify Services

Check that all services are running:

```bash
make status
```

Or:

```bash
docker-compose ps
```

You should see all services in "Up" state:
- `postgres-clinic` - Main database
- `postgres-cal` - Cal.com database
- `backend-api` - Spring Boot API
- `landing` - React landing page
- `calcom` - Cal.com scheduling platform

### 5. Access the Applications

Once all services are running:

- **Landing Page**: http://localhost:3001
- **Backend API**: http://localhost:8080
- **Cal.com**: http://localhost:3000
- **API Health Check**: http://localhost:8080/actuator/health
- **API Metrics**: http://localhost:8080/actuator/metrics

## ⚙️ Configuration

### Environment Variables

The `.env` file contains all configuration. Here are the key variables:

#### Backend Configuration

```bash
# Database Connection
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-clinic:5432/citamedica
SPRING_DATASOURCE_USERNAME=citamedica
SPRING_DATASOURCE_PASSWORD=citamedica123

# JWT Authentication (⚠️ Change in production!)
JWT_SECRET=change-this-to-a-secure-random-string-in-production
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Cal.com Integration
CALCOM_API_URL=http://calcom:3000/api/v2
CALCOM_API_KEY=your-calcom-api-key-here
CALCOM_WEBHOOK_SECRET=your-webhook-secret-here

# Notifications (set to true to enable email/SMS)
NOTIFICATIONS_ENABLED=false

# CORS (add your frontend URLs)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

#### Cal.com Configuration

```bash
# Cal.com Database
DATABASE_URL=postgresql://calcom:calcom123@postgres-cal:5432/calcom

# NextAuth Secret (⚠️ Must be at least 32 characters!)
NEXTAUTH_SECRET=change-this-to-a-random-string-minimum-32-characters

# Cal.com Encryption Key (⚠️ Must be exactly 32 characters!)
CALENDSO_ENCRYPTION_KEY=change-this-to-exactly-32-characters

# Cal.com URLs
NEXT_PUBLIC_WEBAPP_URL=http://localhost:3000
NEXT_PUBLIC_API_V2_URL=http://localhost:3000/api/v2
```

#### Frontend Configuration

```bash
# API URLs
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_CALCOM_URL=http://localhost:3000
```

### Security Notes

⚠️ **IMPORTANT**: Before deploying to production:

1. Generate strong random secrets for `JWT_SECRET`, `NEXTAUTH_SECRET`, and `CALENDSO_ENCRYPTION_KEY`
2. Use environment-specific secrets (never commit secrets to Git)
3. Enable HTTPS for all services
4. Configure proper CORS origins
5. Use strong database passwords
6. Enable notifications with proper SMTP/SMS credentials

## 🔗 Cal.com Webhook Setup

To enable automatic appointment synchronization from Cal.com to CitaMedica:

### 1. Access Cal.com Admin

1. Navigate to http://localhost:3000
2. Create an admin account (first user becomes admin)
3. Log in to Cal.com

### 2. Generate API Key

1. Go to **Settings** → **Developer** → **API Keys**
2. Click **"New API Key"**
3. Give it a name (e.g., "CitaMedica Integration")
4. Copy the generated API key
5. Update `CALCOM_API_KEY` in your `.env` file

### 3. Configure Webhook

1. In Cal.com, go to **Settings** → **Developer** → **Webhooks**
2. Click **"New Webhook"**
3. Configure the webhook:
   - **Subscriber URL**: `http://backend-api:8080/webhooks/cal`
   - **Event Triggers**: Select all booking events:
     - ✅ `BOOKING_CREATED`
     - ✅ `BOOKING_RESCHEDULED`
     - ✅ `BOOKING_CANCELLED`
   - **Secret**: Generate a random string (e.g., using `openssl rand -hex 32`)
4. Save the webhook
5. Update `CALCOM_WEBHOOK_SECRET` in your `.env` file with the secret
6. Restart the backend: `docker-compose restart backend-api`

### 4. Test Webhook

1. Create a test booking in Cal.com
2. Check backend logs: `make backend-logs`
3. Look for webhook processing logs with correlation IDs
4. Verify the appointment was created: `curl http://localhost:8080/api/v1/appointments?doctorId=1&date=2025-10-27`

### Webhook Payload Example

Cal.com sends webhooks in this format:

```json
{
  "triggerEvent": "BOOKING_CREATED",
  "createdAt": "2025-10-27T10:30:00.000Z",
  "payload": {
    "id": 123,
    "uid": "abc123def456",
    "title": "Consulta Médica",
    "startTime": "2025-10-28T15:00:00.000Z",
    "endTime": "2025-10-28T15:30:00.000Z",
    "attendees": [
      {
        "email": "patient@example.com",
        "name": "John Doe",
        "timeZone": "Europe/Madrid"
      }
    ],
    "organizer": {
      "email": "doctor@clinic.com",
      "name": "Dr. Smith",
      "timeZone": "Europe/Madrid"
    }
  }
}
```

## 🏃 Running the Application

### Quick Start (Recommended)

```bash
# Start all services
make dev

# View logs
make logs

# Stop services
make down
```

### Individual Service Management

```bash
# View specific service logs
make backend-logs
make landing-logs
make calcom-logs

# Restart a specific service
docker-compose restart backend-api

# Rebuild and restart
make rebuild
```

### Load Test Data

```bash
# Load seed data (creates sample clinic, doctors, patients, appointments)
make seed

# Verify data was loaded
make backend-logs
```

The seed data includes:
- 1 clinic (Clínica Demo CitaMedica)
- 2 doctors (Cardiología and Medicina General)
- 3 patients
- 4 sample appointments

See [`apps/backend/SEED_DATA.md`](apps/backend/SEED_DATA.md) for details.

## 🧪 End-to-End Testing Flow

Follow this complete flow to verify the system works correctly:

### 1. Start the System

```bash
make dev
# Wait ~15 seconds for all services to be ready
```

### 2. Verify Services Health

```bash
make health
```

Expected output:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### 3. Load Test Data

```bash
make seed
```

### 4. Access Landing Page

1. Open http://localhost:3001 in your browser
2. Navigate to the **"Reservar Cita"** or **"Booking"** page
3. You should see the Cal.com embed widget

### 5. Create a Test Booking

1. In the Cal.com widget, select a doctor
2. Choose an available time slot
3. Fill in patient information:
   - Name: Test Patient
   - Email: test@example.com
   - Phone: +34 600 000 000
4. Confirm the booking

### 6. Verify Webhook Processing

```bash
# Watch backend logs in real-time
make backend-logs
```

Look for these log entries:
```
INFO  - Received webhook from Cal.com [correlationId: xxx-xxx-xxx]
INFO  - Webhook signature verified successfully
INFO  - Processing booking.created event for booking ID: 123
INFO  - Created appointment with cal_booking_id: 123
```

### 7. Query the Appointment via API

```bash
# Get today's appointments for doctor ID 1
curl "http://localhost:8080/api/v1/appointments?doctorId=1&date=$(date +%Y-%m-%d)"
```

Expected response:
```json
[
  {
    "id": 5,
    "calBookingId": "123",
    "doctorId": 1,
    "doctorName": "Dr. María García López",
    "patientId": 4,
    "patientName": "Test Patient",
    "appointmentType": "Consulta Médica",
    "startTime": "2025-10-28T15:00:00Z",
    "endTime": "2025-10-28T15:30:00Z",
    "status": "SCHEDULED",
    "notes": null
  }
]
```

### 8. Test Appointment Rescheduling

1. In Cal.com, reschedule the appointment to a different time
2. Check backend logs for `booking.rescheduled` event
3. Query the API again to verify the updated time

### 9. Test Appointment Cancellation

1. In Cal.com, cancel the appointment
2. Check backend logs for `booking.cancelled` event
3. Query the API to verify status changed to `CANCELLED`

### Complete Test Checklist

- [ ] All services start successfully (`make dev`)
- [ ] Health check returns UP (`make health`)
- [ ] Landing page loads at http://localhost:3001
- [ ] Cal.com embed widget displays correctly
- [ ] Can create a booking in Cal.com
- [ ] Webhook is received and processed (check logs)
- [ ] Appointment appears in API response
- [ ] Appointment has correct `cal_booking_id`
- [ ] Can reschedule appointment
- [ ] Can cancel appointment
- [ ] Correlation IDs appear in all logs
- [ ] Metrics endpoint works (`curl http://localhost:8080/actuator/metrics`)

## 📚 API Documentation

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### Endpoints

#### Patients

**Create Patient**
```http
POST /api/v1/patients
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+34 600 000 000",
  "dateOfBirth": "1990-01-15",
  "insuranceProvider": "Sanitas",
  "insuranceNumber": "SAN123456"
}
```

Response: `201 Created`
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+34 600 000 000",
  "dateOfBirth": "1990-01-15",
  "insuranceProvider": "Sanitas",
  "insuranceNumber": "SAN123456"
}
```

#### Doctors

**List Doctors by Clinic**
```http
GET /api/v1/doctors?clinicId=1
```

Response: `200 OK`
```json
[
  {
    "id": 1,
    "firstName": "María",
    "lastName": "García López",
    "specialty": "Cardiología",
    "email": "maria.garcia@clinicademo.com",
    "phone": "+34 612 345 678",
    "calUsername": "dr-maria-garcia",
    "active": true
  }
]
```

#### Appointments

**List Appointments by Doctor and Date**
```http
GET /api/v1/appointments?doctorId=1&date=2025-10-27
```

Response: `200 OK`
```json
[
  {
    "id": 1,
    "calBookingId": "abc123",
    "doctorId": 1,
    "doctorName": "Dr. María García López",
    "patientId": 1,
    "patientName": "Ana Rodríguez Sánchez",
    "appointmentType": "Consulta de Cardiología",
    "startTime": "2025-10-27T15:00:00Z",
    "endTime": "2025-10-27T15:30:00Z",
    "status": "SCHEDULED",
    "notes": "Primera consulta"
  }
]
```

**Create Appointment (Optional)**
```http
POST /api/v1/appointments
Content-Type: application/json

{
  "doctorId": 1,
  "patientId": 1,
  "appointmentType": "Consulta General",
  "startTime": "2025-10-28T10:00:00Z",
  "endTime": "2025-10-28T10:30:00Z",
  "notes": "Revisión anual"
}
```

#### Webhooks

**Cal.com Webhook Endpoint**
```http
POST /webhooks/cal
Content-Type: application/json
X-Cal-Signature-256: <hmac-signature>

{
  "triggerEvent": "BOOKING_CREATED",
  "payload": { ... }
}
```

This endpoint is called automatically by Cal.com. Do not call it manually.

### Error Responses

All errors follow the RFC 7807 Problem Details format:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Doctor with ID 999 not found",
  "instance": "/api/v1/doctors/999",
  "timestamp": "2025-10-27T10:30:00.000Z"
}
```

### Postman Collection

A complete Postman collection with all endpoints and examples is available at:
[`docs/api/CitaMedica.postman_collection.json`](docs/api/CitaMedica.postman_collection.json)

Import it into Postman to test all API endpoints easily.

## 📁 Project Structure

```
citamedica/
├── apps/
│   ├── backend/                          # Spring Boot Backend
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/citamedica/backend/
│   │   │   │   │   ├── api/              # REST Controllers (Adapters In)
│   │   │   │   │   │   ├── v1/           # API version 1
│   │   │   │   │   │   │   ├── AppointmentController.java
│   │   │   │   │   │   │   ├── DoctorController.java
│   │   │   │   │   │   │   ├── PatientController.java
│   │   │   │   │   │   │   └── dto/      # Data Transfer Objects
│   │   │   │   │   │   └── webhook/      # Webhook endpoints
│   │   │   │   │   ├── config/           # Spring Configuration
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   │   ├── domain/           # Domain Layer
│   │   │   │   │   │   ├── model/        # Entities
│   │   │   │   │   │   │   ├── Appointment.java
│   │   │   │   │   │   │   ├── Doctor.java
│   │   │   │   │   │   │   ├── Patient.java
│   │   │   │   │   │   │   └── ...
│   │   │   │   │   │   └── repository/   # JPA Repositories
│   │   │   │   │   ├── service/          # Application Services (Use Cases)
│   │   │   │   │   │   ├── AppointmentService.java
│   │   │   │   │   │   ├── DoctorService.java
│   │   │   │   │   │   └── ...
│   │   │   │   │   └── integration/      # External Integrations (Adapters Out)
│   │   │   │   │       ├── calcom/       # Cal.com integration
│   │   │   │   │       └── notification/ # Email/SMS notifications
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml   # Application configuration
│   │   │   │       ├── logback-spring.xml # Logging configuration
│   │   │   │       └── db/migration/     # Flyway migrations
│   │   │   └── test/                     # Tests
│   │   ├── build.gradle                  # Gradle build configuration
│   │   ├── Dockerfile                    # Docker image definition
│   │   └── SEED_DATA.md                  # Seed data documentation
│   │
│   └── landing/                          # React Landing Page
│       ├── src/
│       │   ├── components/               # React components
│       │   │   ├── Hero.tsx
│       │   │   ├── Features.tsx
│       │   │   ├── Pricing.tsx
│       │   │   └── ui/                   # shadcn/ui components
│       │   ├── pages/                    # Page components
│       │   │   ├── Index.tsx
│       │   │   ├── Booking.tsx
│       │   │   └── ...
│       │   └── lib/                      # Utilities
│       ├── e2e/                          # Playwright E2E tests
│       ├── package.json
│       ├── Dockerfile
│       └── README.md
│
├── docs/                                 # Documentation
│   ├── api/                              # API documentation
│   │   └── CitaMedica.postman_collection.json
│   └── architecture/                     # Architecture diagrams
│       ├── hexagonal-architecture.md
│       ├── webhook-flow.md
│       └── data-model.md
│
├── docker-compose.yml                    # Docker Compose configuration
├── Makefile                              # Convenience commands
├── .env.example                          # Environment variables template
├── .gitignore
├── LICENSE
└── README.md                             # This file
```

## 💻 Development

### Backend Development

#### Running Locally (without Docker)

1. Start PostgreSQL:
```bash
docker-compose up -d postgres-clinic
```

2. Run the backend:
```bash
cd apps/backend
./gradlew bootRun
```

#### Running Tests

```bash
# All tests
make test-backend

# Or directly with Gradle
cd apps/backend
./gradlew test

# With coverage report
./gradlew test jacocoTestReport
```

#### Database Migrations

```bash
# Create a new migration
cd apps/backend/src/main/resources/db/migration
# Create file: V8__description.sql

# Apply migrations
make db-migrate

# Or with Gradle
./gradlew flywayMigrate
```

#### Code Quality

```bash
# Format code
make format

# Run linters
make lint
```

### Frontend Development

#### Running Locally

```bash
cd apps/landing
npm install
npm run dev
```

The landing page will be available at http://localhost:5173

#### Running Tests

```bash
# Unit tests
npm test

# E2E tests with Playwright
npm run test:e2e

# E2E tests in UI mode
npm run test:e2e:ui
```

#### Building for Production

```bash
npm run build
npm run preview
```

### Database Access

```bash
# Connect to the database
make db-shell

# Inside psql:
\dt                    # List tables
\d appointment         # Describe appointment table
SELECT * FROM doctor;  # Query data
```

## 🧪 Testing

### Backend Tests

The backend includes comprehensive test coverage:

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test database interactions and API endpoints
- **Contract Tests**: Verify webhook payload handling

```bash
# Run all backend tests
make test-backend

# Run specific test class
cd apps/backend
./gradlew test --tests AppointmentServiceTest

# Run with coverage
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Frontend Tests

```bash
# Run E2E tests
cd apps/landing
npm run test:e2e

# Run specific test
npx playwright test booking-flow.spec.ts

# Debug mode
npx playwright test --debug
```

### Test Coverage Goals

- Backend: Minimum 70% code coverage
- Critical paths: 90%+ coverage
- All webhook handlers: 100% coverage

## 🔧 Troubleshooting

### Common Issues and Solutions

#### Services Won't Start

**Problem**: `docker-compose up` fails or services crash

**Solutions**:
1. Check Docker is running: `docker ps`
2. Check available disk space: `df -h`
3. Check available memory: `free -h` (Linux) or Activity Monitor (macOS)
4. Clean up Docker: `make clean` then `make dev`
5. Check logs: `make logs`

#### Backend Won't Connect to Database

**Problem**: Backend logs show connection errors

**Solutions**:
1. Verify PostgreSQL is running: `docker-compose ps postgres-clinic`
2. Check database credentials in `.env`
3. Wait longer (database takes ~10 seconds to initialize)
4. Check database logs: `make db-logs`
5. Try connecting manually: `make db-shell`

#### Cal.com Webhook Not Working

**Problem**: Bookings in Cal.com don't appear in backend

**Solutions**:
1. Verify webhook is configured in Cal.com settings
2. Check `CALCOM_WEBHOOK_SECRET` matches in both `.env` and Cal.com
3. Verify backend URL is accessible from Cal.com container
4. Check backend logs for webhook errors: `make backend-logs | grep webhook`
5. Test webhook signature validation:
```bash
curl -X POST http://localhost:8080/webhooks/cal \
  -H "Content-Type: application/json" \
  -H "X-Cal-Signature-256: test" \
  -d '{"triggerEvent":"BOOKING_CREATED","payload":{}}'
```

#### Port Already in Use

**Problem**: Error: "port is already allocated"

**Solutions**:
1. Check what's using the port:
```bash
# macOS/Linux
lsof -i :8080
lsof -i :3000
lsof -i :3001

# Windows
netstat -ano | findstr :8080
```
2. Stop the conflicting process or change ports in `docker-compose.yml`
3. Use different ports in `.env`:
```bash
BACKEND_PORT=8081
CALCOM_PORT=3002
LANDING_PORT=3003
```

#### Seed Data Not Loading

**Problem**: `make seed` doesn't create data

**Solutions**:
1. Check if data already exists: `make db-shell` then `SELECT * FROM clinic;`
2. Reset database: `make db-reset`
3. Check backend logs: `make backend-logs | grep -i seed`
4. Verify SeedDataService is enabled (not in test profile)

#### Frontend Can't Connect to Backend

**Problem**: API calls fail with CORS or connection errors

**Solutions**:
1. Verify backend is running: `curl http://localhost:8080/actuator/health`
2. Check `NEXT_PUBLIC_API_URL` in `.env`
3. Verify CORS configuration in backend `application.yml`
4. Check browser console for specific errors
5. Try accessing API directly: `curl http://localhost:8080/api/v1/doctors?clinicId=1`

#### Slow Performance

**Problem**: Services are slow or unresponsive

**Solutions**:
1. Check Docker resource limits (increase RAM/CPU in Docker Desktop)
2. Check disk space: `df -h`
3. Restart services: `make restart`
4. Check for memory leaks: `docker stats`
5. Optimize database queries (check slow query logs)

#### JWT Token Issues

**Problem**: Authentication fails or tokens expire immediately

**Solutions**:
1. Verify `JWT_SECRET` is set in `.env`
2. Check token expiration: `JWT_EXPIRATION=86400000` (24 hours)
3. Ensure secret is at least 32 characters
4. Clear browser cookies/localStorage
5. Generate new token via `/api/v1/auth/login`

### Getting Help

If you encounter issues not covered here:

1. Check the logs: `make logs`
2. Search existing GitHub issues
3. Create a new issue with:
   - Description of the problem
   - Steps to reproduce
   - Relevant log output
   - Your environment (OS, Docker version, etc.)

### Debug Mode

Enable debug logging for more detailed information:

```bash
# In .env
LOGGING_LEVEL_CITAMEDICA=DEBUG
SPRING_PROFILES_ACTIVE=dev

# Restart backend
docker-compose restart backend-api
```

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### Development Workflow

1. **Fork the repository**
```bash
git clone https://github.com/your-username/citamedica.git
cd citamedica
```

2. **Create a feature branch**
```bash
git checkout -b feature/your-feature-name
```

3. **Make your changes**
   - Follow the existing code style
   - Add tests for new features
   - Update documentation as needed

4. **Run tests**
```bash
make test
```

5. **Commit your changes**
```bash
git add .
git commit -m "feat: add new feature"
```

Follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `test:` Test changes
- `refactor:` Code refactoring
- `chore:` Maintenance tasks

6. **Push and create Pull Request**
```bash
git push origin feature/your-feature-name
```

### Code Style

- **Java**: Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **TypeScript/React**: Follow [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

### Testing Requirements

- All new features must include tests
- Maintain minimum 70% code coverage
- Integration tests for API endpoints
- E2E tests for critical user flows

### Pull Request Process

1. Update README.md with details of changes if needed
2. Update API documentation if endpoints changed
3. Ensure all tests pass
4. Request review from maintainers
5. Address review feedback
6. Squash commits before merging

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Cal.com](https://cal.com) for the excellent scheduling platform
- [Spring Boot](https://spring.io/projects/spring-boot) for the robust backend framework
- [shadcn/ui](https://ui.shadcn.com/) for beautiful React components
- All contributors who help improve CitaMedica

## 📞 Support

- **Documentation**: This README and files in `/docs`
- **Issues**: [GitHub Issues](https://github.com/your-org/citamedica/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/citamedica/discussions)
- **Email**: support@citamedica.com

---

**Built with ❤️ by the CitaMedica Team**