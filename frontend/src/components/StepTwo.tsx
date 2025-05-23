import React from 'react';
import { ChevronLeft, ChevronRight, User, Mail, Phone, Calendar } from 'lucide-react';
import type { UseFormRegister, FieldErrors } from 'react-hook-form';

interface StepTwoProps {
  register: UseFormRegister<any>;
  errors: FieldErrors<any>;
  onNext: () => void;
  onPrev: () => void;
}

export const StepTwo: React.FC<StepTwoProps> = ({
  register,
  errors,
  onNext,
  onPrev,
}) => {
  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold text-gray-900 mb-6 flex items-center">
        <User className="w-5 h-5 mr-2" />
        Personlig informasjon
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Fullt navn */}
        <div className="md:col-span-2">
          <label htmlFor="fullName" className="block text-sm font-medium text-gray-700 mb-2">
            Fullt navn *
          </label>
          <div className="relative">
            <User className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              id="fullName"
              {...register('fullName')}
              className={`w-full pl-10 pr-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.fullName ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="Skriv inn ditt fulle navn"
            />
          </div>
          {errors.fullName && (
            <p className="mt-1 text-sm text-red-600">{String(errors.fullName.message)}</p>
          )}
        </div>

        {/* E-post */}
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
            E-postadresse *
          </label>
          <div className="relative">
            <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="email"
              id="email"
              {...register('email')}
              className={`w-full pl-10 pr-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.email ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="din@epost.no"
            />
          </div>
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{String(errors.email.message)}</p>
          )}
        </div>

        {/* Telefonnummer */}
        <div>
          <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 mb-2">
            Telefonnummer *
          </label>
          <div className="relative">
            <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="tel"
              id="phoneNumber"
              {...register('phoneNumber')}
              className={`w-full pl-10 pr-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.phoneNumber ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="12345678"
            />
          </div>
          {errors.phoneNumber && (
            <p className="mt-1 text-sm text-red-600">{String(errors.phoneNumber.message)}</p>
          )}
        </div>

        {/* Fødselsdato */}
        <div className="md:col-span-2">
          <label htmlFor="birthDate" className="block text-sm font-medium text-gray-700 mb-2">
            Fødselsdato *
          </label>
          <div className="relative">
            <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="date"
              id="birthDate"
              {...register('birthDate')}
              className={`w-full pl-10 pr-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.birthDate ? 'border-red-500' : 'border-gray-300'
              }`}
              max={new Date().toISOString().split('T')[0]}
            />
          </div>
          {errors.birthDate && (
            <p className="mt-1 text-sm text-red-600">{String(errors.birthDate.message)}</p>
          )}
        </div>
      </div>

      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <p className="text-sm text-blue-800">
          <strong>Personvern:</strong> Informasjonen du oppgir vil kun brukes til medlemsregistrering 
          og kommunikasjon fra klubben. Vi deler ikke dine opplysninger med tredjeparter.
        </p>
      </div>

      {/* Navigasjonsknapper */}
      <div className="flex justify-between">
        <button
          type="button"
          onClick={onPrev}
          className="flex items-center px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition-colors"
        >
          <ChevronLeft className="w-4 h-4 mr-1" />
          Tilbake
        </button>
        <button
          type="button"
          onClick={onNext}
          className="flex items-center px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
        >
          Neste
          <ChevronRight className="w-4 h-4 ml-1" />
        </button>
      </div>
    </div>
  );
}; 