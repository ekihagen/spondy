import React from 'react';
import { CheckCircle, Mail, Calendar, Users } from 'lucide-react';

interface SuccessMessageProps {
  memberName?: string;
  registrationId?: number;
  message?: string;
}

export const SuccessMessage: React.FC<SuccessMessageProps> = ({ 
  memberName, 
  registrationId, 
  message = "Thank you for your registration! You will receive a confirmation email shortly." 
}) => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-white to-blue-50 flex items-center justify-center py-8">
      <div className="max-w-lg mx-auto px-4">
        <div className="bg-white rounded-xl shadow-xl p-8 text-center border border-green-100">
          <CheckCircle className="w-20 h-20 text-green-500 mx-auto mb-6" />
          
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            Registration Complete!
          </h2>
          
          {memberName && (
            <p className="text-xl text-gray-700 mb-4">
              Welcome, <span className="font-semibold text-blue-600">{memberName}</span>!
            </p>
          )}
          
          <p className="text-gray-600 mb-6 leading-relaxed">
            {message}
          </p>
          
          {registrationId && (
            <div className="bg-gray-50 rounded-lg p-4 mb-6">
              <p className="text-sm text-gray-600 mb-1">Registration ID:</p>
              <p className="font-mono text-lg font-semibold text-gray-900">
                #{registrationId}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                Keep this number for future reference
              </p>
            </div>
          )}
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
            <div className="flex flex-col items-center p-4 bg-blue-50 rounded-lg">
              <Mail className="w-8 h-8 text-blue-600 mb-2" />
              <p className="text-sm text-blue-800 font-medium">Email confirmation</p>
              <p className="text-xs text-blue-600">Coming soon</p>
            </div>
            
            <div className="flex flex-col items-center p-4 bg-green-50 rounded-lg">
              <Calendar className="w-8 h-8 text-green-600 mb-2" />
              <p className="text-sm text-green-800 font-medium">Calendar invitation</p>
              <p className="text-xs text-green-600">Included in email</p>
            </div>
            
            <div className="flex flex-col items-center p-4 bg-purple-50 rounded-lg">
              <Users className="w-8 h-8 text-purple-600 mb-2" />
              <p className="text-sm text-purple-800 font-medium">Join the group</p>
              <p className="text-xs text-purple-600">Link in email</p>
            </div>
          </div>
          
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <button
              onClick={() => window.location.reload()}
              className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
            >
              Register another person
            </button>
            
            <button
              onClick={() => window.location.href = '/'}
              className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
            >
              Back to home
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}; 