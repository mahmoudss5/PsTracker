interface ConsistencyHeatmapProps {
  data: number[]; // Array of heat levels (0-4)
  totalSolved?: number;
  submissionsToday?: number;
}

export function ConsistencyHeatmap({ data, totalSolved, submissionsToday }: ConsistencyHeatmapProps) {
  // Pad data to a multiple of 7 so every column is full
  const ROWS = 7;
  const totalCells = Math.ceil(data.length / ROWS) * ROWS;
  const padded = [...data];
  while (padded.length < totalCells) padded.push(-1); // -1 = empty padding

  const numCols = totalCells / ROWS;

  return (
    <div className="glass-panel p-6 overflow-hidden">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between mb-6 gap-4">
        <div>
          <h2 className="dashboard-heading mb-1 text-dashboard-text">Consistency Heatmap</h2>
          <p className="text-sm font-medium text-dashboard-muted">
            365 days of problem-solving activity.
          </p>
        </div>

        {/* Legend */}
        <div className="flex items-center gap-2 text-xs font-semibold text-dashboard-muted">
          <span>LESS</span>
          <div className="flex gap-1">
            {[0, 1, 2, 3, 4].map((level) => (
              <span key={level} className={`heat-key heat-${level}`} />
            ))}
          </div>
          <span>MORE</span>
        </div>
      </div>

      {/* Heatmap grid */}
      <div className="w-full overflow-x-auto pb-2">
        <div className="inline-flex flex-col gap-1 items-start min-w-max">
          {Array.from({ length: ROWS }).map((_, rowIndex) => (
            <div key={rowIndex} className="flex gap-1">
              {Array.from({ length: numCols }).map((_, colIndex) => {
                const dayIndex = colIndex * ROWS + rowIndex;
                const level = padded[dayIndex];

                if (level === -1) {
                  return <span key={colIndex} className="w-3 h-3 opacity-0" />;
                }

                return (
                  <span
                    key={colIndex}
                    className={`heat-cell heat-${level}`}
                    title={`Day ${dayIndex + 1} - Level ${level}`}
                  />
                );
              })}
            </div>
          ))}
        </div>
      </div>

      {/* Stats row — below heatmap, centered */}
      <div className="flex flex-wrap justify-center gap-4 mt-6">
        {totalSolved !== undefined && (
          <div className="flex flex-1 flex-col items-center gap-1 bg-white/5 border border-white/10 rounded-2xl px-4 sm:px-8 py-4 min-w-[120px]">
            <span className="text-2xl sm:text-3xl">✅</span>
            <p className="text-2xl sm:text-3xl font-extrabold text-dashboard-text leading-none">
              {totalSolved.toLocaleString()}
            </p>
            <p className="text-xs sm:text-sm font-semibold text-dashboard-muted uppercase tracking-widest mt-1 text-center">
              Problems Solved
            </p>
          </div>
        )}

        {submissionsToday !== undefined && (
          <div className="flex flex-1 flex-col items-center gap-1 bg-white/5 border border-white/10 rounded-2xl px-4 sm:px-8 py-4 min-w-[120px]">
            <span className="text-2xl sm:text-3xl">📤</span>
            <p className="text-2xl sm:text-3xl font-extrabold text-dashboard-text leading-none">
              {submissionsToday}
            </p>
            <p className="text-xs sm:text-sm font-semibold text-dashboard-muted uppercase tracking-widest mt-1 text-center">
              Submissions Today
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
