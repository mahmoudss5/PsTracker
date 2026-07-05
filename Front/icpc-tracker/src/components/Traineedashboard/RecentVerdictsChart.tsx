interface RecentVerdictsChartProps {
  ac: number;
  wa: number;
  tle: number;
  mle: number;
  re: number;
  total: number;
}

export function RecentVerdictsChart({ ac, wa, tle, mle, re, total }: RecentVerdictsChartProps) {
  // Calculate percentages
  const getPercent = (val: number) => (total > 0 ? Math.round((val / total) * 100) : 0);
  
  const acPercent = getPercent(ac);
  const waPercent = getPercent(wa);
  const tlePercent = getPercent(tle);
  const mlePercent = getPercent(mle);
  const rePercent = getPercent(re);

  const radius = 50;
  const strokeWidth = 12;
  const circumference = 2 * Math.PI * radius;

  // Dash offsets
  const acDash = (acPercent / 100) * circumference;
  const waDash = (waPercent / 100) * circumference;
  const tleDash = (tlePercent / 100) * circumference;
  const mleDash = (mlePercent / 100) * circumference;
  const reDash = (rePercent / 100) * circumference;

  const acOffset = 0;
  const waOffset = -acDash;
  const tleOffset = -(acDash + waDash);
  const mleOffset = -(acDash + waDash + tleDash);
  const reOffset = -(acDash + waDash + tleDash + mleDash);

  return (
    <div className="glass-panel p-6 flex flex-col justify-between hover:border-dashboard-primary/30 transition-all duration-300">
      <div>
        <h3 className="text-base font-bold tracking-tight text-dashboard-text mb-6">
          Recent Verdicts
        </h3>

        {/* Donut Chart Container */}
        <div className="relative flex items-center justify-center my-4 h-36">
          <svg
            viewBox="0 0 120 120"
            className="w-32 h-32 transform -rotate-90 overflow-visible"
          >
            {/* Background circle track */}
            <circle
              cx="60"
              cy="60"
              r={radius}
              fill="transparent"
              stroke="rgb(var(--dashboard-border) / 0.3)"
              strokeWidth={strokeWidth}
            />

            {/* AC Segment */}
            <circle
              cx="60"
              cy="60"
              r={radius}
              fill="transparent"
              stroke="rgb(var(--dashboard-primary))"
              strokeWidth={strokeWidth}
              strokeDasharray={`${acDash} ${circumference}`}
              strokeDashoffset={acOffset}
              strokeLinecap="round"
              className="transition-all duration-500 hover:opacity-80 cursor-pointer"
            />

            {/* WA Segment */}
            <circle
              cx="60"
              cy="60"
              r={radius}
              fill="transparent"
              stroke="#ef4444" // standard red
              strokeWidth={strokeWidth}
              strokeDasharray={`${waDash} ${circumference}`}
              strokeDashoffset={waOffset}
              strokeLinecap="round"
              className="transition-all duration-500 hover:opacity-80 cursor-pointer"
            />

            {/* TLE Segment */}
            <circle
              cx="60"
              cy="60"
              r={radius}
              fill="transparent"
              stroke="#f97316" // orange
              strokeWidth={strokeWidth}
              strokeDasharray={`${tleDash} ${circumference}`}
              strokeDashoffset={tleOffset}
              strokeLinecap="round"
              className="transition-all duration-500 hover:opacity-80 cursor-pointer"
            />

            {/* MLE Segment */}
            <circle
              cx="60"
              cy="60"
              r={radius}
              fill="transparent"
              stroke="#6b7280" // gray
              strokeWidth={strokeWidth}
              strokeDasharray={`${mleDash} ${circumference}`}
              strokeDashoffset={mleOffset}
              strokeLinecap="round"
              className="transition-all duration-500 hover:opacity-80 cursor-pointer"
            />
            
            {/* RE Segment */}
            <circle
              cx="60"
              cy="60"
              r={radius}
              fill="transparent"
              stroke="#3b82f6" // blue
              strokeWidth={strokeWidth}
              strokeDasharray={`${reDash} ${circumference}`}
              strokeDashoffset={reOffset}
              strokeLinecap="round"
              className="transition-all duration-500 hover:opacity-80 cursor-pointer"
            />
          </svg>

          {/* Central absolute stats overlay */}
          <div className="absolute flex flex-col items-center justify-center text-center">
            <span className="text-2xl font-extrabold tracking-tight text-dashboard-text">
              {acPercent}%
            </span>
            <span className="text-[10px] uppercase font-bold tracking-wider text-dashboard-muted">
              AC Rate
            </span>
          </div>
        </div>
      </div>

      {/* Legend below matching user designs */}
      <div className="space-y-2 mt-4 grid grid-cols-2 gap-2">
        {/* AC Item */}
        <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-dashboard-primary/20 transition-all col-span-2">
          <div className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full bg-dashboard-primary" />
            <span className="font-semibold text-dashboard-text">AC</span>
          </div>
          <span className="font-mono text-dashboard-muted font-bold">{acPercent}%</span>
        </div>

        {/* WA Item */}
        <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-red-500/20 transition-all">
          <div className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full bg-red-500" />
            <span className="font-semibold text-dashboard-text">WA</span>
          </div>
          <span className="font-mono text-dashboard-muted font-bold">{waPercent}%</span>
        </div>

        {/* TLE Item */}
        <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-orange-500/20 transition-all">
          <div className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full bg-orange-500" />
            <span className="font-semibold text-dashboard-text">TLE</span>
          </div>
          <span className="font-mono text-dashboard-muted font-bold">{tlePercent}%</span>
        </div>

        {/* RE Item */}
        <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-blue-500/20 transition-all">
          <div className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full bg-blue-500" />
            <span className="font-semibold text-dashboard-text">RE</span>
          </div>
          <span className="font-mono text-dashboard-muted font-bold">{rePercent}%</span>
        </div>

        {/* MLE Item */}
        <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-gray-500/20 transition-all">
          <div className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full bg-gray-500" />
            <span className="font-semibold text-dashboard-text">MLE</span>
          </div>
          <span className="font-mono text-dashboard-muted font-bold">{mlePercent}%</span>
        </div>
      </div>
    </div>

  );
}
