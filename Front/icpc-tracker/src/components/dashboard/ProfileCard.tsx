import { Flame } from 'lucide-react';

interface ProfileCardProps {
  username: string;
  avatarUrl: string;
  title: string;
  streak: number;
  bio: string;
}

export function ProfileCard({ username, avatarUrl, title, streak, bio }: ProfileCardProps) {
  return (
    <div className="glass-panel p-6 flex flex-col sm:flex-row gap-6 items-start sm:items-center h-full">
      <img
        src={avatarUrl}
        alt={username}
        className="h-28 w-28 shrink-0 rounded-2xl border border-dashboard-border object-cover bg-dashboard-elevated shadow-lg"
      />
      <div className="flex-1">
        <h1 className="text-3xl font-bold text-dashboard-text mb-3">{username}</h1>
        <div className="flex flex-wrap items-center gap-4 mb-4">
          <span className="rounded bg-dashboard-primary/20 border border-dashboard-primary/30 px-2.5 py-1 text-xs font-bold tracking-wider text-dashboard-primary mt-1">
            {title}
          </span>
          <span className="flex items-center gap-1.5 text-sm font-medium text-dashboard-muted mt-1">
            <Flame size={16} className="text-dashboard-muted" />
            {streak} Day Streak
          </span>
        </div>
        <p className="text-sm font-medium leading-relaxed text-dashboard-muted whitespace-pre-line">
          {bio}
        </p>
      </div>
    </div>
  );
}
