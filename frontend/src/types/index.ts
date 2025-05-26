export interface MemberType {
  id: string;
  name: string;
}

export interface Group {
  id: number;
  name: string;
  description: string;
}

export interface RegistrationForm {
  clubId: string;
  formId: string;
  title: string;
  description?: string;
  registrationOpens: string;
  memberTypes: MemberType[];
}

export interface RegistrationRequest {
  fullName: string;
  email: string;
  phoneNumber: string;
  birthDate: string;
  memberTypeId: string;
}

export interface RegistrationResponse {
  success: boolean;
  message: string;
  registrationId?: number;
  memberName?: string;
}

export interface ApiError {
  success: false;
  message: string;
  error: string;
  fieldErrors?: Record<string, string>;
} 