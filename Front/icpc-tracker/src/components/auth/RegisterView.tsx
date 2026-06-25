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

    username,
    codeforcesHandle,
    selectedRole,
    isSubmitting,
    setEmail,
    setUserName,
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
          codeforcesHandle={codeforcesHandle}
          username={username}
          isSubmitting={isSubmitting}
          onEmailChange={setEmail}
          onPasswordChange={setPassword}
          onConfirmPasswordChange={setConfirmPassword}
          onCodeforcesHandleChange={setCodeforcesHandle}
          onUsernameChange={setUserName}
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
