import React from 'react';
import { AlertCircle, RefreshCw, Home } from 'lucide-react';

interface ErrorMessageProps {
  message: string;
  onRetry?: () => void;
  showHomeButton?: boolean;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({ 
  message, 
  onRetry,
  showHomeButton = true 
}) => {
  const handleRetry = () => {
    if (onRetry) {
      onRetry();
    } else {
      window.location.reload();
    }
  };

  const handleGoHome = () => {
    window.location.href = '/';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-red-50 via-white to-orange-50 flex items-center justify-center py-8">
      <div className="max-w-md mx-auto px-4">
        <div className="bg-white rounded-xl shadow-xl p-8 text-center border border-red-100">
          <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-6" />
          
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            Something went wrong
          </h2>
          
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-800 leading-relaxed whitespace-pre-line">
              {message}
            </p>
          </div>
          
          <div className="text-sm text-gray-600 mb-6">
            <p>If the problem persists, you can:</p>
            <ul className="mt-2 space-y-1 text-left">
              <li>• Check your internet connection</li>
              <li>• Try refreshing the page</li>
              <li>• Contact support if the error continues</li>
            </ul>
          </div>
          
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <button
              onClick={handleRetry}
              className="flex items-center justify-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
            >
              <RefreshCw className="w-4 h-4 mr-2" />
              Try again
            </button>
            
            {showHomeButton && (
              <button
                onClick={handleGoHome}
                className="flex items-center justify-center px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
              >
                <Home className="w-4 h-4 mr-2" />
                Back to home
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}; 