import { useRegister } from '../../hooks/Auth/useRegister';
import { RegisterCredentialsForm } from './RegisterCredentialsForm';
import { RoleSelection } from './RoleSelection';

interface RegisterViewProps {
  onSwitchToLogin: () => void;
}

export function RegisterView({ onSwitchToLogin }: RegisterViewProps) {
  const {
    email,
    password,
    confirmPassword,
    confirmPasswordError,
    codeforcesHandle,
    selectedRole,
    isSubmitting,
    setEmail,
    setPassword,
    setConfirmPassword,
    setCodeforcesHandle,
    selectRole,
    handleSignUp,
  } = useRegister();

  return (
    <div className="flex flex-col md:flex-row">
      <div className="flex-1">
        <RegisterCredentialsForm
          email={email}
          password={password}
          confirmPassword={confirmPassword}
          confirmPasswordError={confirmPasswordError}
          codeforcesHandle={codeforcesHandle}
          isSubmitting={isSubmitting}
          onEmailChange={setEmail}
          onPasswordChange={setPassword}
          onConfirmPasswordChange={setConfirmPassword}
          onCodeforcesHandleChange={setCodeforcesHandle}
          onSubmit={handleSignUp}
          onSwitchToLogin={onSwitchToLogin}
        />
      </div>
      <div className="hidden w-px shrink-0 bg-accent/20 md:block" />
      <div className="flex-1 border-t border-accent/20 md:border-t-0">
        <RoleSelection selectedRole={selectedRole} onSelectRole={selectRole} />
      </div>
    </div>
  );
}
