import React from 'react';
import { ChevronLeft, Send, Check, User, Mail, Phone, Calendar, CreditCard } from 'lucide-react';
import type { RegistrationForm } from '../types';

interface StepThreeProps {
  form: RegistrationForm;
  data: any;
  onSubmit: () => void;
  onPrev: () => void;
  submitting: boolean;
}

export const StepThree: React.FC<StepThreeProps> = ({
  form,
  data,
  onSubmit,
  onPrev,
  submitting,
}) => {
  const selectedMemberType = form?.memberTypes?.find(mt => mt.id === data.memberTypeId);

  const formatDate = (dateString: string) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('nb-NO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div className="space-y-4 sm:space-y-6">
      <h2 className="text-lg sm:text-xl font-semibold text-gray-900 mb-4 sm:mb-6 flex items-center">
        <Check className="w-5 h-5 mr-2 text-green-600" />
        Confirm Registration
      </h2>

      <div className="bg-gradient-to-r from-gray-50 to-blue-50 rounded-xl p-4 sm:p-6 space-y-4 sm:space-y-6 border border-blue-100">
        {/* Personal information */}
        <div>
          <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
            <User className="w-4 h-4 mr-2 text-blue-600" />
            Personal Information
          </h3>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-3 sm:gap-4">
            <div className="flex items-center bg-white rounded-lg p-2 sm:p-3">
              <User className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Name:</span>
              <span className="font-medium">{data.fullName}</span>
            </div>
            <div className="flex items-center bg-white rounded-lg p-2 sm:p-3">
              <Mail className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Email:</span>
              <span className="font-medium">{data.email}</span>
            </div>
            <div className="flex items-center bg-white rounded-lg p-2 sm:p-3">
              <Phone className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Phone:</span>
              <span className="font-medium">{data.phoneNumber}</span>
            </div>
            <div className="flex items-center bg-white rounded-lg p-2 sm:p-3">
              <Calendar className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Birth date:</span>
              <span className="font-medium">{formatDate(data.birthDate)}</span>
            </div>
          </div>
        </div>

        {/* Membership type */}
        {selectedMemberType && (
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
              <CreditCard className="w-4 h-4 mr-2 text-blue-600" />
              Membership Type
            </h3>
            <div className="bg-white rounded-lg p-3 sm:p-4 border border-blue-200 shadow-sm">
              <div className="flex justify-between items-center">
                <div>
                  <h4 className="font-medium text-gray-900">{selectedMemberType.name}</h4>
                </div>
                <div className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                  Selected
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Confirmation */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 sm:p-4">
        <div className="flex items-start">
          <Check className="w-5 h-5 text-blue-600 mr-3 mt-0.5 flex-shrink-0" />
          <div className="text-sm text-blue-800">
            <p className="font-medium mb-1">By submitting this registration I confirm that:</p>
            <ul className="list-disc list-inside space-y-1 ml-4">
              <li>The information I have provided is correct</li>
              <li>I accept the club's terms and conditions</li>
              <li>I consent to the processing of my personal data</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Navigation buttons */}
      <div className="flex justify-between">
        <button
          type="button"
          onClick={onPrev}
          disabled={submitting}
          className="flex items-center px-4 sm:px-6 py-2 sm:py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <ChevronLeft className="w-4 h-4 mr-1" />
          Back
        </button>
        <button
          type="button"
          onClick={onSubmit}
          disabled={submitting}
          className="flex items-center px-6 sm:px-8 py-2 sm:py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {submitting ? (
            <>
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
              Submitting...
            </>
          ) : (
            <>
              <Send className="w-4 h-4 mr-2" />
              Submit Registration
            </>
          )}
        </button>
      </div>
    </div>
  );
}; 