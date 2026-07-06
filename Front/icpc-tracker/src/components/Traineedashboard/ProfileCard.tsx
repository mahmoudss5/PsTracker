import { Flame } from 'lucide-react';

interface ProfileCardProps {
  username: string;
  avatarUrl: string;
  title: string;
  streak: number;
  bio?: string;
  codeforcesHandle?: string;
  maxRank?: string;
  maxRate?: number;
  rate?: number;
  rank?: string;
}

export function ProfileCard({ username, avatarUrl, title, streak, bio, codeforcesHandle, maxRank, maxRate, rate, rank }: ProfileCardProps) {
  return (
    <div className="glass-panel p-4 sm:p-6 flex flex-col sm:flex-row gap-4 sm:gap-6 items-start sm:items-center h-full">
      <img
        src={avatarUrl}
        alt={username}
        className="h-32 w-32 sm:h-48 sm:w-48 shrink-0 rounded-2xl border-2 border-dashboard-border object-cover bg-dashboard-elevated shadow-xl"
      />
      <div className="flex-1 min-w-0 w-full">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-3">
          <h1 className="text-3xl sm:text-4xl font-extrabold text-dashboard-text leading-none break-words">{username}</h1>
          <span className="flex items-center gap-2 text-base sm:text-lg font-medium text-dashboard-muted shrink-0">
            <Flame size={20} className="text-orange-500 animate-pulse" />
            {streak} Day Streak
          </span>
        </div>
        
        <div className="flex flex-wrap items-center gap-4 mb-4">
          <span className="rounded bg-dashboard-primary/20 border border-dashboard-primary/30 px-3 py-1.5 text-sm sm:text-base font-bold tracking-wider text-dashboard-primary">
            {title}
          </span>
          {codeforcesHandle && (
            <span className="rounded bg-purple-500/10 border border-purple-500/20 px-3 py-1.5 text-base sm:text-lg font-bold tracking-wide text-purple-400 flex items-center gap-2 shadow-sm">
              <span className="text-purple-500/60 font-bold">CF:</span> {codeforcesHandle}
            </span>
          )}
        </div>

        {bio && (
          <p className="text-sm font-medium leading-relaxed text-dashboard-muted whitespace-pre-line mb-4">
            {bio}
          </p>
        )}

        <div className="flex items-center gap-8 pt-6 border-t border-dashboard-border/30 mt-auto">
          <div className="flex flex-col">
            <span className="text-xs sm:text-sm font-bold text-dashboard-muted uppercase tracking-widest mb-1.5">Rank</span>
            <span className="text-lg sm:text-xl font-bold text-dashboard-text/90 bg-dashboard-primary/10 px-3 py-1 rounded border border-dashboard-primary/10">
              {rank || 'Unrated'} {(maxRank && maxRank !== rank && maxRank !== 'Unrated') ? `(Max: ${maxRank})` : ''}
            </span>
          </div>
          <div className="h-12 w-px bg-dashboard-border/30" />
          <div className="flex flex-col">
            <span className="text-xs sm:text-sm font-bold text-dashboard-muted uppercase tracking-widest mb-1.5">Rating</span>
            <span className="text-lg sm:text-xl font-bold text-dashboard-primary px-3 py-1">
              {rate || 0} {maxRate ? `(Max: ${maxRate})` : ''}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
