import { CheckCircle } from 'lucide-react';

interface RankCardProps {
  acceptanceRate: number; // e.g. 31 for 31%
  totalSubmissions: number;
}

export function RankCard({ acceptanceRate, totalSubmissions }: RankCardProps) {
  // Color based on acceptance rate quality
  const rateColor =
    acceptanceRate >= 60
      ? 'text-emerald-400'
      : acceptanceRate >= 40
      ? 'text-yellow-400'
      : 'text-red-400';

  const ringColor =
    acceptanceRate >= 60
      ? 'ring-emerald-400/20 bg-emerald-400/10 text-emerald-400'
      : acceptanceRate >= 40
      ? 'ring-yellow-400/20 bg-yellow-400/10 text-yellow-400'
      : 'ring-red-400/20 bg-red-400/10 text-red-400';

  // Donut mini-chart values
  const radius = 28;
  const circumference = 2 * Math.PI * radius;
  const dashFill = (acceptanceRate / 100) * circumference;

  return (
    <div className="glass-panel p-6 flex flex-col items-center justify-center h-full min-h-[160px] gap-4 hover:border-dashboard-primary/30 transition-all duration-300 relative overflow-hidden group">
      {/* Subtle hover highlight */}
      <div className="absolute inset-0 bg-gradient-to-br from-dashboard-primary/5 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none" />

      <div className={`flex h-10 w-10 items-center justify-center rounded-full ring-4 ${ringColor} z-10`}>
        <CheckCircle size={20} className="stroke-[2]" />
      </div>

      <span className="text-xs font-bold uppercase tracking-wider text-dashboard-muted z-10">
        Acceptance Rate
      </span>

      {/* Mini donut ring + centered percentage */}
      <div className="relative flex items-center justify-center z-10">
        <svg viewBox="0 0 72 72" className="w-28 h-28 -rotate-90">
          {/* Track */}
          <circle
            cx="36"
            cy="36"
            r={radius}
            fill="transparent"
            stroke="rgb(var(--dashboard-border) / 0.3)"
            strokeWidth="6"
          />
          {/* Fill arc */}
          <circle
            cx="36"
            cy="36"
            r={radius}
            fill="transparent"
            stroke={
              acceptanceRate >= 60
                ? '#34d399'
                : acceptanceRate >= 40
                ? '#facc15'
                : '#f87171'
            }
            strokeWidth="6"
            strokeLinecap="round"
            strokeDasharray={`${dashFill} ${circumference}`}
            className="transition-all duration-700"
          />
        </svg>
        {/* Centered label */}
        <div className="absolute flex flex-col items-center">
          <span className={`text-2xl font-extrabold tracking-tight ${rateColor}`}>
            {acceptanceRate}%
          </span>
        </div>
      </div>

      <p className="text-xs text-dashboard-muted z-10">
        from{' '}
        <span className="font-semibold text-dashboard-text">
          {totalSubmissions.toLocaleString()}
        </span>{' '}
        submissions
      </p>
    </div>
  );
}
