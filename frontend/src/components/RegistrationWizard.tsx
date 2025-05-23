import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useRegistrationForm } from '../hooks/useRegistrationForm';
import { StepOne } from './StepOne';
import { StepTwo } from './StepTwo';
import { StepThree } from './StepThree';
import { SuccessMessage } from './SuccessMessage';
import { LoadingSpinner } from './LoadingSpinner';
import { ErrorMessage } from './ErrorMessage';
import { ClosedBanner } from './ClosedBanner';
import type { RegistrationRequest } from '../types';

const registrationSchema = z.object({
  fullName: z.string().min(1, 'Fullt navn er påkrevd'),
  email: z.string().email('Ugyldig e-postadresse'),
  phoneNumber: z.string().min(8, 'Telefonnummer må være minst 8 siffer'),
  birthDate: z.string().min(1, 'Fødselsdato er påkrevd'),
  memberTypeId: z.number().min(1, 'Medlemstype må velges'),
  groupId: z.number().min(1, 'Gruppe må velges'),
});

type FormData = z.infer<typeof registrationSchema>;

export const RegistrationWizard: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(1);
  const { form, loading, error, submitting, submitted, submitRegistration, isRegistrationOpen } = useRegistrationForm();
  
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(registrationSchema),
  });

  const watchedValues = watch();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!form) {
    return <ErrorMessage message="Kunne ikke laste registreringsskjema" />;
  }

  if (submitted) {
    return <SuccessMessage />;
  }

  if (!isRegistrationOpen()) {
    return <ClosedBanner form={form} />;
  }

  const nextStep = () => {
    if (currentStep < 3) {
      setCurrentStep(currentStep + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const onSubmit = async (data: FormData) => {
    try {
      await submitRegistration(data as RegistrationRequest);
    } catch (err) {
      // Error is handled by the hook
    }
  };

  const renderStep = () => {
    switch (currentStep) {
      case 1:
        return (
          <StepOne
            form={form}
            register={register}
            errors={errors}
            watch={watch}
            setValue={setValue}
            onNext={nextStep}
          />
        );
      case 2:
        return (
          <StepTwo
            register={register}
            errors={errors}
            onNext={nextStep}
            onPrev={prevStep}
          />
        );
      case 3:
        return (
          <StepThree
            form={form}
            data={watchedValues}
            onSubmit={handleSubmit(onSubmit)}
            onPrev={prevStep}
            submitting={submitting}
          />
        );
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-2xl mx-auto px-4">
        <div className="bg-white rounded-lg shadow-lg p-6">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{form.title}</h1>
            <p className="text-gray-600">{form.description}</p>
          </div>

          {/* Progress indicator */}
          <div className="mb-8">
            <div className="flex items-center justify-between">
              {[1, 2, 3].map((step) => (
                <div key={step} className="flex items-center">
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                      step <= currentStep
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-200 text-gray-600'
                    }`}
                  >
                    {step}
                  </div>
                  {step < 3 && (
                    <div
                      className={`w-16 h-1 mx-2 ${
                        step < currentStep ? 'bg-blue-600' : 'bg-gray-200'
                      }`}
                    />
                  )}
                </div>
              ))}
            </div>
            <div className="flex justify-between mt-2 text-sm text-gray-600">
              <span>Velg type og gruppe</span>
              <span>Personlig informasjon</span>
              <span>Bekreft og send</span>
            </div>
          </div>

          {renderStep()}
        </div>
      </div>
    </div>
  );
}; 