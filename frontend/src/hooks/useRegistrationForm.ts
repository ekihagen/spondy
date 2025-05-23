import { useState, useEffect } from 'react';
import { api } from '../services/api';
import type { RegistrationForm, RegistrationRequest } from '../types';

export const useRegistrationForm = () => {
  const [form, setForm] = useState<RegistrationForm | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    const fetchForm = async () => {
      try {
        setLoading(true);
        const formData = await api.getForm();
        setForm(formData);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'En feil oppstod');
      } finally {
        setLoading(false);
      }
    };

    fetchForm();
  }, []);

  const submitRegistration = async (data: RegistrationRequest) => {
    if (!form) return;

    try {
      setSubmitting(true);
      setError(null);
      await api.submitRegistration(form.id, data);
      setSubmitted(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registrering feilet');
      throw err;
    } finally {
      setSubmitting(false);
    }
  };

  const isRegistrationOpen = () => {
    if (!form) return false;
    const today = new Date();
    const registrationDate = new Date(form.registrationDate);
    return registrationDate >= today;
  };

  return {
    form,
    loading,
    error,
    submitting,
    submitted,
    submitRegistration,
    isRegistrationOpen,
  };
}; 