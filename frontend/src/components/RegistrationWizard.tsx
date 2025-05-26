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
  fullName: z.string().min(1, 'Full name is required'),
  email: z.string().email('Invalid email address'),
  phoneNumber: z.string().min(8, 'Phone number must be at least 8 digits').regex(/^\d+$/, 'Phone number can only contain digits'),
  birthDate: z.string().min(1, 'Birth date is required').regex(/^\d{2}\.\d{2}\.\d{4}$/, 'Birth date must be in DD.MM.YYYY format'),
  memberTypeId: z.string().min(1, 'Member type must be selected'),
});

type FormData = z.infer<typeof registrationSchema>;

export const RegistrationWizard: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(1);
  const { form, loading, error, submitting, submitted, registrationResponse, submitRegistration, isRegistrationOpen } = useRegistrationForm();
  
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
    return <ErrorMessage message="Could not load registration form" />;
  }

  if (submitted) {
    return (
      <SuccessMessage 
        memberName={registrationResponse?.memberName}
        registrationId={registrationResponse?.registrationId}
        message={registrationResponse?.message}
      />
    );
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
    } catch {
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
            setValue={setValue}
            watch={watch}
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
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 py-8">
      <div className="max-w-2xl mx-auto px-4">
        {/* Spond header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-blue-600 to-indigo-600 rounded-full mb-4 shadow-lg">
            <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
            </svg>
          </div>
          <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-900 to-indigo-900 bg-clip-text text-transparent mb-2">Spond Club</h1>
          <p className="text-blue-600 font-medium">Membership Registration</p>
        </div>
        
        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-2xl p-8 border border-white/20">
          <div className="mb-8">
            <h2 className="text-2xl font-bold text-gray-900 mb-2">{form.title}</h2>
            {form.description && <p className="text-gray-600">{form.description}</p>}
          </div>

          {/* Progress indicator */}
          <div className="mb-8">
            <div className="flex items-center justify-between">
              {[1, 2, 3].map((step) => (
                <div key={step} className="flex items-center">
                  <div
                    className={`w-12 h-12 rounded-full flex items-center justify-center text-sm font-bold transition-all duration-300 ${
                      step <= currentStep
                        ? 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-lg transform scale-110'
                        : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
                    }`}
                  >
                    {step <= currentStep ? (
                      step < currentStep ? (
                        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                        </svg>
                      ) : (
                        step
                      )
                    ) : (
                      step
                    )}
                  </div>
                  {step < 3 && (
                    <div
                      className={`w-20 h-2 mx-2 rounded-full transition-all duration-300 ${
                        step < currentStep ? 'bg-gradient-to-r from-blue-600 to-indigo-600' : 'bg-gray-200'
                      }`}
                    />
                  )}
                </div>
              ))}
            </div>
            <div className="flex justify-between mt-4 text-sm font-medium">
              <span className={currentStep >= 1 ? 'text-blue-600' : 'text-gray-500'}>Select membership</span>
              <span className={currentStep >= 2 ? 'text-blue-600' : 'text-gray-500'}>Personal information</span>
              <span className={currentStep >= 3 ? 'text-blue-600' : 'text-gray-500'}>Confirm and submit</span>
            </div>
          </div>

          {renderStep()}
        </div>
      </div>
    </div>
  );
}; 