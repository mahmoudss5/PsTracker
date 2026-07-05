import { Download, FileText, ImageIcon, Link2, RefreshCw, AlertCircle } from "lucide-react";
import { useMaterials } from "../../hooks/useMaterials";
import type { MaterialResponseDto } from "../../services/materialService";

interface MaterialsPanelProps {
  teamId?: number;
}

export function MaterialsPanel({ teamId }: MaterialsPanelProps) {
  const { materials, isLoading, error, refetch } = useMaterials(teamId);

  const iconFor = (kind: MaterialResponseDto["kind"]) => {
    if (kind === "link") return <Link2 size={18} className="text-dashboard-primary" />;
    if (kind === "pdf") return <FileText size={18} className="text-red-400" />;
    return <ImageIcon size={18} className="text-emerald-400" />;
  };

  const bgFor = (kind: MaterialResponseDto["kind"]) => {
    if (kind === "link") return "bg-dashboard-primary/10 border-dashboard-primary/20";
    if (kind === "pdf") return "bg-red-500/10 border-red-500/20";
    return "bg-emerald-500/10 border-emerald-500/20";
  };

  return (
    <div className="h-full overflow-y-auto px-4 py-4 md:px-8">
      <p className="text-xs text-dashboard-muted mb-4">
        {materials.length} resources uploaded by your coach
      </p>

      {isLoading && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center gap-3 text-dashboard-muted animate-pulse">
          <RefreshCw size={24} className="animate-spin text-dashboard-primary" />
          <span className="text-sm font-medium">Loading materials…</span>
        </div>
      )}

      {!isLoading && error && (
        <div className="glass-panel p-5 flex items-center gap-4 border-red-500/30 bg-red-500/5">
          <AlertCircle size={20} className="text-red-400 shrink-0" />
          <div className="flex-1">
            <p className="text-sm font-semibold text-red-400">Failed to load materials</p>
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

      {!isLoading && !error && materials.length === 0 && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center text-center">
          <p className="text-dashboard-text font-semibold">No materials yet</p>
          <p className="text-xs text-dashboard-muted mt-1">Check back later for resources uploaded by your coach.</p>
        </div>
      )}

      {!isLoading && !error && (
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">
          {materials.map((mat) => (
            <div
              key={mat.id}
              className="glass-panel p-4 flex items-start gap-4 hover:border-dashboard-primary/30 transition-all duration-200 group cursor-pointer"
            >
              <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg border ${bgFor(mat.kind)}`}>
                {iconFor(mat.kind)}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-bold text-dashboard-text group-hover:text-dashboard-primary transition-colors truncate">
                  {mat.title}
                </p>
                <p className="text-xs text-dashboard-muted truncate mt-0.5">{mat.subtitle || (mat.size ? `${mat.size} • Uploaded by Coach` : 'Uploaded by Coach')}</p>
              </div>
              <button className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity text-dashboard-muted hover:text-dashboard-text">
                <Download size={16} />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
