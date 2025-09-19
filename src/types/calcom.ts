// Cal.com API Types

export interface CalcomUser {
  id: number;
  username: string;
  name: string;
  email: string;
  avatar?: string;
  bio?: string;
  timeZone: string;
  weekStart: string;
  createdDate: string;
  verified: boolean;
}

export interface EventType {
  id: number;
  title: string;
  slug: string;
  description?: string;
  length: number; // in minutes
  userId: number;
  teamId?: number;
  schedulingType?: 'ROUND_ROBIN' | 'COLLECTIVE' | null;
  hidden: boolean;
  locations?: Array<{
    type: string;
    address?: string;
    link?: string;
  }>;
  customInputs?: Array<{
    id: number;
    label: string;
    type: string;
    required: boolean;
    placeholder?: string;
  }>;
}

export interface AvailabilitySlot {
  start: string;
  end: string;
  available: boolean;
}

export interface Booking {
  id: number;
  uid: string;
  userId: number;
  eventTypeId: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  status: 'ACCEPTED' | 'PENDING' | 'REJECTED' | 'CANCELLED';
  attendees: Array<{
    email: string;
    name: string;
    timeZone: string;
    locale?: string;
  }>;
  location?: string;
  createdAt: string;
  updatedAt: string;
  user: CalcomUser;
  eventType: EventType;
}

export interface CreateBookingRequest {
  eventTypeId: number;
  start: string;
  end?: string;
  responses: {
    name: string;
    email: string;
    location?: string;
    notes?: string;
    [key: string]: any; // for custom inputs
  };
  timeZone?: string;
  language?: string;
  metadata?: Record<string, any>;
}

export interface BookingFilters {
  status?: string;
  dateFrom?: string;
  dateTo?: string;
  attendeeEmail?: string;
}

export interface Team {
  id: number;
  name: string;
  slug: string;
  logo?: string;
  bio?: string;
  hideBranding: boolean;
  hideBookATeamMember: boolean;
  createdAt: string;
  members: TeamMember[];
  eventTypes: EventType[];
}

export interface TeamMember {
  id: number;
  userId: number;
  teamId: number;
  role: 'MEMBER' | 'ADMIN' | 'OWNER';
  accepted: boolean;
  user: CalcomUser;
}

export interface AddMemberRequest {
  userId: number;
  role?: 'MEMBER' | 'ADMIN' | 'OWNER';
}

export interface CreateUserRequest {
  email: string;
  name: string;
  username: string;
  password?: string; // Optional, some cal.com setups might not require it on creation
}

// Error types
export interface CalcomApiError {
  message: string;
  status: number;
  data?: any;
}