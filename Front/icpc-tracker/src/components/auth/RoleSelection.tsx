import { GraduationCap, Terminal } from 'lucide-react';
import type { UserRole } from '../../types/auth.types';
import { RoleCard } from './RoleCard';

const ROLES: { role: UserRole; icon: typeof Terminal; title: string; description: string }[] = [
  {
    role: 'trainee',
    icon: Terminal,
    title: 'Trainee',
    description: 'I want to track my progress and solve assignments.',
  },
  {
    role: 'coach',
    icon: GraduationCap,
    title: 'Coach',
    description: 'I want to manage trainees and assign problems.',
  },
];

interface RoleSelectionProps {
  selectedRole: UserRole | null;
  onSelectRole: (role: UserRole) => void;
}

export function RoleSelection({ selectedRole, onSelectRole }: RoleSelectionProps) {
  return (
    <div className="flex flex-col justify-center px-8 py-10 md:px-10">
      <h2 className="text-2xl font-semibold text-white">Select your role</h2>
      <p className="mt-2 text-sm text-muted">Choose how you&apos;ll be using AlgoTrack.</p>

      <div className="mt-8 flex flex-col gap-4 sm:flex-row">
        {ROLES.map(({ role, icon, title, description }) => (
          <RoleCard
            key={role}
            icon={icon}
            title={title}
            description={description}
            isActive={selectedRole === role}
            onClick={() => onSelectRole(role)}
          />
        ))}
      </div>
    </div>
  );
}
