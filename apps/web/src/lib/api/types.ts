// API Types matching backend DTOs

export interface DoctorResponse {
  id: number;
  fullName: string;
  specialty: string;
  licenseNumber: string;
  email: string;
  phone: string;
  active: boolean;
  clinicId: number;
}

export interface PatientResponse {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  birthDate: string;
  insurancePlan?: string;
}

export interface AppointmentResponse {
  id: number;
  doctorId: number;
  patientId: number;
  type: string;
  startAt: string;
  endAt: string;
  status: 'SCHEDULED' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  calBookingId?: string;
  notes?: string;
  createdAt: string;
}

export interface CreatePatientRequest {
  fullName: string;
  email: string;
  phone: string;
  birthDate: string;
  insurancePlan?: string;
}

export interface CreateAppointmentRequest {
  doctorId: number;
  patientId: number;
  type: string;
  startAt: string;
  endAt: string;
  calBookingId?: string;
  notes?: string;
}

export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  timestamp: string;
}