import { Users } from 'lucide-react';
import type { TeamResponse } from '../../types/api.types';

interface ActiveTeamsListProps {
  teams: TeamResponse[];
}

function initials(name: string): string {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join('') || '?';
}

function avgRating(team: TeamResponse): number | null {
  const withRate = team.trainees.filter((t) => t.rate > 0);
  if (!withRate.length) return null;
  const sum = withRate.reduce((s, t) => s + t.rate, 0);
  return Math.round(sum / withRate.length);
}

import { useCopyToClipboard } from '../../hooks/useCopyToClipboard';
import { toast } from 'sonner';

export function ActiveTeamsList({ teams }: ActiveTeamsListProps) {
  if (teams.length === 0) {
    return (
      <div className="glass-panel p-8 flex flex-col items-center justify-center text-dashboard-muted text-center">
        <Users size={24} className="mb-2 opacity-40" />
        <p className="text-sm font-semibold">No teams yet</p>
        <p className="text-xs mt-1">Create your first team above.</p>
      </div>
    );
  }

  const CopyableTeamCodeBadge = ({ code }: { code: string }) => {
    const { copied, copy } = useCopyToClipboard();
    return (
      <div className="flex items-center gap-1">
        <span 
          className="shrink-0 font-mono text-[10px] text-dashboard-muted bg-dashboard-elevated border border-dashboard-border rounded px-1.5 py-0.5 cursor-pointer hover:text-dashboard-text transition-colors"
          title="Double-click to copy"
          onDoubleClick={(e) => {
            e.stopPropagation();
            copy(code);
          }}
        >
          {code}
        </span>
        {copied && <span className="text-[9px] text-emerald-500 font-bold">Copied!</span>}
      </div>
    );
  };

  return (
    <div className="glass-panel p-5 space-y-4">
      <div className="flex items-center justify-between mb-1">
        <h3 className="text-sm font-bold text-dashboard-text">Active Teams</h3>
        <span className="text-xs text-dashboard-muted">{teams.length} team{teams.length !== 1 ? 's' : ''}</span>
      </div>

      <div className="space-y-3">
        {teams.map((team) => {
          const rating = avgRating(team);
          return (
            <div
              key={team.id}
              className="flex items-center justify-between gap-4 border-b border-dashboard-border/50 pb-3 last:border-0 last:pb-0"
            >
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <p className="text-sm font-bold text-dashboard-text truncate">{team.teamName}</p>
                  <CopyableTeamCodeBadge code={team.teamCode} />
                </div>
                <div className="flex items-center gap-1.5 mt-1.5">
                  {/* Member avatar bubbles */}
                  {team.trainees.slice(0, 4).map((t) => (
                    <div
                      key={t.id}
                      title={t.userName}
                      className="flex h-5 w-5 items-center justify-center rounded-full bg-dashboard-elevated border border-dashboard-border text-[9px] font-bold text-dashboard-muted"
                    >
                      {initials(t.userName)}
                    </div>
                  ))}
                  {team.trainees.length === 0 && (
                    <span className="text-[10px] text-dashboard-muted">No members yet</span>
                  )}
                </div>
              </div>

              {rating !== null && (
                <div className="shrink-0 text-right">
                  <p className="text-xs text-dashboard-muted">Avg rating</p>
                  <p className="text-sm font-bold text-dashboard-primary">{rating.toLocaleString()}</p>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
