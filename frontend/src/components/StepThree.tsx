import React from 'react';
import { ChevronLeft, Send, Check, User, Mail, Phone, Calendar, CreditCard, Users } from 'lucide-react';
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
  const selectedMemberType = form.memberTypes.find(mt => mt.id === data.memberTypeId);
  const selectedGroup = form.groups.find(g => g.id === data.groupId);

  const formatDate = (dateString: string) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('nb-NO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold text-gray-900 mb-6 flex items-center">
        <Check className="w-5 h-5 mr-2" />
        Bekreft registrering
      </h2>

      <div className="bg-gray-50 rounded-lg p-6 space-y-6">
        {/* Personlig informasjon */}
        <div>
          <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
            <User className="w-4 h-4 mr-2" />
            Personlig informasjon
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-center">
              <User className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Navn:</span>
              <span className="font-medium">{data.fullName}</span>
            </div>
            <div className="flex items-center">
              <Mail className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">E-post:</span>
              <span className="font-medium">{data.email}</span>
            </div>
            <div className="flex items-center">
              <Phone className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Telefon:</span>
              <span className="font-medium">{data.phoneNumber}</span>
            </div>
            <div className="flex items-center">
              <Calendar className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-600 mr-2">Fødselsdato:</span>
              <span className="font-medium">{formatDate(data.birthDate)}</span>
            </div>
          </div>
        </div>

        {/* Medlemstype */}
        {selectedMemberType && (
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
              <CreditCard className="w-4 h-4 mr-2" />
              Medlemstype
            </h3>
            <div className="bg-white rounded-lg p-4 border">
              <div className="flex justify-between items-start">
                <div>
                  <h4 className="font-medium text-gray-900">{selectedMemberType.name}</h4>
                  <p className="text-sm text-gray-600 mt-1">{selectedMemberType.description}</p>
                </div>
                <div className="text-right">
                  <span className="text-lg font-semibold text-gray-900">
                    {selectedMemberType.price.toLocaleString('nb-NO')} kr
                  </span>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Gruppe */}
        {selectedGroup && (
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
              <Users className="w-4 h-4 mr-2" />
              Gruppe
            </h3>
            <div className="bg-white rounded-lg p-4 border">
              <h4 className="font-medium text-gray-900">{selectedGroup.name}</h4>
              <p className="text-sm text-gray-600 mt-1">{selectedGroup.description}</p>
            </div>
          </div>
        )}
      </div>

      {/* Bekreftelse */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start">
          <Check className="w-5 h-5 text-blue-600 mr-3 mt-0.5 flex-shrink-0" />
          <div className="text-sm text-blue-800">
            <p className="font-medium mb-1">Ved å sende inn denne registreringen bekrefter jeg at:</p>
            <ul className="list-disc list-inside space-y-1 ml-4">
              <li>Informasjonen jeg har oppgitt er korrekt</li>
              <li>Jeg godtar klubbens vilkår og betingelser</li>
              <li>Jeg samtykker til behandling av mine personopplysninger</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Navigasjonsknapper */}
      <div className="flex justify-between">
        <button
          type="button"
          onClick={onPrev}
          disabled={submitting}
          className="flex items-center px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <ChevronLeft className="w-4 h-4 mr-1" />
          Tilbake
        </button>
        <button
          type="button"
          onClick={onSubmit}
          disabled={submitting}
          className="flex items-center px-8 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {submitting ? (
            <>
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
              Sender...
            </>
          ) : (
            <>
              <Send className="w-4 h-4 mr-2" />
              Send registrering
            </>
          )}
        </button>
      </div>
    </div>
  );
}; 