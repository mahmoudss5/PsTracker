import type { TraineeSubmissionsStats } from '../../types/dashboard.types';

interface SubmissionActivityChartProps {
  stats: TraineeSubmissionsStats;
}

export function SubmissionActivityChart({ stats }: SubmissionActivityChartProps) {
  const { dailyActivity } = stats;

  const maxCount = Math.max(...dailyActivity.map((d) => d.count), 1);

  const chartHeight = 140;
  const chartWidth = 460;
  const paddingLeft = 32;
  const paddingRight = 16;
  const paddingTop = 16;
  const paddingBottom = 20;
  const innerWidth = chartWidth - paddingLeft - paddingRight;
  const innerHeight = chartHeight - paddingTop - paddingBottom;

  // Decide how many x-axis labels to show to avoid clutter
  const totalPoints = dailyActivity.length;
  const labelStep = totalPoints <= 7 ? 1 : totalPoints <= 14 ? 2 : totalPoints <= 30 ? 5 : 7;

  const points = dailyActivity.map((d, i) => ({
    x: paddingLeft + (i / Math.max(totalPoints - 1, 1)) * innerWidth,
    y: paddingTop + innerHeight - (d.count / maxCount) * innerHeight,
    date: d.date,
    count: d.count,
  }));

  const linePath = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ');
  const areaPath = `
    ${linePath}
    L ${points[points.length - 1].x} ${paddingTop + innerHeight}
    L ${points[0].x} ${paddingTop + innerHeight}
    Z
  `;

  const gridValues = [0, Math.ceil(maxCount / 2), maxCount];
  const yLabels = gridValues.map((val) => ({
    value: val,
    y: paddingTop + innerHeight - (val / maxCount) * innerHeight,
  }));

  // Format date for display: "Jun 15"
  const formatDate = (iso: string) => {
    const d = new Date(iso);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  return (
    <div className="glass-panel p-6 flex flex-col gap-4 hover:border-dashboard-primary/30 transition-all duration-300">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-bold tracking-tight text-dashboard-text">
          Daily Activity
        </h3>
        <span className="text-xs font-semibold text-dashboard-muted">
          Submissions per day
        </span>
      </div>

      {totalPoints === 0 || (totalPoints === 1 && dailyActivity[0]?.count === 0) ? (
        <div className="flex flex-1 items-center justify-center h-32 text-dashboard-muted text-sm italic">
          No activity in this period
        </div>
      ) : (
        <>
          <div className="relative w-full overflow-hidden">
            <svg
              viewBox={`0 0 ${chartWidth} ${chartHeight}`}
              className="w-full h-auto overflow-visible"
            >
              <defs>
                <linearGradient id="activity-gradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="rgb(var(--dashboard-primary))" stopOpacity="0.25" />
                  <stop offset="100%" stopColor="rgb(var(--dashboard-primary))" stopOpacity="0.0" />
                </linearGradient>
              </defs>

              {/* Grid lines */}
              {yLabels.map(({ value, y }) => (
                <g key={value} className="opacity-40">
                  <line
                    x1={paddingLeft} y1={y}
                    x2={chartWidth - paddingRight} y2={y}
                    stroke="rgb(var(--dashboard-border))"
                    strokeWidth="1"
                    strokeDasharray="4 6"
                  />
                  <text
                    x={paddingLeft - 6}
                    y={y + 4}
                    textAnchor="end"
                    fontSize="9"
                    fontFamily="monospace"
                    fill="rgb(var(--dashboard-muted))"
                    fontWeight="600"
                  >
                    {value}
                  </text>
                </g>
              ))}

              {/* Area fill */}
              <path d={areaPath} fill="url(#activity-gradient)" />

              {/* Line */}
              <path
                d={linePath}
                fill="none"
                stroke="rgb(var(--dashboard-primary))"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              />

              {/* Dots with tooltip */}
              {points.map((p, i) => (
                <g key={i} className="group/dot">
                  <circle cx={p.x} cy={p.y} r="10" fill="rgb(var(--dashboard-primary))" fillOpacity="0.12"
                    className="opacity-0 group-hover/dot:opacity-100 transition-opacity duration-200" />
                  <circle
                    cx={p.x} cy={p.y} r="4.5"
                    fill="rgb(var(--dashboard-panel))"
                    stroke="rgb(var(--dashboard-primary))"
                    strokeWidth="2.5"
                    className="cursor-pointer"
                  />
                  {/* Tooltip */}
                  <g className="opacity-0 group-hover/dot:opacity-100 transition-opacity duration-200 pointer-events-none">
                    <rect
                      x={p.x - 30} y={p.y - 34}
                      width="60" height="22"
                      rx="4"
                      fill="rgb(var(--dashboard-panel))"
                      stroke="rgb(var(--dashboard-border))"
                      strokeWidth="1"
                    />
                    <text
                      x={p.x} y={p.y - 20}
                      textAnchor="middle"
                      fontSize="9"
                      fontFamily="monospace"
                      fill="rgb(var(--dashboard-text))"
                      fontWeight="700"
                    >
                      {p.count} sub{p.count !== 1 ? 's' : ''}
                    </text>
                  </g>
                </g>
              ))}
            </svg>
          </div>

          {/* X-axis labels */}
          <div className="flex justify-between pl-[32px] pr-[16px]">
            {points.map((p, i) =>
              i % labelStep === 0 ? (
                <span
                  key={i}
                  className="text-[9px] sm:text-[10px] font-mono font-semibold text-dashboard-muted text-center"
                  style={{ width: `${(1 / totalPoints) * 100}%`, minWidth: 0 }}
                >
                  {formatDate(p.date)}
                </span>
              ) : null
            )}
          </div>
        </>
      )}
    </div>
  );
}
