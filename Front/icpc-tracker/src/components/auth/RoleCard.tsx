import type { LucideIcon } from 'lucide-react';

interface RoleCardProps {
  icon: LucideIcon;
  title: string;
  description: string;
  isActive: boolean;
  onClick: () => void;
}

export function RoleCard({ icon: Icon, title, description, isActive, onClick }: RoleCardProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={[
        'flex flex-1 flex-col items-start gap-3 rounded-xl border p-5 text-left transition-all',
        isActive
          ? 'border-accent bg-accent-muted shadow-[0_0_20px_rgba(86,193,242,0.1)]'
          : 'border-surface-border bg-surface-input/50 hover:border-surface-border/80',
      ].join(' ')}
    >
      <div
        className={[
          'flex h-10 w-10 items-center justify-center rounded-lg',
          isActive ? 'bg-accent/20 text-accent' : 'bg-surface-border/50 text-muted',
        ].join(' ')}
      >
        <Icon size={20} />
      </div>
      <div>
        <h3 className="text-base font-semibold text-white">{title}</h3>
        <p className="mt-1 text-sm leading-relaxed text-muted">{description}</p>
      </div>
    </button>
  );
}
