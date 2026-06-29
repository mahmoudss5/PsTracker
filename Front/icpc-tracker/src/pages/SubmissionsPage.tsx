import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, BarChart2, RefreshCw, AlertCircle } from 'lucide-react';
import { PeriodSelector } from '../components/Submissions/PeriodSelector';
import { SubmissionStatsBar } from '../components/Submissions/SubmissionStatsBar';
import { SubmissionVerdictsChart } from '../components/Submissions/SubmissionVerdictsChart';
import { SubmissionActivityChart } from '../components/Submissions/SubmissionActivityChart';
import { SubmissionTable } from '../components/Submissions/SubmissionTable';
import { MOCK_TRAINEES } from '../data/mockProfile';
import { useSubmissions } from '../hooks/useSubmissions';
import { useSubmissionsStats } from '../hooks/useSubmissionsStats';
import type { TimePeriod, CustomRange } from '../types/dashboard.types';

export function SubmissionsPage() {
  const { handle } = useParams<{ handle?: string }>();

  const [period, setPeriod] = useState<TimePeriod>('30d');
  const [customRange, setCustomRange] = useState<CustomRange | undefined>(undefined);

  const handlePeriodChange = (newPeriod: TimePeriod, range?: CustomRange) => {
    setPeriod(newPeriod);
    setCustomRange(range);
  };

  // Resolve trainee display info (name / initials) from handle
  const trainee = handle
    ? MOCK_TRAINEES.find((t) => t.handle === handle)
    : null;
  const numericUserId = handle && /^\d+$/.test(handle) ? Number(handle) : undefined;
  const isViewingUser = Boolean(handle);

  // Derive a numeric userId from the trainee handle.
  // TODO: once the backend returns userId on the profile or a handle→id lookup
  // endpoint is available, replace this index-based heuristic.
  const MOCK_USER_IDS: Record<string, number> = {
    tourist_wannabe: 1,
    ahmed_sayed: 2,
    jana_cp: 3,
    ahmed_mohsed: 4,
  };
  const targetUserId = numericUserId ?? (handle ? (MOCK_USER_IDS[handle] ?? undefined) : undefined);

  const { submissions, isLoading, error, refetch } = useSubmissions(
    targetUserId !== undefined ? { userId: targetUserId } : {},
  );

  const { filtered, stats } = useSubmissionsStats(submissions, period, customRange);

  return (
    <div className="space-y-5 sm:space-y-6">
      {/* ── Page Header ──────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start gap-3">
        <div className="flex-1 min-w-0">
          {isViewingUser && (
            <Link
              to="/dashboard/team"
              className="inline-flex items-center gap-1.5 text-xs font-semibold text-dashboard-muted hover:text-dashboard-primary transition-colors mb-2"
            >
              <ArrowLeft size={13} />
              Back to Team Hub
            </Link>
          )}

          <div className="flex items-center gap-3">
            {isViewingUser && (
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-dashboard-primary text-dashboard-primary-contrast font-bold text-sm">
                {trainee?.avatarInitials ?? "U"}
              </div>
            )}
            <div>
              <h1 className="text-2xl font-extrabold tracking-tight text-dashboard-text flex items-center gap-2">
                <BarChart2 size={22} className="text-dashboard-primary shrink-0" />
                {isViewingUser ? `${trainee?.name ?? `User #${targetUserId ?? handle}`}'s Submissions` : 'My Submissions'}
              </h1>
              <p className="text-sm text-dashboard-muted mt-0.5">
                {isViewingUser
                  ? `Viewing submission history for ${trainee ? `@${trainee.handle}` : `user ${targetUserId ?? handle}`}`
                  : 'Your complete submission history and performance analytics.'}
              </p>
            </div>
          </div>
        </div>

        {/* Period selector — top-right */}
        <div className="shrink-0">
          <PeriodSelector
            period={period}
            customRange={customRange}
            onChange={handlePeriodChange}
          />
        </div>
      </div>

      {/* ── Loading skeleton ─────────────────────────────────────────────────── */}
      {isLoading && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center gap-3 text-dashboard-muted animate-pulse">
          <RefreshCw size={24} className="animate-spin text-dashboard-primary" />
          <span className="text-sm font-medium">Loading submissions…</span>
        </div>
      )}

      {/* ── Error banner ─────────────────────────────────────────────────────── */}
      {!isLoading && error && (
        <div className="glass-panel p-5 flex items-center gap-4 border-red-500/30 bg-red-500/5">
          <AlertCircle size={20} className="text-red-400 shrink-0" />
          <div className="flex-1">
            <p className="text-sm font-semibold text-red-400">Failed to load submissions</p>
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

      {/* ── Data views (shown even while loading with stale data) ────────────── */}
      {!isLoading && !error && (
        <>
          {/* ── Stats Bar ────────────────────────────────────────────────────── */}
          <SubmissionStatsBar stats={stats} />

          {/* ── Charts row ───────────────────────────────────────────────────── */}
          <div className="grid grid-cols-1 lg:grid-cols-5 gap-4 sm:gap-5">
            <div className="lg:col-span-3">
              <SubmissionVerdictsChart stats={stats} />
            </div>
            <div className="lg:col-span-2">
              <SubmissionActivityChart stats={stats} />
            </div>
          </div>

          {/* ── Full submissions table ────────────────────────────────────────── */}
          <SubmissionTable submissions={filtered} />
        </>
      )}
    </div>
  );
}
