import { useCallback, useState } from 'react';

export type AuthView = 'login' | 'register';

export function useAuthView(initialView: AuthView = 'login') {
  const [view, setView] = useState<AuthView>(initialView);

  const switchToLogin = useCallback(() => setView('login'), []);
  const switchToRegister = useCallback(() => setView('register'), []);

  return { view, switchToLogin, switchToRegister };
}
