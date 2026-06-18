import { useCallback, useState, type FormEvent } from 'react';
import type { RegisterCredentials, UserRole } from '../../types/auth.types';

const INITIAL_CREDENTIALS: Omit<RegisterCredentials, 'role'> = {
  email: '',
  password: '',
  codeforcesHandle: '',
};

export function useRegister() {
  const [credentials, setCredentials] = useState(INITIAL_CREDENTIALS);
  const [confirmPassword, setConfirmPasswordState] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState<string | null>(null);
  const [selectedRole, setSelectedRole] = useState<UserRole | null>('trainee');
  const [isSubmitting, setIsSubmitting] = useState(false);
 
  const setEmail = useCallback((email: string) => {
    setCredentials((prev) => ({ ...prev, email }));
  }, []);

  const setPassword = useCallback((password: string) => {
    setCredentials((prev) => ({ ...prev, password }));
  }, []);

  const setConfirmPassword = useCallback((value: string) => {
    setConfirmPasswordState(value);
    setConfirmPasswordError(null);
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

      if (credentials.password !== confirmPassword) {
        setConfirmPasswordError('Passwords do not match.');
        return;
      }

      setIsSubmitting(true);

      try {
        // TODO: connect to auth API
        console.log('Sign up:', { ...credentials, role: selectedRole });
      } finally {
        setIsSubmitting(false);
      }
    },
    [credentials, confirmPassword, selectedRole],
  );

  return {
    email: credentials.email,
    password: credentials.password,
    confirmPassword,
    confirmPasswordError,
    codeforcesHandle: credentials.codeforcesHandle,
    selectedRole,
    isSubmitting,
    setEmail,
    setPassword,
    setConfirmPassword,
    setCodeforcesHandle,
    selectRole,
    handleSignUp,
  };
}
