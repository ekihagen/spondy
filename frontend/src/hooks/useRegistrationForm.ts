import { useState, useEffect } from 'react';
import { api } from '../services/api';
import type { RegistrationForm, RegistrationRequest, RegistrationResponse } from '../types';

export const useRegistrationForm = () => {
  const [form, setForm] = useState<RegistrationForm | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [registrationResponse, setRegistrationResponse] = useState<RegistrationResponse | null>(null);

  useEffect(() => {
    const fetchForm = async () => {
      try {
        setLoading(true);
        const formData = await api.getForm();
        setForm(formData);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'En uventet feil oppstod ved henting av skjema');
      } finally {
        setLoading(false);
      }
    };

    fetchForm();
  }, []);

  const submitRegistration = async (data: RegistrationRequest) => {
    if (!form) {
      throw new Error('Skjema er ikke lastet ennå');
    }

    try {
      setSubmitting(true);
      setError(null);
      const response = await api.submitRegistration(form.formId, data);
      setRegistrationResponse(response);
      setSubmitted(true);
      return response;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'En uventet feil oppstod under registrering';
      setError(errorMessage);
      throw err;
    } finally {
      setSubmitting(false);
    }
  };

  const isRegistrationOpen = () => {
    if (!form) return false;
    const today = new Date();
    const registrationDate = new Date(form.registrationOpens);
    return registrationDate <= today;
  };

  const resetForm = () => {
    setSubmitted(false);
    setRegistrationResponse(null);
    setError(null);
  };

  return {
    form,
    loading,
    error,
    submitting,
    submitted,
    registrationResponse,
    submitRegistration,
    isRegistrationOpen,
    resetForm,
  };
}; 