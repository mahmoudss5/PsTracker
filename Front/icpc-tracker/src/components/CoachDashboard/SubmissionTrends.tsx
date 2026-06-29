import { TrendingUp } from 'lucide-react';

interface SubmissionTrendsProps {
  acceptanceRatio: number;
  dailyActivityTrend: number;
  dailyActivityData: number[];
}

export function SubmissionTrends({
  acceptanceRatio,
  dailyActivityTrend,
  dailyActivityData,
}: SubmissionTrendsProps) {
  // Normalize data for the mini bar chart
  const maxVal = Math.max(...dailyActivityData, 1);
  const normalizedData = dailyActivityData.map((val) => (val / maxVal) * 100);

  return (
    <div className="glass-panel p-5 space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-bold text-dashboard-text flex items-center gap-2">
          Submission Trends
        </h3>
        <TrendingUp size={16} className="text-dashboard-muted" />
      </div>

      <div className="space-y-2">
        <div className="flex justify-between items-end">
          <span className="text-[10px] font-bold uppercase tracking-wider text-dashboard-muted">Acceptance Ratio</span>
          <span className="text-emerald-400 font-bold text-sm">{acceptanceRatio}%</span>
        </div>
        <div className="h-1.5 w-full bg-dashboard-panel rounded-full overflow-hidden">
          <div 
            className="h-full bg-emerald-400 rounded-full" 
            style={{ width: `${acceptanceRatio}%` }}
          />
        </div>
      </div>

      <div className="space-y-3 pt-2">
        <div className="flex justify-between items-end">
          <span className="text-[10px] font-bold uppercase tracking-wider text-dashboard-muted">Daily Activity</span>
          <span className="text-dashboard-text font-bold text-sm">
            {dailyActivityTrend > 0 ? '+' : ''}{dailyActivityTrend}%
          </span>
        </div>
        
        {/* Simple CSS Bar Chart */}
        <div className="flex items-end justify-between h-16 gap-1 border-b border-dashboard-border/50 pb-1">
          {normalizedData.map((height, i) => (
            <div 
              key={i} 
              className={`w-full rounded-t-sm transition-all duration-500 hover:bg-dashboard-primary/80 ${i === normalizedData.length - 2 ? 'bg-dashboard-primary' : 'bg-dashboard-primary/30'}`}
              style={{ height: `${height}%` }}
              title={`Activity: ${dailyActivityData[i]}`}
            />
          ))}
        </div>
      </div>

      <div className="text-xs text-dashboard-muted pt-2">
        Last updated: {new Date().toLocaleTimeString('en-US', { hour12: false })} UTC
      </div>
    </div>
  );
}
