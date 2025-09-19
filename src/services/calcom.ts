import { CalcomUser, EventType, AvailabilitySlot, Booking, Team, TeamMember, CreateBookingRequest, AddMemberRequest, BookingFilters, CalcomApiError, CreateUserRequest } from '../types/calcom';
import { ErrorHandler, RetryMechanism, CircuitBreaker, UserFriendlyError } from '../utils/errorHandler';

export class CalcomApiService {
  private baseUrl: string;
  private apiKey: string;
  private circuitBreaker: CircuitBreaker;

  constructor(baseUrl: string, apiKey: string) {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.circuitBreaker = new CircuitBreaker();
  }

  private async request(endpoint: string, options: RequestInit = {}): Promise<any> {
    const operation = async () => {
      const url = `${this.baseUrl}${endpoint}`;
      const response = await fetch(url, {
        ...options,
        headers: {
          'Authorization': `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json',
          ...options.headers,
        },
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const error: CalcomApiError = {
          message: errorData.message || response.statusText,
          status: response.status,
          data: errorData,
        };
        throw error;
      }

      return response.json();
    };

    return this.circuitBreaker.execute(() =>
      RetryMechanism.withRetry(operation, `Cal.com API ${endpoint}`)
    );
  }

  // Gestión de usuarios
  async getUser(userId: string): Promise<CalcomUser> {
    try {
      return await this.request(`/users/${userId}`);
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async getUsers(teamId?: string): Promise<CalcomUser[]> {
    try {
      const endpoint = teamId ? `/teams/${teamId}/members` : '/users';
      const response = await this.request(endpoint);
      return response.members || response.users || [];
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async createUser(userData: CreateUserRequest): Promise<CalcomUser> {
    try {
      // Note: The username is derived from the email address as per Cal.com's typical behavior.
      const username = userData.email.split('@')[0];
      return await this.request('/users', {
        method: 'POST',
        body: JSON.stringify({ ...userData, username }),
      });
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  // Tipos de eventos
  async getEventTypes(userId: string): Promise<EventType[]> {
    try {
      return await this.request(`/event-types?userId=${userId}`);
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  // Disponibilidad
  async getAvailability(
    userId: string,
    dateFrom: string,
    dateTo: string
  ): Promise<AvailabilitySlot[]> {
    try {
      const response = await this.request(
        `/availability?userId=${userId}&dateFrom=${dateFrom}&dateTo=${dateTo}`
      );
      return response.availability || [];
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  // Reservas
  async createBooking(data: CreateBookingRequest): Promise<Booking> {
    try {
      return await this.request('/bookings', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async getBookings(
    userId: string,
    filters?: BookingFilters
  ): Promise<Booking[]> {
    try {
      let endpoint = `/bookings?userId=${userId}`;
      if (filters) {
        const params = new URLSearchParams();
        if (filters.status) params.append('status', filters.status);
        if (filters.dateFrom) params.append('dateFrom', filters.dateFrom);
        if (filters.dateTo) params.append('dateTo', filters.dateTo);
        endpoint += `&${params.toString()}`;
      }
      const response = await this.request(endpoint);
      return response.bookings || [];
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  // Teams (Clínicas)
  async getTeam(teamId: string): Promise<Team> {
    try {
      return await this.request(`/teams/${teamId}`);
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async getTeamMembers(teamId: string): Promise<TeamMember[]> {
    try {
      const response = await this.request(`/teams/${teamId}/members`);
      return response.members || [];
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async addTeamMember(
    teamId: string,
    memberData: AddMemberRequest
  ): Promise<TeamMember> {
    try {
      return await this.request(`/teams/${teamId}/members`, {
        method: 'POST',
        body: JSON.stringify(memberData),
      });
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async createTeam(teamData: { name: string; slug: string }): Promise<Team> {
    try {
      return await this.request('/teams', {
        method: 'POST',
        body: JSON.stringify(teamData),
      });
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async updateTeam(teamId: string, teamData: Partial<Team>): Promise<Team> {
    try {
      return await this.request(`/teams/${teamId}`, {
        method: 'PATCH',
        body: JSON.stringify(teamData),
      });
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }

  async deleteTeam(teamId: string): Promise<void> {
    try {
      await this.request(`/teams/${teamId}`, {
        method: 'DELETE',
      });
    } catch (error) {
      throw ErrorHandler.handleCalcomError(error as CalcomApiError);
    }
  }
}
