export type UserRole = 'trainee' | 'coach';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterCredentials {
  email: string;
  password: string;
  codeforcesHandle: string;
  role: UserRole | null;
}
