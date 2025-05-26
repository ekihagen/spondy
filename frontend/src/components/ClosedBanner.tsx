import React from 'react';
import { Calendar, Clock } from 'lucide-react';
import type { RegistrationForm } from '../types';

interface ClosedBannerProps {
  form: RegistrationForm;
}

export const ClosedBanner: React.FC<ClosedBannerProps> = ({ form }) => {
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('nb-NO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="max-w-2xl mx-auto px-4">
        <div className="bg-white rounded-lg shadow-lg p-8 text-center">
          <Clock className="w-16 h-16 text-orange-500 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-900 mb-4">{form.title}</h1>
          <div className="bg-orange-50 border border-orange-200 rounded-lg p-6 mb-6">
            <div className="flex items-center justify-center mb-2">
              <Calendar className="w-5 h-5 text-orange-600 mr-2" />
              <span className="text-orange-800 font-medium">Registrering stengt</span>
            </div>
            <p className="text-orange-700">
              Registreringen åpnet {formatDate(form.registrationOpens)}
            </p>
          </div>
          <p className="text-gray-600 mb-6">{form.description}</p>
          <p className="text-sm text-gray-500">
            Kontakt klubben direkte hvis du har spørsmål om registrering.
          </p>
        </div>
      </div>
    </div>
  );
}; 