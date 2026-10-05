import { AlertCircle, RefreshCw } from 'lucide-react';

export function MaterialLoadingState() {
  return (
    <div className="glass-panel flex animate-pulse flex-col items-center justify-center gap-3 p-8 text-dashboard-muted">
      <RefreshCw size={24} className="animate-spin text-dashboard-primary" />
      <span className="text-sm font-medium">Loading materials…</span>
    </div>
  );
}

export function MaterialErrorState({ message, onRetry }: { message: string; onRetry: () => void }) {
  return (
    <div className="glass-panel flex items-center gap-4 border-red-500/30 bg-red-500/5 p-5">
      <AlertCircle size={20} className="shrink-0 text-red-400" />
      <div className="flex-1">
        <p className="text-sm font-semibold text-red-400">Failed to load materials</p>
        <p className="mt-0.5 text-xs text-dashboard-muted">{message}</p>
      </div>
      <button
        type="button"
        onClick={onRetry}
        className="flex items-center gap-1.5 text-xs font-semibold text-dashboard-primary transition-colors hover:text-dashboard-primary/80"
      >
        <RefreshCw size={13} />
        Retry
      </button>
    </div>
  );
}

export function MaterialEmptyState() {
  return (
    <div className="glass-panel flex flex-col items-center justify-center p-8 text-center">
      <p className="font-semibold text-dashboard-text">No materials yet</p>
      <p className="mt-1 text-xs text-dashboard-muted">Check back later for resources uploaded by your coach.</p>
    </div>
  );
}
