import { ChevronRight, Filter, RefreshCw, AlertCircle } from "lucide-react";
import { useAnnouncements } from "../../hooks/useAnnouncements";

// Re-use the existing colors from mock for now to keep the UI looking the same
const typeColor: Record<string, string> = {
  URGENT: "text-red-400 bg-red-500/10 border-red-500/20",
  UPDATE: "text-yellow-400 bg-yellow-500/10 border-yellow-500/20",
  INFO: "text-dashboard-primary bg-dashboard-primary/10 border-dashboard-primary/20",
};
const typeBorder: Record<string, string> = {
  URGENT: "border-l-red-500",
  UPDATE: "border-l-yellow-400",
  INFO: "border-l-dashboard-primary",
};

interface AnnouncementsPanelProps {
  teamId?: number;
  userId?: number;
}

export function AnnouncementsPanel({ teamId, userId }: AnnouncementsPanelProps) {
  const { announcements, isLoading, error, refetch } = useAnnouncements(teamId, userId);

  return (
    <div className="h-full overflow-y-auto px-4 py-4 md:px-8 space-y-4">
      <div className="flex items-center justify-between mb-2">
        <p className="text-xs text-dashboard-muted">
          {announcements.length} announcements from your coach
        </p>
        <button className="flex items-center gap-1.5 rounded-lg border border-dashboard-border px-3 py-1.5 text-xs font-semibold text-dashboard-muted hover:border-dashboard-primary/40 hover:text-dashboard-text transition">
          <Filter size={13} />
          Filter
        </button>
      </div>

      {isLoading && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center gap-3 text-dashboard-muted animate-pulse">
          <RefreshCw size={24} className="animate-spin text-dashboard-primary" />
          <span className="text-sm font-medium">Loading announcements…</span>
        </div>
      )}

      {!isLoading && error && (
        <div className="glass-panel p-5 flex items-center gap-4 border-red-500/30 bg-red-500/5">
          <AlertCircle size={20} className="text-red-400 shrink-0" />
          <div className="flex-1">
            <p className="text-sm font-semibold text-red-400">Failed to load announcements</p>
            <p className="text-xs text-dashboard-muted mt-0.5">{error}</p>
          </div>
          <button
            onClick={refetch}
            className="flex items-center gap-1.5 text-xs font-semibold text-dashboard-primary hover:text-dashboard-primary/80 transition-colors"
          >
            <RefreshCw size={13} />
            Retry
          </button>
        </div>
      )}

      {!isLoading && !error && announcements.length === 0 && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center text-center">
          <p className="text-dashboard-text font-semibold">No announcements yet</p>
          <p className="text-xs text-dashboard-muted mt-1">Check back later for updates from your coach.</p>
        </div>
      )}

      {!isLoading && !error && announcements.map((ann) => {
        // Fallbacks in case the backend type is missing/unknown
        const colorClass = typeColor[ann.type] || typeColor["INFO"];
        const borderClass = typeBorder[ann.type] || typeBorder["INFO"];
        
        return (
          <div
            key={ann.id}
            className={`glass-panel border-l-4 ${borderClass} p-5 hover:border-r-dashboard-primary/20 transition-all duration-200 group cursor-pointer`}
          >
            <div className="flex items-start justify-between gap-3">
              <div className="flex-1 space-y-2">
                <div className="flex items-center gap-2 flex-wrap">
                  <span
                    className={`inline-flex items-center rounded-full border px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider ${colorClass}`}
                  >
                    {ann.type}
                  </span>
                  <span className="text-xs text-dashboard-muted">
                    {new Date(ann.createdAt).toLocaleDateString()}, {new Date(ann.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
                {/* Assuming title is derived or included in body since DTO has content only */}
                <p className="text-sm leading-relaxed text-dashboard-muted mt-2">{ann.content}</p>
                <div className="flex items-center gap-2 pt-1">
                  <div className="flex h-6 w-6 items-center justify-center rounded-full bg-dashboard-elevated border border-dashboard-border text-[10px] font-bold text-dashboard-muted">
                    CA
                  </div>
                  <span className="text-xs font-semibold text-dashboard-muted">Coach (ID: {ann.senderId})</span>
                </div>
              </div>
              <ChevronRight
                size={16}
                className="shrink-0 text-dashboard-muted opacity-0 group-hover:opacity-100 transition-opacity mt-1"
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}
