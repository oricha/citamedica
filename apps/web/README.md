# CitaMedica Web App

Web application for managing medical appointments, now refactored to consume the backend REST API.

## Changes from Previous Version

### Removed
- **Prisma ORM**: Removed all Prisma dependencies and database schema
- **Local Database**: No longer uses SQLite or local database
- **Direct Database Access**: All data operations now go through the backend API

### Added
- **API Client**: New HTTP client in `src/lib/api/client.ts` for backend communication
- **Type Definitions**: TypeScript types matching backend DTOs in `src/lib/api/types.ts`
- **Error Handling**: Support for RFC 7807 Problem Details format from backend

## API Client Usage

```typescript
import { api } from '@/lib/api/client';

// Get all doctors
const doctors = await api.doctors.getAll();

// Get doctors by clinic
const clinicDoctors = await api.doctors.getAll(clinicId);

// Create a patient
const patient = await api.patients.create({
  fullName: 'John Doe',
  email: 'john@example.com',
  phone: '+34600000000',
  birthDate: '1990-01-01',
  insurancePlan: 'Basic'
});

// Get appointments
const appointments = await api.appointments.getByDoctorAndDate(
  doctorId,
  '2025-01-15'
);
```

## Environment Variables

Create a `.env.local` file:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## Development

```bash
npm install
npm run dev
```

The app will be available at http://localhost:3000

## Backend Integration

This app requires the CitaMedica backend to be running. See the main project README for instructions on starting the backend with Docker Compose.
