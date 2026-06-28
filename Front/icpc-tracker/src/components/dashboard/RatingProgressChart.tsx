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

export function RatingProgressChart() {
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

  // Convert rating point to coordinates
  const getCoordinates = (index: number, rating: number) => {
    const x = paddingLeft + (index / (contestData.length - 1)) * width;
    // Map rating to y: lower rating -> higher Y
    const y = paddingTop + height - ((rating - minRating) / (maxRating - minRating)) * height;
    return { x, y };
  };

  const points = contestData.map((d, i) => getCoordinates(i, d.rating));

  // Generate path string for the line
  const linePath = points.map((p, i) => `${i === 0 ? "M" : "L"} ${p.x} ${p.y}`).join(" ");

  // Generate path string for the filled gradient area under the line
  const areaPath = `
    ${linePath}
    L ${points[points.length - 1].x} ${paddingTop + height}
    L ${points[0].x} ${paddingTop + height}
    Z
  `;

  // Standard grid lines
  const gridLinesY = [1400, 1600, 1800, 2000];

  return (
    <div className="glass-panel p-6 flex flex-col justify-between hover:border-dashboard-primary/30 transition-all duration-300">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-base font-bold tracking-tight text-dashboard-text">
          Rating Progress
        </h3>
        <span className="text-xs font-semibold text-dashboard-muted">
          Last 5 Contests
        </span>
      </div>

      {/* SVG Chart Container */}
      <div className="relative w-full overflow-hidden mt-2">
        <svg
          viewBox={`0 0 ${chartWidth} ${chartHeight}`}
          className="w-full h-auto overflow-visible"
        >
          <defs>
            <linearGradient id="rating-gradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="rgb(var(--dashboard-primary))" stopOpacity="0.25" />
              <stop offset="100%" stopColor="rgb(var(--dashboard-primary))" stopOpacity="0.0" />
            </linearGradient>
          </defs>

          {/* Grid lines */}
          {gridLinesY.map((val) => {
            const y = paddingTop + height - ((val - minRating) / (maxRating - minRating)) * height;
            return (
              <g key={val} className="opacity-40">
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
                  x={paddingLeft - 10}
                  y={y + 4}
                  textAnchor="end"
                  className="text-[9px] font-mono fill-dashboard-muted font-semibold"
                >
                  {val}
                </text>
              </g>
            );
          })}

          {/* Filled Area */}
          <path d={areaPath} fill="url(#rating-gradient)" />

          {/* Line Path */}
          <path
            d={linePath}
            fill="none"
            stroke="rgb(var(--dashboard-primary))"
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />

          {/* Glowing node dots */}
          {points.map((p, i) => (
            <g key={i} className="group/node">
              {/* Outer soft glow ring (displays on hover) */}
              <circle
                cx={p.x}
                cy={p.y}
                r="10"
                className="fill-dashboard-primary/20 opacity-0 group-hover/node:opacity-100 transition-opacity duration-200"
              />
              {/* Main Node Point */}
              <circle
                cx={p.x}
                cy={p.y}
                r="6"
                className="fill-dashboard-panel stroke-dashboard-primary cursor-pointer transition-all duration-200"
                strokeWidth="3.5"
              />
              {/* Tooltip or rating badge above node */}
              <text
                x={p.x}
                y={p.y - 12}
                textAnchor="middle"
                className="text-[10px] font-mono fill-dashboard-text font-bold opacity-0 group-hover/node:opacity-100 transition-opacity duration-200 bg-dashboard-panel"
              >
                {contestData[i].rating}
              </text>
            </g>
          ))}
        </svg>
      </div>

      {/* X Axis Labels */}
      <div className="flex justify-between pl-[40px] pr-[20px] mt-2">
        {contestData.map((d, i) => (
          <span
            key={i}
            className="text-[10px] sm:text-xs font-mono font-semibold text-dashboard-muted text-center w-12 truncate"
          >
            {d.contestName}
          </span>
        ))}
      </div>
    </div>
  );
}
