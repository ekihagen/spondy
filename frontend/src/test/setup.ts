import '@testing-library/jest-dom'
import { vi, beforeEach } from 'vitest'

// Mock environment variables for tests
Object.defineProperty(window, 'location', {
  value: {
    hostname: 'localhost',
  },
  writable: true,
})

// Mock fetch for API tests
globalThis.fetch = vi.fn()

// Setup function to reset mocks between tests
beforeEach(() => {
  vi.clearAllMocks()
}) 