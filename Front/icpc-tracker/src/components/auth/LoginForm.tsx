import { useLogin } from '../../hooks/Auth/useLogin';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';



interface LoginFormProps {
  onSwitchToRegister: () => void;
}

export function LoginForm({ onSwitchToRegister }: LoginFormProps) {
  const {
    email,
    password,
    isSubmitting,
    setEmail,
    setPassword,
    handleSignIn,
    handleForgotPassword,
  } = useLogin();

  return (
    <div className="flex flex-col justify-center px-8 py-10 md:px-10">
      <h2 className="text-2xl font-semibold text-white">Welcome back</h2>
      <p className="mt-2 text-sm text-muted">
        Enter your credentials to access your dashboard.
      </p>

      <form onSubmit={handleSignIn} className="mt-8 flex flex-col gap-5">
        <Input
          label="Email"
          type="email"
          placeholder="name@domain.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          autoComplete="email"
          required
        />

        <Input
          label="Password"
          type="password"
          placeholder="••••••••"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          autoComplete="current-password"
          required
          labelAction={
            <button
              type="button"
              onClick={handleForgotPassword}
              className="text-xs text-accent transition-colors hover:text-accent-hover"
            >
              Forgot?
            </button>
          }
        />

        <Button type="submit" fullWidth disabled={isSubmitting}>
          {isSubmitting ? 'Signing in...' : 'Sign In'}
        </Button>
      </form>

      

      <p className="mt-6 text-center text-sm text-muted">
        Don&apos;t have an account?{' '}
        <button
          type="button"
          onClick={onSwitchToRegister}
          className="font-medium text-accent transition-colors hover:text-accent-hover"
        >
          Sign up
        </button>
      </p>
    </div>
  );
}
