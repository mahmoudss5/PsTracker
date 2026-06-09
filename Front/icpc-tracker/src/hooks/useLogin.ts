import { useCallback, useState, type FormEvent } from 'react';
import type { LoginCredentials } from '../types/auth.types';

const INITIAL_CREDENTIALS: LoginCredentials = {
  email: '',
  password: '',
};

export function useLogin() {
  const [credentials, setCredentials] = useState<LoginCredentials>(INITIAL_CREDENTIALS);
  const [isSubmitting, setIsSubmitting] = useState(false);

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
        // TODO: connect to auth API
        console.log('Sign in:', credentials);
      } finally {
        setIsSubmitting(false);
      }
    },
    [credentials],
  );

  const handleCodeforcesSignIn = useCallback(() => {
    // TODO: redirect to Codeforces OAuth
    console.log('Sign in with Codeforces');
  }, []);

  const handleForgotPassword = useCallback(() => {
    // TODO: navigate to forgot-password flow
    console.log('Forgot password');
  }, []);

  return {
    email: credentials.email,
    password: credentials.password,
    isSubmitting,
    setEmail,
    setPassword,
    handleSignIn,
    handleCodeforcesSignIn,
    handleForgotPassword,
  };
}
