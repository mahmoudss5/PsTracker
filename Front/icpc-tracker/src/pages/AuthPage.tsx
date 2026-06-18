import { AuthLayout } from '../components/auth/AuthLayout';
import { LoginForm } from '../components/auth/LoginForm';
import { RegisterView } from '../components/auth/RegisterView';
import { useAuthView } from '../hooks/Auth/useAuthView';

export function AuthPage() {
  const { view, switchToLogin, switchToRegister } = useAuthView();

  return (
    <AuthLayout wide={view === 'register'}>
      {view === 'login' ? (
        <LoginForm onSwitchToRegister={switchToRegister} />
      ) : (
        <RegisterView onSwitchToLogin={switchToLogin} />
      )}
    </AuthLayout>
  );
}
