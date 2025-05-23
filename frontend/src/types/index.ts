export interface MemberType {
  id: number;
  name: string;
  description: string;
  price: number;
}

export interface Group {
  id: number;
  name: string;
  description: string;
}

export interface RegistrationForm {
  id: number;
  title: string;
  description: string;
  registrationDate: string;
  memberTypes: MemberType[];
  groups: Group[];
}

export interface RegistrationRequest {
  fullName: string;
  email: string;
  phoneNumber: string;
  birthDate: string;
  memberTypeId: number;
  groupId: number;
}

export interface RegistrationResponse {
  success: boolean;
  message: string;
  registrationId?: number;
} 