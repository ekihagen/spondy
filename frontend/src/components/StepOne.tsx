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
      {/* Member types */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-6 flex items-center">
          <div className="w-8 h-8 bg-gradient-to-r from-blue-600 to-indigo-600 rounded-lg flex items-center justify-center mr-3">
            <CreditCard className="w-4 h-4 text-white" />
          </div>
          Select membership type
        </h2>
        <div className="space-y-4">
          {form.memberTypes.map((memberType) => (
            <div
              key={memberType.id}
              className={`group border-2 rounded-xl p-6 cursor-pointer transition-all duration-300 hover:shadow-lg hover:scale-[1.02] ${
                selectedMemberTypeId === memberType.id
                  ? 'border-blue-500 bg-gradient-to-r from-blue-50 to-indigo-50 ring-2 ring-blue-200 shadow-lg'
                  : 'border-gray-200 hover:border-blue-300 bg-white hover:bg-gray-50'
              }`}
              onClick={() => handleMemberTypeSelect(memberType.id)}
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h3 className="font-semibold text-gray-900 group-hover:text-blue-900 transition-colors text-lg">{memberType.name}</h3>
                </div>
                <div className="flex items-center ml-4">
                  <div className={`w-6 h-6 rounded-full border-2 transition-all duration-300 ${
                    selectedMemberTypeId === memberType.id
                      ? 'border-blue-500 bg-blue-500 shadow-md'
                      : 'border-gray-300 group-hover:border-blue-400'
                  }`}>
                    {selectedMemberTypeId === memberType.id && (
                      <div className="w-full h-full rounded-full bg-white scale-50 flex items-center justify-center">
                        <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                      </div>
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

      {/* Summary */}
      {selectedMemberType && (
        <div className="bg-gradient-to-r from-blue-50 via-indigo-50 to-purple-50 rounded-xl p-6 border border-blue-200 shadow-md">
          <h3 className="font-semibold text-gray-900 mb-3 flex items-center">
            <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center mr-2">
              <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
            </div>
            Selected membership type:
          </h3>
          <div className="flex justify-between items-center">
            <span className="text-gray-800 font-medium text-lg">{selectedMemberType.name}</span>
            <span className="px-4 py-2 bg-gradient-to-r from-green-100 to-blue-100 text-green-800 rounded-full text-sm font-semibold shadow-sm">
              ✓ Selected
            </span>
          </div>
        </div>
      )}

      {/* Next button */}
      <div className="flex justify-end">
        <button
          type="button"
          onClick={onNext}
          disabled={!canProceed}
          className={`flex items-center px-8 py-4 rounded-xl font-semibold transition-all duration-300 transform ${
            canProceed
              ? 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white hover:from-blue-700 hover:to-indigo-700 shadow-lg hover:shadow-xl hover:scale-105'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed opacity-60'
          }`}
        >
          Next
          <ChevronRight className="w-5 h-5 ml-2" />
        </button>
      </div>
    </div>
  );
}; 