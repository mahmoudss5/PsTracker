export function RecentVerdictsChart() {
  // Percentages matching the screenshot
  const acPercent = 62;
  const waPercent = 24;
  const tlePercent = 14;

  const radius = 50;
  const strokeWidth = 12;
  const circumference = 2 * Math.PI * radius;

  // Dash offsets
  const acDash = (acPercent / 100) * circumference;
  const waDash = (waPercent / 100) * circumference;
  const tleDash = (tlePercent / 100) * circumference;

  const acOffset = 0;
  const waOffset = -acDash;
  const tleOffset = -(acDash + waDash);

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
      <div className="space-y-2 mt-4">
        {/* AC Item */}
        <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-dashboard-primary/20 transition-all">
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
      </div>

      <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-red-500/20 transition-all">
        <div className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-full bg-blue-500" />
          <span className="font-semibold text-dashboard-text">RE</span>
        </div>
        <span className="font-mono text-dashboard-muted font-bold">{waPercent}%</span>
      </div>

      <div className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 hover:border-red-500/20 transition-all">
        <div className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-full bg-gray-500" />
          <span className="font-semibold text-dashboard-text">ME</span>
        </div>
        <span className="font-mono text-dashboard-muted font-bold">{waPercent}%</span>
      </div>

    </div>

  );
}
