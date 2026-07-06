import { Filter, RefreshCw, AlertCircle, Megaphone, Wifi } from "lucide-react";
import { useTeamAnnouncements } from "../../hooks/useTeamAnnouncements";

// Type → colour mappings aligned with the design system
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
const typeGlow: Record<string, string> = {
  URGENT: "ring-2 ring-red-500/30",
  UPDATE: "ring-2 ring-yellow-400/20",
  INFO: "ring-1 ring-dashboard-primary/20",
};

function formatDate(iso: string) {
  try {
    const d = new Date(iso);
    return `${d.toLocaleDateString()} · ${d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}`;
  } catch {
    return "";
  }
}

function initials(str: string) {
  return str.slice(0, 2).toUpperCase();
}

interface AnnouncementsPanelProps {
  teamId?: number;
}

export function AnnouncementsPanel({ teamId }: AnnouncementsPanelProps) {
  const { announcements, isLoading, error, refetch, newIds } =
    useTeamAnnouncements(teamId);

  return (
    <div className="h-full overflow-y-auto px-4 py-4 md:px-8 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <Megaphone size={15} className="text-dashboard-primary" />
          <p className="text-xs font-semibold text-dashboard-muted">
            {announcements.length} announcement{announcements.length !== 1 ? "s" : ""} from your coach
          </p>
          {/* Live indicator */}
          <span className="flex items-center gap-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 px-2 py-0.5 text-[10px] font-bold uppercase tracking-widest text-emerald-500">
            <Wifi size={9} /> Live
          </span>
        </div>
        <button
          onClick={refetch}
          className="flex items-center gap-1.5 rounded-lg border border-dashboard-border px-3 py-1.5 text-xs font-semibold text-dashboard-muted hover:border-dashboard-primary/40 hover:text-dashboard-text transition"
        >
          <Filter size={13} />
          Filter
        </button>
      </div>

      {/* Loading */}
      {isLoading && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center gap-3 text-dashboard-muted animate-pulse">
          <RefreshCw size={24} className="animate-spin text-dashboard-primary" />
          <span className="text-sm font-medium">Loading announcements…</span>
        </div>
      )}

      {/* Error */}
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

      {/* Empty */}
      {!isLoading && !error && announcements.length === 0 && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center text-center">
          <Megaphone size={28} className="text-dashboard-muted mb-3" />
          <p className="text-dashboard-text font-semibold">No announcements yet</p>
          <p className="text-xs text-dashboard-muted mt-1">
            Check back later for updates from your coach.
          </p>
        </div>
      )}

      {/* Announcement cards */}
      {!isLoading &&
        !error &&
        announcements.map((ann) => {
          const colorClass = typeColor[ann.type] ?? typeColor["INFO"];
          const borderClass = typeBorder[ann.type] ?? typeBorder["INFO"];
          const glowClass = newIds.has(ann.id) ? (typeGlow[ann.type] ?? typeGlow["INFO"]) : "";
          const animClass = newIds.has(ann.id) ? "animate-ann-flash-in" : "";

          return (
            <div
              key={ann.id}
              className={`glass-panel border-l-4 ${borderClass} ${glowClass} ${animClass} p-5 hover:border-r-dashboard-primary/20 transition-all duration-300 group cursor-pointer`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex-1 space-y-2">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span
                      className={`inline-flex items-center rounded-full border px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider ${colorClass}`}
                    >
                      {ann.type}
                    </span>
                    {newIds.has(ann.id) && (
                      <span className="rounded-full bg-dashboard-primary px-2 py-0.5 text-[9px] font-bold uppercase tracking-widest text-dashboard-primary-contrast animate-pulse">
                        NEW
                      </span>
                    )}
                    <span className="text-xs text-dashboard-muted">
                      {formatDate(ann.createdAt)}
                    </span>
                  </div>

                  <p className="text-sm leading-relaxed text-dashboard-text whitespace-pre-wrap">
                    {ann.content}
                  </p>

                  <div className="flex items-center gap-2 pt-1">
                    <div className="flex h-6 w-6 items-center justify-center rounded-full bg-amber-500/20 border border-amber-400/30 text-[10px] font-bold text-amber-300">
                      {initials("Coach")}
                    </div>
                    <span className="text-xs font-semibold text-dashboard-muted">
                      Coach · ID {ann.senderId}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
    </div>
  );
}
