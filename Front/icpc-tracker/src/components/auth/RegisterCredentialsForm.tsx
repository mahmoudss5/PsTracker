import { Button } from '../ui/Button';
import { Input } from '../ui/Input';

interface RegisterCredentialsFormProps {
  email: string;
  password: string;
  confirmPassword: string;
  codeforcesHandle: string;
  username: string;
  isSubmitting: boolean;
  onEmailChange: (value: string) => void;
  onPasswordChange: (value: string) => void;
  onConfirmPasswordChange: (value: string) => void;
  onCodeforcesHandleChange: (value: string) => void;
  onUsernameChange: (value: string) => void;
  onSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
  onSwitchToLogin: () => void;
}

export function RegisterCredentialsForm({
  email,
  password,
  confirmPassword,
  codeforcesHandle,
  username,
  isSubmitting,
  onEmailChange,
  onPasswordChange,
  onConfirmPasswordChange,
  onCodeforcesHandleChange,
  onUsernameChange,
  onSubmit,
  onSwitchToLogin,
}: RegisterCredentialsFormProps) {
  return (
    <div className="flex flex-col justify-center px-8 py-10 md:px-10">
      <h2 className="text-2xl font-semibold text-white">Create account</h2>
      <p className="mt-2 text-sm text-muted">
        Enter your details to get started with AlgoTrack.
      </p>

      <form onSubmit={onSubmit} className="mt-8 flex flex-col gap-5">
        
        <Input
          label="Username"
          type="text"
          placeholder="Enter your username"
          value={username}
          onChange={(e) => onUsernameChange(e.target.value)}
          autoComplete="username"
          required
        />
      
        <Input
          label="Email"
          type="email"
          placeholder="name@domain.com"
          value={email}
          onChange={(e) => onEmailChange(e.target.value)}
          autoComplete="email"
          required
        />

        <Input
          label="Password"
          type="password"
          placeholder="••••••••"
          value={password}
          onChange={(e) => onPasswordChange(e.target.value)}
          autoComplete="new-password"
          required
        />
        <div className="flex flex-col gap-2">
          <Input
            label="Confirm Password"
            type="password"
            placeholder="••••••••"
            value={confirmPassword}
            onChange={(e) => onConfirmPasswordChange(e.target.value)}
            autoComplete="new-password"
            required
          />
         
        </div>

        <Input
          label="Codeforces handle"
          type="text"
          placeholder="tourist"
          value={codeforcesHandle}
          onChange={(e) => onCodeforcesHandleChange(e.target.value)}
          autoComplete="username"
          required
        />

        <Button type="submit" fullWidth disabled={isSubmitting}>
          {isSubmitting ? 'Creating account...' : 'Sign Up'}
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-muted">
        Already have an account?{' '}
        <button
          type="button"
          onClick={onSwitchToLogin}
          className="font-medium text-accent transition-colors hover:text-accent-hover"
        >
          Sign in
        </button>
      </p>
    </div>
  );
}
