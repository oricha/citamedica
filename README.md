# CitaMedica

CitaMedica es una plataforma SaaS para gestión de citas médicas que integra con Cal.com para scheduling y proporciona un backend robusto para operaciones clínicas.

## Arquitectura

Este proyecto está organizado como un monorepo con las siguientes aplicaciones:

- **apps/landing**: Landing page optimizada con Next.js 14 + Astro
- **apps/backend**: API backend en Java 21 + Spring Boot 3
- **infra**: Configuración de Docker Compose para desarrollo local

### Stack Tecnológico

- **Frontend**: Next.js 14, Astro, Tailwind CSS, shadcn/ui
- **Backend**: Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Flyway
- **Integraciones**: Cal.com self-hosted, Redis
- **Infraestructura**: Docker, Docker Compose

## Instalación y Desarrollo

### Prerrequisitos

- Docker y Docker Compose
- Java 21 (para desarrollo local del backend)
- Node.js 18+ y pnpm (para frontend)

### Levantar el Entorno de Desarrollo

1. Clona el repositorio:
   ```bash
   git clone <repo-url>
   cd citamedica
   ```

2. Inicia todos los servicios:
   ```bash
   make dev
   ```

3. Accede a las aplicaciones:
   - Landing: http://localhost:3001
   - Backend API: http://localhost:8080
   - Cal.com: http://localhost:3000

### Comandos Disponibles

- `make dev`: Levantar todos los servicios
- `make down`: Detener todos los servicios
- `make logs`: Ver logs en tiempo real
- `make clean`: Limpiar volúmenes y detener servicios
- `make seed`: Cargar datos de prueba
- `make test`: Ejecutar tests

## Estructura del Proyecto

```
citamedica/
├── apps/
│   ├── landing/          # Next.js 14 + Astro landing page
│   └── backend/          # Java 21 + Spring Boot 3 API
├── infra/
│   ├── docker/           # Dockerfiles
│   ├── docker-compose.yml
│   └── scripts/          # Scripts de setup y seed
├── docs/
│   ├── api/              # Documentación de API
│   └── architecture/     # Diagramas de arquitectura
└── Makefile
```

## API Endpoints

### Principales Endpoints

- `POST /api/v1/patients` - Crear paciente
- `GET /api/v1/doctors?clinic={id}` - Listar médicos de clínica
- `GET /api/v1/appointments?doctorId={id}&date=YYYY-MM-DD` - Agenda del día
- `POST /webhooks/cal` - Webhook de Cal.com

Ver documentación completa en `/docs/api`.

## Variables de Entorno

Crea un archivo `.env` en la raíz con:

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

## Contribución

1. Crea una rama para tu feature: `git checkout -b feature/nueva-funcionalidad`
2. Haz commit de tus cambios: `git commit -am 'Añadir nueva funcionalidad'`
3. Push a la rama: `git push origin feature/nueva-funcionalidad`
4. Abre un Pull Request

## Licencia

Este proyecto está bajo la licencia MIT.