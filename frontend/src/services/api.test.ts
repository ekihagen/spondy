import { describe, it, expect, vi, beforeEach } from 'vitest'
import { api } from './api'

// Mock fetch globally
const mockFetch = vi.fn()
globalThis.fetch = mockFetch

describe('API Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getForm', () => {
    it('should fetch form data successfully', async () => {
      const mockFormData = {
        clubId: 'britsport',
        formId: 'B171388180BC457D9887AD92B6CCFC86',
        title: 'Coding camp summer 2025',
        description: 'Join our exciting coding camp this summer!',
        registrationOpens: '2024-12-16T00:00:00',
        memberTypes: [
          { id: '8FE4113D4E4020E0DCF887803A886981', name: 'Active Member' },
          { id: '4237C55C5CC3B4B082CBF2540612778E', name: 'Social Member' }
        ]
      }

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          success: true,
          data: mockFormData
        })
      })

      const result = await api.getForm()
      
      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/api/form')
      expect(result).toEqual(mockFormData)
    })

    it('should handle API errors with user-friendly messages', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        json: async () => ({
          success: false,
          message: 'Kunne ikke hente registreringsskjema. Prøv igjen senere.',
          error: 'FORM_FETCH_ERROR'
        })
      })

      await expect(api.getForm()).rejects.toThrow('Kunne ikke hente registreringsskjema. Prøv igjen senere.')
    })

    it('should handle network errors gracefully', async () => {
      mockFetch.mockRejectedValueOnce(new Error('Network error'))

      await expect(api.getForm()).rejects.toThrow('Network error')
    })
  })

  describe('submitRegistration', () => {
    const mockRegistrationData = {
      fullName: 'Test Testesen',
      email: 'test@example.com',
      phoneNumber: '12345678',
      birthDate: '15.06.1990',
      memberTypeId: '8FE4113D4E4020E0DCF887803A886981'
    }

    it('should submit registration successfully', async () => {
      const mockResponse = {
        success: true,
        message: 'Takk for din registrering! Du vil motta en bekreftelse på e-post.',
        registrationId: 1234567890,
        memberName: 'Test Testesen'
      }

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse
      })

      const result = await api.submitRegistration('test-form-id', mockRegistrationData)
      
      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/api/form/1/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(mockRegistrationData),
      })
      
      expect(result).toEqual(mockResponse)
    })

    it('should handle validation errors with detailed field messages', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: async () => ({
          success: false,
          message: 'Vennligst rett opp følgende feil:',
          error: 'VALIDATION_ERROR',
          fieldErrors: {
            fullName: 'Fullt navn er påkrevd',
            email: 'E-post må ha gyldig format'
          }
        })
      })

      await expect(api.submitRegistration('test-form-id', mockRegistrationData))
        .rejects.toThrow('Vennligst rett opp følgende feil:\n\nfullName: Fullt navn er påkrevd\nemail: E-post må ha gyldig format')
    })

    it('should handle server errors gracefully', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        json: async () => ({
          success: false,
          message: 'En uventet feil oppstod under registrering. Prøv igjen senere.',
          error: 'REGISTRATION_ERROR'
        })
      })

      await expect(api.submitRegistration('test-form-id', mockRegistrationData))
        .rejects.toThrow('En uventet feil oppstod under registrering. Prøv igjen senere.')
    })
  })

  describe('environment detection', () => {
    it('should use localhost in development environment', async () => {
      // Mock a successful response for this test
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ 
          success: true, 
          data: { test: 'data' } 
        })
      })
      
      // The current setup should use localhost:8080 for development
      expect(mockFetch).not.toHaveBeenCalled()
      
      // Test by calling the API
      await api.getForm()
      
      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/api/form')
    })
  })
}) 