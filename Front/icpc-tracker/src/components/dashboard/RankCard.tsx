import { ArrowUp, ArrowDown } from 'lucide-react';

interface RankCardProps {
  rank: number;
  change: number;
  isPositive: boolean;
}

export function RankCard({ rank, change, isPositive }: RankCardProps) {
  return (
    <div className="glass-panel p-6 flex flex-col items-center justify-center h-full min-h-[160px]">
      <span className="text-sm font-semibold text-dashboard-muted mb-3">Global Rank</span>
      <span className="text-4xl sm:text-5xl font-bold text-dashboard-primary mb-4 tracking-tight">
        #{rank.toLocaleString()}
      </span>
      <div className={`flex items-center gap-1 text-sm font-medium ${isPositive ? 'text-emerald-400' : 'text-red-500'}`}>
        {isPositive ? <ArrowUp size={16} strokeWidth={3} /> : <ArrowDown size={16} strokeWidth={3} />}
        <span>{change} this week</span>
      </div>
    </div>
  );
}
