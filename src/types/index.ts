// Application-specific types

export interface Clinic {
  id: string;
  slug: string;
  name: string;
  description?: string;
  logo?: string;
  calcomTeamId: string;
  adminUserId: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface Doctor {
  id: string;
  slug: string;
  name: string;
  specialty: string;
  bio?: string;
  avatar?: string;
  calcomUserId: string;
  clinicId?: string;
  isIndependent: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface Appointment {
  id: string;
  calcomBookingId: string;
  doctorId: string;
  patientName: string;
  patientPhone: string;
  patientEmail?: string;
  startTime: Date;
  endTime: Date;
  status: 'confirmed' | 'cancelled' | 'completed';
  eventTypeId: string;
  createdAt: Date;
}

// Form types
export interface PatientBookingData {
  name: string;
  phone: string;
  email?: string;
  notes?: string;
}

export interface DoctorRegistrationData {
  name: string;
  email: string;
  specialty: string;
  bio?: string;
  avatar?: string;
}

export interface ClinicRegistrationData {
  name: string;
  slug: string;
  description?: string;
  adminEmail: string;
  adminName: string;
}

// Dashboard component props
export interface ClinicPageProps {
  clinic: Clinic;
  doctors: Doctor[];
}

export interface DoctorCardProps {
  doctor: Doctor;
  onSelect: (doctor: Doctor) => void;
}

export interface CalendarBookingProps {
  doctorId: string;
  eventTypeId: string;
  availability: Array<{
    start: string;
    end: string;
    available: boolean;
  }>;
  onSlotSelect: (slot: { start: string; end: string }) => void;
}

export interface BookingFormProps {
  selectedSlot: { start: string; end: string };
  doctorInfo: Doctor;
  onSubmit: (data: PatientBookingData) => Promise<void>;
}

export interface DoctorDashboardProps {
  doctor: Doctor;
  upcomingAppointments: Appointment[];
  todayStats: {
    totalAppointments: number;
    completedAppointments: number;
    nextAppointment?: Appointment;
  };
}

export interface ClinicDashboardProps {
  clinic: Clinic;
  doctors: Doctor[];
  allAppointments: Appointment[];
  clinicStats: {
    totalDoctors: number;
    totalAppointments: number;
    todayAppointments: number;
  };
}

export interface SystemAdminDashboardProps {
  clinics: Clinic[];
  pendingRegistrations: Array<{
    id: string;
    type: 'clinic' | 'doctor';
    name: string;
    email: string;
    submittedAt: Date;
  }>;
  systemStats: {
    totalClinics: number;
    totalDoctors: number;
    totalAppointments: number;
  };
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

// User roles and permissions
export type UserRole = 'patient' | 'doctor' | 'clinic_admin' | 'system_admin';

export interface UserPermissions {
  canViewAppointments: boolean;
  canManageDoctors: boolean;
  canManageClinic: boolean;
  canManageSystem: boolean;
  canBookAppointments: boolean;
}

export const ROLE_PERMISSIONS: Record<UserRole, UserPermissions> = {
  patient: {
    canViewAppointments: false,
    canManageDoctors: false,
    canManageClinic: false,
    canManageSystem: false,
    canBookAppointments: true,
  },
  doctor: {
    canViewAppointments: true,
    canManageDoctors: false,
    canManageClinic: false,
    canManageSystem: false,
    canBookAppointments: false,
  },
  clinic_admin: {
    canViewAppointments: true,
    canManageDoctors: true,
    canManageClinic: true,
    canManageSystem: false,
    canBookAppointments: false,
  },
  system_admin: {
    canViewAppointments: true,
    canManageDoctors: true,
    canManageClinic: true,
    canManageSystem: true,
    canBookAppointments: false,
  },
};