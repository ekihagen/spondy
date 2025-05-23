import React from 'react';
import { ChevronRight, CreditCard } from 'lucide-react';
import type { RegistrationForm } from '../types';
import type { UseFormRegister, FieldErrors, UseFormWatch, UseFormSetValue } from 'react-hook-form';

interface StepOneProps {
  form: RegistrationForm;
  register: UseFormRegister<any>;
  errors: FieldErrors<any>;
  watch: UseFormWatch<any>;
  setValue: UseFormSetValue<any>;
  onNext: () => void;
}

export const StepOne: React.FC<StepOneProps> = ({
  form,
  register,
  errors,
  watch,
  setValue,
  onNext,
}) => {
  const selectedMemberTypeId = watch('memberTypeId');

  const selectedMemberType = form.memberTypes.find(mt => mt.id === selectedMemberTypeId);

  const canProceed = selectedMemberTypeId;

  const handleMemberTypeSelect = (memberTypeId: string) => {
    setValue('memberTypeId', memberTypeId);
  };

  return (
    <div className="space-y-8">
      {/* Medlemstyper */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
          <CreditCard className="w-5 h-5 mr-2 text-blue-600" />
          Velg medlemstype
        </h2>
        <div className="space-y-3">
          {form.memberTypes.map((memberType) => (
            <div
              key={memberType.id}
              className={`border rounded-lg p-4 cursor-pointer transition-all ${
                selectedMemberTypeId === memberType.id
                  ? 'border-blue-500 bg-blue-50 ring-2 ring-blue-200'
                  : 'border-gray-200 hover:border-blue-300 hover:bg-blue-50'
              }`}
              onClick={() => handleMemberTypeSelect(memberType.id)}
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h3 className="font-medium text-gray-900">{memberType.name}</h3>
                </div>
                <div className="flex items-center">
                  <div className={`w-4 h-4 rounded-full border-2 ${
                    selectedMemberTypeId === memberType.id
                      ? 'border-blue-500 bg-blue-500'
                      : 'border-gray-300'
                  }`}>
                    {selectedMemberTypeId === memberType.id && (
                      <div className="w-2 h-2 bg-white rounded-full m-0.5"></div>
                    )}
                  </div>
                </div>
              </div>
              <input
                type="radio"
                {...register('memberTypeId')}
                value={memberType.id}
                className="sr-only"
              />
            </div>
          ))}
        </div>
        {errors.memberTypeId && (
          <p className="mt-2 text-sm text-red-600">{String(errors.memberTypeId.message)}</p>
        )}
      </div>

      {/* Sammendrag */}
      {selectedMemberType && (
        <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-4 border border-blue-200">
          <h3 className="font-medium text-gray-900 mb-2">Valgt medlemstype:</h3>
          <div className="flex justify-between items-center">
            <span className="text-gray-700">{selectedMemberType.name}</span>
            <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
              Valgt
            </span>
          </div>
        </div>
      )}

      {/* Neste knapp */}
      <div className="flex justify-end">
        <button
          type="button"
          onClick={onNext}
          disabled={!canProceed}
          className={`flex items-center px-6 py-3 rounded-lg font-medium transition-all ${
            canProceed
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