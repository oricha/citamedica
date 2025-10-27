// API Client for CitaMedica Backend

import type {
  DoctorResponse,
  PatientResponse,
  AppointmentResponse,
  CreatePatientRequest,
  CreateAppointmentRequest,
  ProblemDetail,
} from './types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

class ApiError extends Error {
  constructor(
    public status: number,
    public problemDetail: ProblemDetail
  ) {
    super(problemDetail.detail);
    this.name = 'ApiError';
  }
}

async function fetchApi<T>(
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    const problemDetail: ProblemDetail = await response.json();
    throw new ApiError(response.status, problemDetail);
  }

  return response.json();
}

export const api = {
  // Doctors
  doctors: {
    getAll: (clinicId?: number) => {
      const params = clinicId ? `?clinic=${clinicId}` : '';
      return fetchApi<DoctorResponse[]>(`/api/v1/doctors${params}`);
    },
  },

  // Patients
  patients: {
    getAll: () => fetchApi<PatientResponse[]>('/api/v1/patients'),
    
    create: (data: CreatePatientRequest) =>
      fetchApi<PatientResponse>('/api/v1/patients', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
  },

  // Appointments
  appointments: {
    getByDoctorAndDate: (doctorId: number, date: string) =>
      fetchApi<AppointmentResponse[]>(
        `/api/v1/appointments?doctorId=${doctorId}&date=${date}`
      ),
    
    create: (data: CreateAppointmentRequest) =>
      fetchApi<AppointmentResponse>('/api/v1/appointments', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
  },
};

export { ApiError };
export type { ProblemDetail };