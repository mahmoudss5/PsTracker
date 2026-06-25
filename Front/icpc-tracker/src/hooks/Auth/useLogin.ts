import { useCallback, useState, type FormEvent } from 'react';
import type { LoginCredentials } from '../../types/auth.types';
import { useAuth, } from '../../contextes/AuthContext';
import {toast} from "sonner";
import {useNavigate} from "react-router-dom";
import {getIsCoach} from "../../services/AuthService.tsx";

const INITIAL_CREDENTIALS: LoginCredentials = {
  email: '',
  password: '',
};

export function useLogin() {
  const [credentials, setCredentials] = useState<LoginCredentials>(INITIAL_CREDENTIALS);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isCoach=getIsCoach();
 const navigate=useNavigate();
  const {login} = useAuth();


  const setEmail = useCallback((email: string) => {
    setCredentials((prev) => ({ ...prev, email }));
  }, []);


  const setPassword = useCallback((password: string) => {
    setCredentials((prev) => ({ ...prev, password }));
  }, []);


  const handleSignIn = useCallback(
    async (event: FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      setIsSubmitting(true);

      try {
        await  login(credentials);
        toast.success('Successfully signed in!');
        console.log('Sign in:', credentials);
        if(isCoach) navigate("/coachDashboard");
        else navigate("/traineeDashboard");
      }
      catch (error) {
         const message=error instanceof Error?error.message:String(error);
         setError(message);
         toast.error(message);
      }
      finally {
        setIsSubmitting(false);
      }
    },
    [credentials,navigate],
  );


  return {
    email: credentials.email,
    password: credentials.password,
    isSubmitting,
    error,
    setEmail,
    setPassword,
    handleSignIn,

  };
}
