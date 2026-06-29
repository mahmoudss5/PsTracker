import { CheckCircle, Terminal, Flame, Zap, Trophy, CalendarDays } from 'lucide-react';
import type { TraineeSubmissionsStats } from '../../types/dashboard.types';

interface StatCardProps {
  title: string;
  value: string;
  sub: string;
  accent: 'blue' | 'green' | 'orange' | 'purple' | 'yellow' | 'cyan';
  icon: React.ReactNode;
}

const ACCENT_CLASSES: Record<StatCardProps['accent'], { ring: string; bg: string; text: string }> = {
  blue:   { ring: 'ring-dashboard-primary/10', bg: 'bg-dashboard-primary/10', text: 'text-dashboard-primary' },
  green:  { ring: 'ring-emerald-500/10',        bg: 'bg-emerald-500/10',        text: 'text-emerald-500' },
  orange: { ring: 'ring-orange-500/10',          bg: 'bg-orange-500/10',          text: 'text-orange-500' },
  purple: { ring: 'ring-purple-500/10',          bg: 'bg-purple-500/10',          text: 'text-purple-500' },
  yellow: { ring: 'ring-yellow-500/10',          bg: 'bg-yellow-500/10',          text: 'text-yellow-500' },
  cyan:   { ring: 'ring-cyan-500/10',            bg: 'bg-cyan-500/10',            text: 'text-cyan-500' },
};

function StatCard({ title, value, sub, accent, icon }: StatCardProps) {
  const a = ACCENT_CLASSES[accent];
  return (
    <div className="glass-panel p-5 flex items-start justify-between relative overflow-hidden group hover:border-dashboard-primary/30 transition-all duration-300">
      <div className="absolute inset-0 bg-gradient-to-br from-dashboard-primary/5 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none" />
      <div className="space-y-2 z-10 min-w-0">
        <span className="text-[10px] font-bold uppercase tracking-wider text-dashboard-muted block">
          {title}
        </span>
        <span className="text-2xl sm:text-3xl font-extrabold tracking-tight text-dashboard-text block leading-none">
          {value}
        </span>
        <span className="text-xs font-medium text-dashboard-muted block truncate">{sub}</span>
      </div>
      <div className={`z-10 shrink-0 ml-3 flex h-10 w-10 items-center justify-center rounded-full ring-4 ${a.bg} ${a.text} ${a.ring}`}>
        {icon}
      </div>
    </div>
  );
}

interface SubmissionStatsBarProps {
  stats: TraineeSubmissionsStats;
}

export function SubmissionStatsBar({ stats }: SubmissionStatsBarProps) {
  const {
    totalSubmissions,
    acceptanceRate,
    longestStreak,
    problemsSolved,
    avgRuntimeMs,
    mostActiveDay,
  } = stats;

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 sm:gap-4">
      <StatCard
        title="Total Submissions"
        value={totalSubmissions.toLocaleString()}
        sub="in selected period"
        accent="blue"
        icon={<Terminal size={18} className="stroke-[2]" />}
      />
      <StatCard
        title="Acceptance Rate"
        value={`${acceptanceRate}%`}
        sub={`${stats.acceptedCount} accepted`}
        accent="green"
        icon={<CheckCircle size={18} className="stroke-[2]" />}
      />
      <StatCard
        title="Problems Solved"
        value={problemsSolved.toLocaleString()}
        sub="unique problems"
        accent="purple"
        icon={<Trophy size={18} className="stroke-[2]" />}
      />
      <StatCard
        title="Longest Streak"
        value={`${longestStreak}d`}
        sub="consecutive days"
        accent="orange"
        icon={<Flame size={18} className="stroke-[2]" />}
      />
      <StatCard
        title="Avg. Runtime"
        value={avgRuntimeMs > 0 ? `${avgRuntimeMs}ms` : '—'}
        sub="accepted submissions"
        accent="cyan"
        icon={<Zap size={18} className="stroke-[2]" />}
      />
      <StatCard
        title="Most Active Day"
        value={mostActiveDay}
        sub="day of the week"
        accent="yellow"
        icon={<CalendarDays size={18} className="stroke-[2]" />}
      />
    </div>
  );
}
