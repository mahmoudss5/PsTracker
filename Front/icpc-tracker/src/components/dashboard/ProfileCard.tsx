import { Flame } from 'lucide-react';

interface ProfileCardProps {
  username: string;
  avatarUrl: string;
  title: string;
  streak: number;
  bio: string;
  maxRank?: string;
  maxRate?: number;
}

export function ProfileCard({ username, avatarUrl, title, streak, bio, maxRank, maxRate }: ProfileCardProps) {
  return (
    <div className="glass-panel p-4 sm:p-6 flex flex-col sm:flex-row gap-4 sm:gap-6 items-start sm:items-center h-full">
      <img
        src={avatarUrl}
        alt={username}
        className="h-20 w-20 sm:h-28 sm:w-28 shrink-0 rounded-2xl border border-dashboard-border object-cover bg-dashboard-elevated shadow-lg"
      />
      <div className="flex-1 min-w-0 w-full">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-3">
          <h1 className="text-2xl sm:text-3xl font-bold text-dashboard-text leading-none break-words">{username}</h1>
          <span className="flex items-center gap-1.5 text-sm font-medium text-dashboard-muted shrink-0">
            <Flame size={16} className="text-orange-500 animate-pulse" />
            {streak} Day Streak
          </span>
        </div>
        
        <div className="flex flex-wrap items-center gap-2 mb-4">
          <span className="rounded bg-dashboard-primary/20 border border-dashboard-primary/30 px-2.5 py-1 text-xs font-bold tracking-wider text-dashboard-primary">
            {title}
          </span>
        </div>

        <p className="text-sm font-medium leading-relaxed text-dashboard-muted whitespace-pre-line mb-4">
          {bio}
        </p>

        <div className="flex items-center gap-6 pt-4 border-t border-dashboard-border/30">
          <div className="flex flex-col">
            <span className="text-[10px] font-bold text-dashboard-muted uppercase tracking-widest mb-0.5">Max Rank</span>
            <span className="text-sm font-bold text-dashboard-text/90 bg-dashboard-primary/10 px-2 py-0.5 rounded border border-dashboard-primary/10">{maxRank || 'N/A'}</span>
          </div>
          <div className="h-8 w-px bg-dashboard-border/30" />
          <div className="flex flex-col">
            <span className="text-[10px] font-bold text-dashboard-muted uppercase tracking-widest mb-0.5">Max Rate</span>
            <span className="text-sm font-bold text-dashboard-primary">{maxRate ? `${maxRate} max` : 'N/A'}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
