import type { TraineeSubmissionsStats, Verdict } from '../../types/dashboard.types';

interface SubmissionVerdictsChartProps {
  stats: TraineeSubmissionsStats;
}

const VERDICT_META: Record<Verdict, { label: string; color: string; stroke: string }> = {
  AC:  { label: 'Accepted',              color: 'rgb(var(--dashboard-primary))', stroke: 'bg-dashboard-primary' },
  WA:  { label: 'Wrong Answer',          color: '#ef4444',                        stroke: 'bg-red-500'           },
  TLE: { label: 'Time Limit Exceeded',   color: '#f97316',                        stroke: 'bg-orange-500'        },
  RE:  { label: 'Runtime Error',         color: '#a855f7',                        stroke: 'bg-purple-500'        },
  MLE: { label: 'Memory Limit Exceeded', color: '#06b6d4',                        stroke: 'bg-cyan-500'          },
};

const VERDICT_ORDER: Verdict[] = ['AC', 'WA', 'TLE', 'RE', 'MLE'];

export function SubmissionVerdictsChart({ stats }: SubmissionVerdictsChartProps) {
  const { verdictsBreakdown, languageCounts, totalSubmissions } = stats;

  // ── Donut chart ──────────────────────────────────────────────────────────────
  const radius = 50;
  const strokeWidth = 12;
  const circumference = 2 * Math.PI * radius;

  type Segment = { verdict: Verdict; pct: number; dash: number; offset: number };
  const segments: Segment[] = [];
  let cumulativeDash = 0;

  VERDICT_ORDER.forEach((v) => {
    const count = verdictsBreakdown[v];
    const pct = totalSubmissions > 0 ? (count / totalSubmissions) * 100 : 0;
    const dash = (pct / 100) * circumference;
    segments.push({ verdict: v, pct, dash, offset: -cumulativeDash });
    cumulativeDash += dash;
  });

  // ── Language bar chart ────────────────────────────────────────────────────────
  const langEntries = Object.entries(languageCounts).sort((a, b) => b[1] - a[1]);
  const maxLangCount = langEntries[0]?.[1] ?? 1;

  const LANG_COLORS = [
    'bg-dashboard-primary', 'bg-emerald-500', 'bg-orange-500',
    'bg-purple-500', 'bg-cyan-500', 'bg-yellow-500',
  ];

  return (
    <div className="glass-panel p-6 flex flex-col gap-6 hover:border-dashboard-primary/30 transition-all duration-300">
      <h3 className="text-base font-bold tracking-tight text-dashboard-text">
        Verdicts &amp; Languages
      </h3>

      <div className="flex flex-col sm:flex-row gap-6">
        {/* Left: Donut */}
        <div className="flex flex-col items-center gap-4 flex-1">
          <p className="text-xs font-bold uppercase tracking-wider text-dashboard-muted self-start">
            Verdict Breakdown
          </p>

          <div className="relative flex items-center justify-center h-36 w-36 shrink-0">
            <svg viewBox="0 0 120 120" className="w-32 h-32 transform -rotate-90 overflow-visible">
              {/* Background track */}
              <circle
                cx="60" cy="60" r={radius}
                fill="transparent"
                stroke="rgb(var(--dashboard-border) / 0.3)"
                strokeWidth={strokeWidth}
              />
              {totalSubmissions === 0 ? (
                <circle
                  cx="60" cy="60" r={radius}
                  fill="transparent"
                  stroke="rgb(var(--dashboard-border) / 0.3)"
                  strokeWidth={strokeWidth}
                />
              ) : (
                segments.map(({ verdict, dash, offset }) => (
                  <circle
                    key={verdict}
                    cx="60" cy="60" r={radius}
                    fill="transparent"
                    stroke={VERDICT_META[verdict].color}
                    strokeWidth={strokeWidth}
                    strokeDasharray={`${dash} ${circumference}`}
                    strokeDashoffset={offset}
                    strokeLinecap="round"
                    className="transition-all duration-500 hover:opacity-80 cursor-pointer"
                  />
                ))
              )}
            </svg>

            {/* Center label */}
            <div className="absolute flex flex-col items-center justify-center text-center pointer-events-none">
              <span className="text-xl font-extrabold tracking-tight text-dashboard-text">
                {stats.acceptanceRate}%
              </span>
              <span className="text-[9px] uppercase font-bold tracking-wider text-dashboard-muted">
                AC Rate
              </span>
            </div>
          </div>

          {/* Legend */}
          <div className="w-full space-y-1.5">
            {VERDICT_ORDER.map((v) => {
              const meta = VERDICT_META[v];
              const count = verdictsBreakdown[v];
              const pct = totalSubmissions > 0 ? Math.round((count / totalSubmissions) * 100) : 0;
              return (
                <div
                  key={v}
                  className="flex items-center justify-between rounded bg-dashboard-bg/30 px-3 py-1.5 text-xs border border-dashboard-border/30 transition-all hover:border-dashboard-primary/20"
                >
                  <div className="flex items-center gap-2">
                    <span className={`h-2 w-2 rounded-full ${meta.stroke}`} />
                    <span className="font-semibold text-dashboard-text font-mono">{v}</span>
                    <span className="text-dashboard-muted hidden sm:inline truncate max-w-[110px]">
                      {meta.label}
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-bold text-dashboard-muted">{count}</span>
                    <span className="font-mono font-bold text-dashboard-text w-8 text-right">{pct}%</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Divider */}
        <div className="hidden sm:block w-px bg-dashboard-border/50 self-stretch" />
        <div className="block sm:hidden h-px bg-dashboard-border/50" />

        {/* Right: Language bars */}
        <div className="flex flex-col gap-3 flex-1">
          <p className="text-xs font-bold uppercase tracking-wider text-dashboard-muted">
            Languages Used
          </p>

          {langEntries.length === 0 ? (
            <p className="text-sm text-dashboard-muted italic mt-2">No data for this period</p>
          ) : (
            <div className="space-y-3">
              {langEntries.map(([lang, count], i) => {
                const pct = Math.round((count / maxLangCount) * 100);
                return (
                  <div key={lang} className="flex flex-col gap-1">
                    <div className="flex items-center justify-between text-xs font-semibold">
                      <span className="text-dashboard-text">{lang}</span>
                      <span className="font-mono text-dashboard-muted">{count} sub{count !== 1 ? 's' : ''}</span>
                    </div>
                    <div className="h-2 w-full rounded-full bg-dashboard-elevated overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all duration-700 ${LANG_COLORS[i % LANG_COLORS.length]}`}
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
