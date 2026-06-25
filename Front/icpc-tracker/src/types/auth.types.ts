export type UserRole = 'trainee' | 'coach';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterCredentials {
  username: string;
  email: string;
  password: string;
  codeforcesHandle: string;
  role: UserRole | null;
}

export interface AuthContextType {
  isError:string | undefined;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (credentials: RegisterCredentials) => Promise<void>;
  logout: () => void;
}

export interface AuthResponse{
 token:string
 userId:number 
 userName:string
 isCoach:boolean
}