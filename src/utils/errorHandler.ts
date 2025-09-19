import { CalcomApiError } from '../types/calcom';

export class UserFriendlyError extends Error {
  constructor(message: string, public statusCode?: number) {
    super(message);
    this.name = 'UserFriendlyError';
  }
}

export class ErrorHandler {
  static handleCalcomError(error: CalcomApiError): UserFriendlyError {
    switch (error.status) {
      case 400:
        return new UserFriendlyError(
          'Los datos proporcionados no son válidos. Por favor verifica la información.'
        );
      case 401:
        return new UserFriendlyError(
          'Error de autenticación. Por favor verifica tus credenciales.'
        );
      case 403:
        return new UserFriendlyError(
          'No tienes permisos para realizar esta acción.'
        );
      case 404:
        return new UserFriendlyError(
          'El recurso solicitado no fue encontrado.'
        );
      case 409:
        return new UserFriendlyError(
          'Conflicto: El horario solicitado ya no está disponible.'
        );
      case 422:
        return new UserFriendlyError(
          'Los datos proporcionados no cumplen con los requisitos.'
        );
      case 429:
        return new UserFriendlyError(
          'Demasiadas solicitudes. Por favor espera un momento e intenta nuevamente.'
        );
      case 500:
        return new UserFriendlyError(
          'Error interno del servidor. Por favor intenta más tarde.'
        );
      case 502:
      case 503:
      case 504:
        return new UserFriendlyError(
          'Servicio temporalmente no disponible. Por favor intenta más tarde.'
        );
      default:
        return new UserFriendlyError(
          'Ha ocurrido un error inesperado. Por favor intenta nuevamente.'
        );
    }
  }

  static isRetryableError(status: number): boolean {
    // Retry on server errors, rate limiting, and network issues
    return status >= 500 || status === 429 || status === 408 || status === 0;
  }
}

export class RetryMechanism {
  private static readonly MAX_RETRIES = 3;
  private static readonly BASE_DELAY = 1000; // 1 second

  static async withRetry<T>(
    operation: () => Promise<T>,
    context: string = 'operation'
  ): Promise<T> {
    let lastError: Error;

    for (let attempt = 1; attempt <= this.MAX_RETRIES; attempt++) {
      try {
        return await operation();
      } catch (error) {
        lastError = error as Error;

        if (attempt === this.MAX_RETRIES) {
          throw lastError;
        }

        // Check if error is retryable
        const status = (error as any).status || 0;
        if (!ErrorHandler.isRetryableError(status)) {
          throw lastError;
        }

        // Exponential backoff with jitter
        const delay = this.BASE_DELAY * Math.pow(2, attempt - 1);
        const jitter = Math.random() * 1000; // Up to 1 second jitter
        const totalDelay = delay + jitter;

        console.warn(
          `${context}: Attempt ${attempt} failed, retrying in ${Math.round(totalDelay)}ms`,
          error
        );

        await new Promise(resolve => setTimeout(resolve, totalDelay));
      }
    }

    throw lastError!;
  }
}

export class CircuitBreaker {
  private failures = 0;
  private lastFailureTime = 0;
  private state: 'CLOSED' | 'OPEN' | 'HALF_OPEN' = 'CLOSED';

  private readonly FAILURE_THRESHOLD = 5;
  private readonly TIMEOUT = 60000; // 1 minute

  async execute<T>(operation: () => Promise<T>): Promise<T> {
    if (this.state === 'OPEN') {
      if (Date.now() - this.lastFailureTime > this.TIMEOUT) {
        this.state = 'HALF_OPEN';
      } else {
        throw new UserFriendlyError(
          'Servicio temporalmente no disponible. Por favor intenta más tarde.'
        );
      }
    }

    try {
      const result = await operation();
      this.onSuccess();
      return result;
    } catch (error) {
      this.onFailure();
      throw error;
    }
  }

  private onSuccess() {
    this.failures = 0;
    this.state = 'CLOSED';
  }

  private onFailure() {
    this.failures++;
    this.lastFailureTime = Date.now();

    if (this.failures >= this.FAILURE_THRESHOLD) {
      this.state = 'OPEN';
    }
  }
}