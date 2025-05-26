import React, { useState } from 'react';
import { ChevronLeft, ChevronRight, User, Mail, Phone, Calendar } from 'lucide-react';
import type { UseFormRegister, FieldErrors, UseFormSetValue, UseFormWatch } from 'react-hook-form';

interface StepTwoProps {
  register: UseFormRegister<any>;
  errors: FieldErrors<any>;
  setValue: UseFormSetValue<any>;
  watch: UseFormWatch<any>;
  onNext: () => void;
  onPrev: () => void;
}

const countryCodes = [
  { code: '+47', country: 'Norge', flag: '🇳🇴', pattern: /^\d{8}$/ },
  { code: '+46', country: 'Sverige', flag: '🇸🇪', pattern: /^\d{8,9}$/ },
  { code: '+44', country: 'UK', flag: '🇬🇧', pattern: /^\d{10,11}$/ },
];

export const StepTwo: React.FC<StepTwoProps> = ({
  register,
  errors,
  setValue,
  watch,
  onNext,
  onPrev,
}) => {
  const [selectedCountryCode, setSelectedCountryCode] = useState('+47');
  const phoneNumber = watch('phoneNumber') || '';

  const getPhoneValidationMessage = () => {
    const country = countryCodes.find(c => c.code === selectedCountryCode);
    if (!country) return '';
    
    if (selectedCountryCode === '+47') {
      return 'Norsk telefonnummer (8 siffer)';
    } else if (selectedCountryCode === '+46') {
      return 'Svensk telefonnummer (8-9 siffer)';
    } else if (selectedCountryCode === '+44') {
      return 'Britisk telefonnummer (10-11 siffer)';
    }
    return '';
  };

  const isValidPhoneNumber = (phone: string) => {
    const country = countryCodes.find(c => c.code === selectedCountryCode);
    if (!country || !phone) return false;
    return country.pattern.test(phone.replace(/\s/g, ''));
  };

  const formatBirthDate = (value: string) => {
    // Remove all non-digits
    const digits = value.replace(/\D/g, '');
    
    // Format as DD.MM.YYYY
    if (digits.length <= 2) {
      return digits;
    } else if (digits.length <= 4) {
      return `${digits.slice(0, 2)}.${digits.slice(2)}`;
    } else {
      return `${digits.slice(0, 2)}.${digits.slice(2, 4)}.${digits.slice(4, 8)}`;
    }
  };

  const handleBirthDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatBirthDate(e.target.value);
    setValue('birthDate', formatted);
  };

  const canProceed = () => {
    const fullName = watch('fullName');
    const email = watch('email');
    const birthDate = watch('birthDate');
    
    return fullName && email && birthDate && phoneNumber && 
           isValidPhoneNumber(phoneNumber) && 
           /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) &&
           /^\d{2}\.\d{2}\.\d{4}$/.test(birthDate);
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold text-gray-900 mb-6 flex items-center">
        <User className="w-5 h-5 mr-2 text-blue-600" />
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
              className={`w-full pl-10 pr-3 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all ${
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
              className={`w-full pl-10 pr-3 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all ${
                errors.email ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="din@epost.no"
            />
          </div>
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{String(errors.email.message)}</p>
          )}
        </div>

        {/* Telefonnummer med country code */}
        <div>
          <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 mb-2">
            Telefonnummer *
          </label>
          <div className="flex">
            <select
              value={selectedCountryCode}
              onChange={(e) => setSelectedCountryCode(e.target.value)}
              className="px-3 py-3 border border-r-0 border-gray-300 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-gray-50"
            >
              {countryCodes.map((country) => (
                <option key={country.code} value={country.code}>
                  {country.flag} {country.code}
                </option>
              ))}
            </select>
            <div className="relative flex-1">
              <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="tel"
                id="phoneNumber"
                {...register('phoneNumber')}
                className={`w-full pl-10 pr-3 py-3 border rounded-r-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all ${
                  errors.phoneNumber || (phoneNumber && !isValidPhoneNumber(phoneNumber)) ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="12345678"
              />
            </div>
          </div>
          <p className="mt-1 text-xs text-gray-500">{getPhoneValidationMessage()}</p>
          {errors.phoneNumber && (
            <p className="mt-1 text-sm text-red-600">{String(errors.phoneNumber.message)}</p>
          )}
          {phoneNumber && !isValidPhoneNumber(phoneNumber) && !errors.phoneNumber && (
            <p className="mt-1 text-sm text-red-600">Ugyldig telefonnummer for valgt land</p>
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
              type="text"
              id="birthDate"
              {...register('birthDate')}
              onChange={handleBirthDateChange}
              className={`w-full pl-10 pr-3 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all ${
                errors.birthDate ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="DD.MM.YYYY (f.eks. 15.06.1990)"
              maxLength={10}
            />
          </div>
          <p className="mt-1 text-xs text-gray-500">Format: DD.MM.YYYY</p>
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
          className="flex items-center px-6 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all"
        >
          <ChevronLeft className="w-4 h-4 mr-1" />
          Tilbake
        </button>
        <button
          type="button"
          onClick={onNext}
          disabled={!canProceed()}
          className={`flex items-center px-6 py-3 rounded-lg font-medium transition-all ${
            canProceed()
              ? 'bg-blue-600 text-white hover:bg-blue-700 shadow-md hover:shadow-lg'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          }`}
        >
          Neste
          <ChevronRight className="w-4 h-4 ml-1" />
        </button>
      </div>
    </div>
  );
}; 