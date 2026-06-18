interface ConsistencyHeatmapProps {
  data: number[]; // Array of heat levels (0-4)
}

export function ConsistencyHeatmap({ data }: ConsistencyHeatmapProps) {
  // Ensure we have exactly 365 days, pad or slice as needed
  const displayData = data.slice(0, 365);
  
  return (
    <div className="glass-panel p-6 overflow-hidden">
      <div className="flex flex-col sm:flex-row sm:items-end justify-between mb-6 gap-4">
        <div>
          <h2 className="dashboard-heading mb-1 text-dashboard-text">Consistency Heatmap</h2>
          <p className="text-sm font-medium text-dashboard-muted">
            365 days of problem-solving activity.
          </p>
        </div>
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
      
      <div className="w-full overflow-x-auto pb-2 scrollbar-thin">
        <div className="inline-flex flex-col gap-1 items-start min-w-max">
          {/* We'll arrange 365 days into 7 rows (weeks * 7) */}
          {Array.from({ length: 7 }).map((_, rowIndex) => (
            <div key={rowIndex} className="flex gap-1">
              {Array.from({ length: Math.ceil(365 / 7) }).map((_, colIndex) => {
                const dayIndex = colIndex * 7 + rowIndex;
                if (dayIndex >= 365) return <span key={colIndex} className="w-3 h-3 transparent" />;
                const level = displayData[dayIndex] || 0;
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
    </div>
  );
}
