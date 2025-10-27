# CitaMedica

CitaMedica is a SaaS platform for medical appointment management that integrates with Cal.com for scheduling and provides a robust backend for clinical operations.

## Architecture

This project is organized as a monorepo with the following applications:

- **apps/landing**: Landing page optimized with Next.js 14 + Astro
- **apps/backend**: Backend API in Java 21 + Spring Boot 3
- **infra**: Docker Compose configuration for local development

### Technology Stack

- **Frontend**: Next.js 14, Astro, Tailwind CSS, shadcn/ui
- **Backend**: Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Flyway
- **Integrations**: Cal.com self-hosted, Redis
- **Infrastructure**: Docker, Docker Compose

## Installation and Development

### Prerequisites

- Docker and Docker Compose
- Java 21 (for local backend development)
- Node.js 18+ and pnpm (for frontend)

### Setting Up the Development Environment

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd citamedica
   ```

2. Start all services:
   ```bash
   make dev
   ```

3. Access the applications:
   - Landing: http://localhost:3001
   - Backend API: http://localhost:8080
   - Cal.com: http://localhost:3000

### Available Commands

- `make dev`: Start all services
- `make down`: Stop all services
- `make logs`: View real-time logs
- `make clean`: Clean volumes and stop services
- `make seed`: Load test data
- `make test`: Run tests

## Project Structure

```
citamedica/
├── apps/
│   ├── landing/          # Next.js 14 + Astro landing page
│   └── backend/          # Java 21 + Spring Boot 3 API
├── infra/
│   ├── docker/           # Dockerfiles
│   ├── docker-compose.yml
│   └── scripts/          # Setup and seed scripts
├── docs/
│   ├── api/              # API documentation
│   └── architecture/     # Architecture diagrams
└── Makefile
```

## API Endpoints

### Main Endpoints

- `POST /api/v1/patients` - Create patient
- `GET /api/v1/doctors?clinic={id}` - List doctors of clinic
- `GET /api/v1/appointments?doctorId={id}&date=YYYY-MM-DD` - Daily schedule
- `POST /webhooks/cal` - Cal.com webhook

See complete documentation at `/docs/api`.

## Environment Variables

Create a `.env` file in the root with:

```bash
# Backend
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:postgresql://localhost:5432/citamedica
DATABASE_USERNAME=citamedica
DATABASE_PASSWORD=citamedica123

# Cal.com Integration
CALCOM_API_URL=http://localhost:3000/api/v2
CALCOM_API_KEY=your-api-key
CALCOM_WEBHOOK_SECRET=your-webhook-secret

# Frontend
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_CALCOM_URL=http://localhost:3000
```

## Contribution

1. Create a branch for your feature: `git checkout -b feature/new-feature`
2. Commit your changes: `git commit -am 'Add new functionality'`
3. Push to the branch: `git push origin feature/new-feature`
4. Open a Pull Request

## License

This project is under the MIT license.