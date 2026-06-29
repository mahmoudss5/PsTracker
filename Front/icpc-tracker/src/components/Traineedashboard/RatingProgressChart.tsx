import { useState } from "react";

// --- Rating Progress data ---
interface ContestRatingPoint {
  contestName: string;
  rating: number;
}

const contestData: ContestRatingPoint[] = [
  { contestName: "Round #842", rating: 1420 },
  { contestName: "Round #843", rating: 1680 },
  { contestName: "Round #844", rating: 1600 },
  { contestName: "Edu #145", rating: 1850 },
  { contestName: "Round #845", rating: 1940 },
];

// --- Problems Solved per Day (last 7 days) data ---
interface DailySolvedPoint {
  day: string;
  solved: number;
}

const dailySolvedData: DailySolvedPoint[] = [
  { day: "Mon", solved: 3 },
  { day: "Tue", solved: 7 },
  { day: "Wed", solved: 2 },
  { day: "Thu", solved: 5 },
  { day: "Fri", solved: 8 },
  { day: "Sat", solved: 4 },
  { day: "Sun", solved: 6 },
];

type ChartMode = "rating" | "solved";

// ------------------------------------------------------------------
// Generic SVG chart renderer
// ------------------------------------------------------------------
function LineChart({
  points,
  labels,
  yLabels,
  valueLabels,
  gradientId,
  strokeColor,
}: {
  points: { x: number; y: number }[];
  labels: string[];
  yLabels: { value: number; y: number }[];
  valueLabels: (string | number)[];
  gradientId: string;
  strokeColor: string;
}) {
  const chartHeight = 160;
  const chartWidth = 460;
  const paddingLeft = 40;
  const paddingRight = 20;

  const linePath = points.map((p, i) => `${i === 0 ? "M" : "L"} ${p.x} ${p.y}`).join(" ");
  const areaPath = `
    ${linePath}
    L ${points[points.length - 1].x} ${chartHeight - 20}
    L ${points[0].x} ${chartHeight - 20}
    Z
  `;

  return (
    <>
      <div className="relative w-full overflow-hidden mt-2">
        <svg viewBox={`0 0 ${chartWidth} ${chartHeight}`} className="w-full h-auto overflow-visible">
          <defs>
            <linearGradient id={gradientId} x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={strokeColor} stopOpacity="0.25" />
              <stop offset="100%" stopColor={strokeColor} stopOpacity="0.0" />
            </linearGradient>
          </defs>

          {/* Grid lines */}
          {yLabels.map(({ value, y }) => (
            <g key={value} className="opacity-40">
              <line
                x1={paddingLeft}
                y1={y}
                x2={chartWidth - paddingRight}
                y2={y}
                stroke="rgb(var(--dashboard-border))"
                strokeWidth="1"
                strokeDasharray="4 6"
              />
              <text
                x={paddingLeft - 8}
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

          {/* Filled Area */}
          <path d={areaPath} fill={`url(#${gradientId})`} />

          {/* Line Path */}
          <path
            d={linePath}
            fill="none"
            stroke={strokeColor}
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />

          {/* Node dots */}
          {points.map((p, i) => (
            <g key={i} className="group/node">
              <circle cx={p.x} cy={p.y} r="10" fill={strokeColor} fillOpacity="0.12"
                className="opacity-0 group-hover/node:opacity-100 transition-opacity duration-200" />
              <circle
                cx={p.x} cy={p.y} r="5.5"
                fill="rgb(var(--dashboard-panel))"
                stroke={strokeColor}
                strokeWidth="3"
                className="cursor-pointer"
              />
              <text
                x={p.x} y={p.y - 12}
                textAnchor="middle"
                fontSize="10"
                fontFamily="monospace"
                fill="rgb(var(--dashboard-text))"
                fontWeight="700"
                className="opacity-0 group-hover/node:opacity-100 transition-opacity duration-200"
              >
                {valueLabels[i]}
              </text>
            </g>
          ))}
        </svg>
      </div>

      {/* X Axis Labels */}
      <div className="flex justify-between pl-[40px] pr-[20px] mt-2">
        {labels.map((label, i) => (
          <span
            key={i}
            className="text-[10px] sm:text-xs font-mono font-semibold text-dashboard-muted text-center w-12 truncate"
          >
            {label}
          </span>
        ))}
      </div>
    </>
  );
}

// ------------------------------------------------------------------
// Rating chart data processor
// ------------------------------------------------------------------
function RatingChartContent() {
  const minRating = 1300;
  const maxRating = 2050;
  const chartHeight = 160;
  const chartWidth = 460;
  const paddingLeft = 40;
  const paddingRight = 20;
  const paddingTop = 20;
  const paddingBottom = 20;
  const width = chartWidth - paddingLeft - paddingRight;
  const height = chartHeight - paddingTop - paddingBottom;

  const points = contestData.map((d, i) => ({
    x: paddingLeft + (i / (contestData.length - 1)) * width,
    y: paddingTop + height - ((d.rating - minRating) / (maxRating - minRating)) * height,
  }));

  const gridValues = [1400, 1600, 1800, 2000];
  const yLabels = gridValues.map((val) => ({
    value: val,
    y: paddingTop + height - ((val - minRating) / (maxRating - minRating)) * height,
  }));

  return (
    <LineChart
      points={points}
      labels={contestData.map((d) => d.contestName)}
      yLabels={yLabels}
      valueLabels={contestData.map((d) => d.rating)}
      gradientId="rating-gradient"
      strokeColor="rgb(var(--dashboard-primary))"
    />
  );
}

// ------------------------------------------------------------------
// Daily solved chart data processor
// ------------------------------------------------------------------
function DailySolvedChartContent() {
  const maxSolved = Math.max(...dailySolvedData.map((d) => d.solved));
  const minSolved = 0;
  const chartHeight = 160;
  const chartWidth = 460;
  const paddingLeft = 40;
  const paddingRight = 20;
  const paddingTop = 20;
  const paddingBottom = 20;
  const width = chartWidth - paddingLeft - paddingRight;
  const height = chartHeight - paddingTop - paddingBottom;

  const points = dailySolvedData.map((d, i) => ({
    x: paddingLeft + (i / (dailySolvedData.length - 1)) * width,
    y:
      paddingTop +
      height -
      ((d.solved - minSolved) / (maxSolved - minSolved || 1)) * height,
  }));

  // Y-axis: 0, midpoint, max
  const gridValues = [0, Math.ceil(maxSolved / 2), maxSolved];
  const yLabels = gridValues.map((val) => ({
    value: val,
    y:
      paddingTop +
      height -
      ((val - minSolved) / (maxSolved - minSolved || 1)) * height,
  }));

  return (
    <LineChart
      points={points}
      labels={dailySolvedData.map((d) => d.day)}
      yLabels={yLabels}
      valueLabels={dailySolvedData.map((d) => d.solved)}
      gradientId="solved-gradient"
      strokeColor="#34d399"  // emerald-400
    />
  );
}

// ------------------------------------------------------------------
// Main component with toggle
// ------------------------------------------------------------------
export function RatingProgressChart() {
  const [mode, setMode] = useState<ChartMode>("rating");

  return (
    <div className="glass-panel p-6 flex flex-col justify-between hover:border-dashboard-primary/30 transition-all duration-300">
      {/* Header row */}
      <div className="flex items-center justify-between mb-4 gap-2 flex-wrap">
        <h3 className="text-base font-bold tracking-tight text-dashboard-text">
          {mode === "rating" ? "Rating Progress" : "Problems Solved"}
        </h3>

        {/* Toggle pill */}
        <div className="flex items-center rounded-full bg-dashboard-elevated border border-dashboard-border p-0.5 text-xs font-semibold">
          <button
            onClick={() => setMode("rating")}
            className={`px-3 py-1.5 rounded-full transition-all duration-200 ${
              mode === "rating"
                ? "bg-dashboard-primary text-dashboard-primary-contrast shadow"
                : "text-dashboard-muted hover:text-dashboard-text"
            }`}
          >
            Rating
          </button>
          <button
            onClick={() => setMode("solved")}
            className={`px-3 py-1.5 rounded-full transition-all duration-200 ${
              mode === "solved"
                ? "bg-emerald-500 text-white shadow"
                : "text-dashboard-muted hover:text-dashboard-text"
            }`}
          >
            Solved / Day
          </button>
        </div>

        <span className="text-xs font-semibold text-dashboard-muted">
          {mode === "rating" ? "Last 5 Contests" : "This Week"}
        </span>
      </div>

      {/* Chart area — smooth swap */}
      <div className="transition-all duration-300">
        {mode === "rating" ? <RatingChartContent /> : <DailySolvedChartContent />}
      </div>
    </div>
  );
}
