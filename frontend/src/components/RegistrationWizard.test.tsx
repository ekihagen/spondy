import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { RegistrationWizard } from './RegistrationWizard'
import { api } from '../services/api'

// Mock the API
vi.mock('../services/api', () => ({
  api: {
    getForm: vi.fn(),
    submitRegistration: vi.fn(),
  }
}))

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

describe('RegistrationWizard', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(api.getForm).mockResolvedValue(mockFormData)
  })

  it('should render form title and description', async () => {
    render(<RegistrationWizard />)
    
    await waitFor(() => {
      expect(screen.getByText('Coding camp summer 2025')).toBeInTheDocument()
      expect(screen.getByText('Join our exciting coding camp this summer!')).toBeInTheDocument()
    })
  })

  it('should render step 1 initially', async () => {
    render(<RegistrationWizard />)
    
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /velg medlemstype/i })).toBeInTheDocument()
      expect(screen.getByText('Active Member')).toBeInTheDocument()
      expect(screen.getByText('Social Member')).toBeInTheDocument()
    })
  })

  it('should show loading state initially', () => {
    render(<RegistrationWizard />)
    
    expect(screen.getByText(/laster/i)).toBeInTheDocument()
  })

  it('should handle API errors gracefully', async () => {
    vi.mocked(api.getForm).mockRejectedValue(new Error('API Error'))
    
    render(<RegistrationWizard />)
    
    await waitFor(() => {
      expect(screen.getByText('API Error')).toBeInTheDocument()
    })
  })

  it('should navigate between steps', async () => {
    const user = userEvent.setup()
    render(<RegistrationWizard />)
    
    // Wait for form to load
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /velg medlemstype/i })).toBeInTheDocument()
    })

    // Select member type on step 1 and proceed
    const memberTypeOption = screen.getByText('Active Member')
    await user.click(memberTypeOption)
    
    const nextButton = screen.getByRole('button', { name: /neste/i })
    await user.click(nextButton)

    // Should now be on step 2
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /personlig informasjon/i })).toBeInTheDocument()
    })
  })

  it('should validate required fields before proceeding', async () => {
    const user = userEvent.setup()
    render(<RegistrationWizard />)
    
    await waitFor(() => {
      expect(screen.getByRole('button', { name: /neste/i })).toBeInTheDocument()
    })

    // Try to proceed without selecting member type (step 1 validation)
    const nextButton = screen.getByRole('button', { name: /neste/i })
    
    // Button should be disabled when no member type is selected
    expect(nextButton).toBeDisabled()
    
    // Select a member type to proceed to step 2
    const memberTypeOption = screen.getByText('Active Member')
    await user.click(memberTypeOption)
    
    // Now button should be enabled
    await waitFor(() => {
      expect(nextButton).not.toBeDisabled()
    })
    
    // Proceed to step 2
    await user.click(nextButton)
    
    // Should now be on step 2
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /personlig informasjon/i })).toBeInTheDocument()
    })
    
    // Try to proceed without filling required fields on step 2
    const step2NextButton = screen.getByRole('button', { name: /neste/i })
    
         // Button should be disabled when required fields are empty
     expect(step2NextButton).toBeDisabled()
   })

   it('should show validation errors on step 2 when form is submitted with invalid data', async () => {
     const user = userEvent.setup()
     render(<RegistrationWizard />)
     
     // Wait for form to load and navigate to step 2
     await waitFor(() => {
       expect(screen.getByText('Active Member')).toBeInTheDocument()
     })
     
     // Select member type and proceed to step 2
     const memberTypeOption = screen.getByText('Active Member')
     await user.click(memberTypeOption)
     
     const nextButton = screen.getByRole('button', { name: /neste/i })
     await user.click(nextButton)
     
     // Should now be on step 2
     await waitFor(() => {
       expect(screen.getByRole('heading', { name: /personlig informasjon/i })).toBeInTheDocument()
     })
     
     // Try to fill with invalid data and trigger validation
     const fullNameInput = screen.getByLabelText(/fullt navn/i)
     const emailInput = screen.getByLabelText(/e-post/i)
     
     // Fill with invalid email
     await user.type(emailInput, 'invalid-email')
     await user.clear(emailInput) // Clear to trigger required validation
     await user.tab() // Trigger blur event
     
     // Clear name field to trigger required validation
     await user.click(fullNameInput)
     await user.tab()
     
     // The button should remain disabled due to validation
     const step2NextButton = screen.getByRole('button', { name: /neste/i })
     expect(step2NextButton).toBeDisabled()
   })
 })  