import type { RegistrationForm, RegistrationRequest, RegistrationResponse } from '../types';

// Detect environment and set appropriate API base URL
const getApiBaseUrl = () => {
  // If running in development mode (Vite dev server)
  if (import.meta.env.DEV) {
    return 'http://localhost:8080/api';
  }
  // If running in production (served by nginx with proxy)
  return '/api';
};

const API_BASE_URL = getApiBaseUrl();

// Helper function to handle API errors with user-friendly messages
const handleApiError = async (response: Response): Promise<never> => {
  let errorMessage = 'An unexpected error occurred. Please try again later.';
  
  try {
    const errorData = await response.json();
    if (errorData.message) {
      errorMessage = errorData.message;
    }
    
    // Handle specific error types
    if (errorData.error === 'VALIDATION_ERROR' && errorData.fieldErrors) {
      const fieldErrorMessages = Object.values(errorData.fieldErrors).join(', ');
      errorMessage = `${errorData.message} ${fieldErrorMessages}`;
    }
  } catch {
    // If we can't parse the error response, use status-based messages
    switch (response.status) {
      case 400:
        errorMessage = 'Invalid request. Please check that all fields are correctly filled out.';
        break;
      case 404:
        errorMessage = 'Registration form not found.';
        break;
      case 500:
        errorMessage = 'Server error. Please try again later.';
        break;
      default:
        errorMessage = `Error ${response.status}: ${response.statusText}`;
    }
  }
  
  throw new Error(errorMessage);
};

export const api = {
  async getForm(): Promise<RegistrationForm> {
    try {
      const response = await fetch(`${API_BASE_URL}/form`);
      
      if (!response.ok) {
        await handleApiError(response);
      }
      
      const result = await response.json();
      
      if (!result.success) {
        throw new Error(result.message || 'Could not fetch registration form');
      }
      
      return result.data;
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Could not connect to server. Please check your internet connection.');
    }
  },

  async submitRegistration(formId: string, data: RegistrationRequest): Promise<RegistrationResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/form/${formId}/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
      
      const result = await response.json();
      
      if (!response.ok) {
        // Handle validation errors specially
        if (result.error === 'VALIDATION_ERROR' && result.fieldErrors) {
          const fieldErrorMessages = Object.entries(result.fieldErrors)
            .map(([field, message]) => `${field}: ${message}`)
            .join('\n');
          throw new Error(`${result.message}\n\n${fieldErrorMessages}`);
        }
        
        throw new Error(result.message || 'Registration failed');
      }
      
      if (!result.success) {
        throw new Error(result.message || 'Registration failed');
      }
      
      return {
        success: result.success,
        message: result.message,
        registrationId: result.registrationId,
        memberName: result.memberName
      };
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Could not connect to server. Please check your internet connection.');
    }
  },
}; 