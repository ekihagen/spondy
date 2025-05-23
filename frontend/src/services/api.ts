import type { RegistrationForm, RegistrationRequest, RegistrationResponse } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

export const api = {
  async getForm(): Promise<RegistrationForm> {
    const response = await fetch(`${API_BASE_URL}/form`);
    if (!response.ok) {
      throw new Error('Kunne ikke hente registreringsskjema');
    }
    return response.json();
  },

  async submitRegistration(formId: string, data: RegistrationRequest): Promise<RegistrationResponse> {
    const response = await fetch(`${API_BASE_URL}/form/1/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    
    const result = await response.json();
    
    if (!response.ok) {
      throw new Error(result.message || 'Registrering feilet');
    }
    
    return result;
  },
}; 