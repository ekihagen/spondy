import React from 'react';
import { ChevronRight, Users, CreditCard } from 'lucide-react';
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
  const selectedGroupId = watch('groupId');

  const selectedMemberType = form.memberTypes.find(mt => mt.id === selectedMemberTypeId);

  const canProceed = selectedMemberTypeId && selectedGroupId;

  const handleMemberTypeSelect = (memberTypeId: number) => {
    setValue('memberTypeId', memberTypeId);
  };

  const handleGroupSelect = (groupId: number) => {
    setValue('groupId', groupId);
  };

  return (
    <div className="space-y-8">
      {/* Medlemstyper */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
          <CreditCard className="w-5 h-5 mr-2" />
          Velg medlemstype
        </h2>
        <div className="space-y-3">
          {form.memberTypes.map((memberType) => (
            <div
              key={memberType.id}
              className={`border rounded-lg p-4 cursor-pointer transition-all ${
                selectedMemberTypeId === memberType.id
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-200 hover:border-gray-300'
              }`}
              onClick={() => handleMemberTypeSelect(memberType.id)}
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h3 className="font-medium text-gray-900">{memberType.name}</h3>
                  <p className="text-sm text-gray-600 mt-1">{memberType.description}</p>
                </div>
                <div className="text-right">
                  <span className="text-lg font-semibold text-gray-900">
                    {memberType.price.toLocaleString('nb-NO')} kr
                  </span>
                </div>
              </div>
              <input
                type="radio"
                {...register('memberTypeId', { valueAsNumber: true })}
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

      {/* Grupper */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
          <Users className="w-5 h-5 mr-2" />
          Velg gruppe
        </h2>
        <div className="space-y-3">
          {form.groups.map((group) => (
            <div
              key={group.id}
              className={`border rounded-lg p-4 cursor-pointer transition-all ${
                selectedGroupId === group.id
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-200 hover:border-gray-300'
              }`}
              onClick={() => handleGroupSelect(group.id)}
            >
              <h3 className="font-medium text-gray-900">{group.name}</h3>
              <p className="text-sm text-gray-600 mt-1">{group.description}</p>
              <input
                type="radio"
                {...register('groupId', { valueAsNumber: true })}
                value={group.id}
                className="sr-only"
              />
            </div>
          ))}
        </div>
        {errors.groupId && (
          <p className="mt-2 text-sm text-red-600">{String(errors.groupId.message)}</p>
        )}
      </div>

      {/* Sammendrag */}
      {selectedMemberType && (
        <div className="bg-gray-50 rounded-lg p-4">
          <h3 className="font-medium text-gray-900 mb-2">Valgt medlemstype:</h3>
          <div className="flex justify-between items-center">
            <span className="text-gray-700">{selectedMemberType.name}</span>
            <span className="font-semibold text-gray-900">
              {selectedMemberType.price.toLocaleString('nb-NO')} kr
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
          className={`flex items-center px-6 py-2 rounded-md font-medium transition-colors ${
            canProceed
              ? 'bg-blue-600 text-white hover:bg-blue-700'
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