import { useCallback, useState, type FormEvent } from 'react';
import type { RegisterCredentials, UserRole } from '../types/auth.types';

const INITIAL_CREDENTIALS: Omit<RegisterCredentials, 'role'> = {
  email: '',
  password: '',
  codeforcesHandle: '',
};

export function useRegister() {
  const [credentials, setCredentials] = useState(INITIAL_CREDENTIALS);
  const [selectedRole, setSelectedRole] = useState<UserRole | null>('trainee');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const setEmail = useCallback((email: string) => {
    setCredentials((prev) => ({ ...prev, email }));
  }, []);

  const setPassword = useCallback((password: string) => {
    setCredentials((prev) => ({ ...prev, password }));
  }, []);

  const setCodeforcesHandle = useCallback((codeforcesHandle: string) => {
    setCredentials((prev) => ({ ...prev, codeforcesHandle }));
  }, []);

  const selectRole = useCallback((role: UserRole) => {
    setSelectedRole(role);
  }, []);

  const handleSignUp = useCallback(
    async (event: FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      if (!selectedRole) return;

      setIsSubmitting(true);

      try {
        // TODO: connect to auth API
        console.log('Sign up:', { ...credentials, role: selectedRole });
      } finally {
        setIsSubmitting(false);
      }
    },
    [credentials, selectedRole],
  );

  return {
    email: credentials.email,
    password: credentials.password,
    codeforcesHandle: credentials.codeforcesHandle,
    selectedRole,
    isSubmitting,
    setEmail,
    setPassword,
    setCodeforcesHandle,
    selectRole,
    handleSignUp,
  };
}
