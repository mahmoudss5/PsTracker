import { useCallback, useState} from 'react';
import type {SubmitEvent} from "react";
import type { RegisterCredentials, UserRole } from '../../types/auth.types';
import { toast } from 'sonner';
import {useAuth} from "../../contextes/AuthContext";
const INITIAL_CREDENTIALS: Omit<RegisterCredentials, 'isCoach'> = {
  username:'',
  email: '',
  password: '',
  codeforcesHandle: '',
};

export function useRegister() {
  const [credentials, setCredentials] = useState(INITIAL_CREDENTIALS);
  const [confirmPassword, setConfirmPasswordState] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [selectedRole, setSelectedRole] = useState<UserRole | null>('trainee');
  const [isSubmitting, setIsSubmitting] = useState(false);
const {register}=useAuth();



  const setUserName=useCallback((name:string)=>{
    setCredentials((prev)=>({...prev,username:name}))
  },[])

  const setEmail = useCallback((email: string) => {
    setCredentials((prev) => ({ ...prev, email }));
  }, []);

  const setPassword = useCallback((password: string) => {
    setCredentials((prev) => ({ ...prev, password }));
  }, []);

  const setConfirmPassword = useCallback((value: string) => {
    setConfirmPasswordState(value);
  }, []);

  const setCodeforcesHandle = useCallback((codeforcesHandle: string) => {
    setCredentials((prev) => ({ ...prev, codeforcesHandle }));
  }, []);

  const selectRole = useCallback((role: UserRole) => {
    setSelectedRole(role);
  }, []);

  const handleSignUp = useCallback(
    async (event: SubmitEvent<HTMLFormElement>) => {
      event.preventDefault();
      if (!selectedRole) return;
      if (credentials.password !== confirmPassword) {
        setError('Passwords do not match');
        return;
      }
      if(credentials.username.length<5){
        setError("Username must be at least 5 characters long");
        return;
      }
      if(credentials.email.length<5 || !credentials.email.includes("@")){
        setError("Please enter a valid email address");
       return;
      }
      const finalCredentials: RegisterCredentials = {
        ...credentials,
        isCoach: selectedRole === 'coach',
      };

      setIsSubmitting(true);

      try {
        await register(finalCredentials);
        toast.success('Successfully signed up!');
        console.log('Sign up:', { ...credentials, role: selectedRole==='coach'});
      }catch (error) {
         const message=error instanceof Error?error.message:String(error);
         setError(message);
         toast.error(message);
      }
      finally {
        setIsSubmitting(false);
      }
    },
    [credentials, confirmPassword, selectedRole,register],
  );

  return {
    email: credentials.email,
    password: credentials.password,
    confirmPassword,
    username:credentials.username,
    codeforcesHandle: credentials.codeforcesHandle,
    selectedRole,
    error,
    isSubmitting,
    setUserName,
    setEmail,
    setPassword,
    setConfirmPassword,
    setCodeforcesHandle,
    selectRole,
    handleSignUp,
  };

}
