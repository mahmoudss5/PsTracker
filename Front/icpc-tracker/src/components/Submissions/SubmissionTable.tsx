import { useState, useMemo } from 'react';
import { ChevronUp, ChevronDown, ChevronsUpDown, ChevronLeft, ChevronRight } from 'lucide-react';
import type { SubmissionEntry, Verdict } from '../../types/dashboard.types';

interface SubmissionTableProps {
  submissions: SubmissionEntry[];
}

type SortKey = keyof Pick<SubmissionEntry, 'problemName' | 'verdict' | 'language' | 'runtimeMs' | 'memoryKB' | 'date'>;
type SortDir = 'asc' | 'desc';

const VERDICT_STYLE: Record<Verdict, string> = {
  AC:  'bg-emerald-500/10 text-emerald-500 border-emerald-500/20',
  WA:  'bg-red-500/10 text-red-500 border-red-500/20',
  TLE: 'bg-orange-500/10 text-orange-500 border-orange-500/20',
  RE:  'bg-purple-500/10 text-purple-500 border-purple-500/20',
  MLE: 'bg-cyan-500/10 text-cyan-500 border-cyan-500/20',
};

const VERDICT_LABEL: Record<Verdict, string> = {
  AC:  'Accepted',
  WA:  'Wrong Answer',
  TLE: 'TLE',
  RE:  'Runtime Error',
  MLE: 'Memory Limit',
};

const ALL_VERDICTS: Verdict[] = ['AC', 'WA', 'TLE', 'RE', 'MLE'];
const PAGE_SIZE = 10;

const HEADERS: { key: SortKey; label: string; align: string }[] = [
  { key: 'problemName', label: 'Problem', align: 'text-left' },
  { key: 'verdict',     label: 'Verdict', align: 'text-left' },
  { key: 'language',    label: 'Language', align: 'text-left' },
  { key: 'runtimeMs',  label: 'Runtime', align: 'text-right' },
  { key: 'memoryKB',   label: 'Memory', align: 'text-right' },
  { key: 'date',       label: 'Date', align: 'text-right' },
];

export function SubmissionTable({ submissions }: SubmissionTableProps) {
  const [sortKey, setSortKey] = useState<SortKey>('date');
  const [sortDir, setSortDir] = useState<SortDir>('desc');
  const [verdictFilter, setVerdictFilter] = useState<Set<Verdict>>(new Set());
  const [page, setPage] = useState(1);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDir('desc');
    }
    setPage(1);
  };

  const toggleVerdict = (v: Verdict) => {
    setVerdictFilter((prev) => {
      const next = new Set(prev);
      next.has(v) ? next.delete(v) : next.add(v);
      return next;
    });
    setPage(1);
  };

  const processed = useMemo(() => {
    let rows = [...submissions];

    // Filter by verdict
    if (verdictFilter.size > 0) {
      rows = rows.filter((r) => verdictFilter.has(r.verdict));
    }

    // Sort
    rows.sort((a, b) => {
      let cmp = 0;
      if (sortKey === 'problemName' || sortKey === 'language' || sortKey === 'verdict') {
        cmp = a[sortKey].localeCompare(b[sortKey]);
      } else if (sortKey === 'date') {
        cmp = new Date(a.date).getTime() - new Date(b.date).getTime();
      } else {
        cmp = (a[sortKey] as number) - (b[sortKey] as number);
      }
      return sortDir === 'asc' ? cmp : -cmp;
    });

    return rows;
  }, [submissions, sortKey, sortDir, verdictFilter]);

  const totalPages = Math.max(1, Math.ceil(processed.length / PAGE_SIZE));
  const pageRows = processed.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  const formatDate = (iso: string) =>
    new Date(iso).toLocaleString('en-US', {
      month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });

  const SortIcon = ({ col }: { col: SortKey }) => {
    if (sortKey !== col) return <ChevronsUpDown size={12} className="text-dashboard-muted/50" />;
    return sortDir === 'asc'
      ? <ChevronUp size={12} className="text-dashboard-primary" />
      : <ChevronDown size={12} className="text-dashboard-primary" />;
  };

  return (
    <div className="glass-panel flex flex-col hover:border-dashboard-primary/30 transition-all duration-300">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-4 sm:px-6 py-4 border-b border-dashboard-border">
        <div className="flex-1">
          <h2 className="dashboard-heading text-dashboard-text">All Submissions</h2>
          <p className="text-xs text-dashboard-muted mt-0.5">
            {processed.length.toLocaleString()} result{processed.length !== 1 ? 's' : ''}
          </p>
        </div>

        {/* Verdict filter chips */}
        <div className="flex flex-wrap gap-1.5">
          {ALL_VERDICTS.map((v) => {
            const active = verdictFilter.has(v);
            return (
              <button
                key={v}
                id={`filter-verdict-${v}`}
                onClick={() => toggleVerdict(v)}
                className={`px-2.5 py-1 rounded-full text-xs font-bold border transition-all duration-200 ${
                  active
                    ? `${VERDICT_STYLE[v]} border-current`
                    : 'border-dashboard-border text-dashboard-muted hover:text-dashboard-text bg-dashboard-elevated'
                }`}
              >
                {v}
              </button>
            );
          })}
          {verdictFilter.size > 0 && (
            <button
              onClick={() => { setVerdictFilter(new Set()); setPage(1); }}
              className="px-2.5 py-1 rounded-full text-xs font-bold border border-dashboard-border text-dashboard-muted hover:text-dashboard-text bg-dashboard-elevated transition-all"
            >
              Clear
            </button>
          )}
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-dashboard-border text-dashboard-muted">
              {HEADERS.map((h) => (
                <th
                  key={h.key}
                  className={`px-3 sm:px-5 py-3 font-semibold uppercase tracking-wider text-[10px] cursor-pointer select-none ${h.align} hover:text-dashboard-text transition-colors`}
                  onClick={() => toggleSort(h.key)}
                >
                  <span className="inline-flex items-center gap-1">
                    {h.label}
                    <SortIcon col={h.key} />
                  </span>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-dashboard-border/50 text-dashboard-text">
            {pageRows.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-5 py-10 text-center text-sm text-dashboard-muted italic">
                  No submissions match the current filters.
                </td>
              </tr>
            ) : (
              pageRows.map((sub) => (
                <tr
                  key={sub.id}
                  className="hover:bg-dashboard-elevated transition-colors group"
                >
                  {/* Problem */}
                  <td className="px-3 sm:px-5 py-3 font-medium font-mono max-w-[160px] sm:max-w-xs truncate">
                    <span title={sub.problemName}>{sub.problemName}</span>
                  </td>

                  {/* Verdict */}
                  <td className="px-3 sm:px-5 py-3">
                    <span
                      className={`inline-flex items-center rounded px-2 py-0.5 text-xs font-bold tracking-wide border ${VERDICT_STYLE[sub.verdict]}`}
                    >
                      <span className="hidden sm:inline">{VERDICT_LABEL[sub.verdict]}</span>
                      <span className="sm:hidden">{sub.verdict}</span>
                    </span>
                  </td>

                  {/* Language */}
                  <td className="px-3 sm:px-5 py-3">
                    <span className="inline-flex rounded-full px-2 py-0.5 text-xs font-semibold bg-dashboard-elevated border border-dashboard-border text-dashboard-text">
                      {sub.language}
                    </span>
                  </td>

                  {/* Runtime */}
                  <td className="px-3 sm:px-5 py-3 font-mono text-right text-dashboard-muted">
                    <span className={sub.verdict === 'AC' ? 'text-emerald-500 font-bold' : ''}>
                      {sub.runtimeMs} ms
                    </span>
                  </td>

                  {/* Memory */}
                  <td className="px-3 sm:px-5 py-3 font-mono text-right text-dashboard-muted">
                    {sub.memoryKB >= 1024
                      ? `${(sub.memoryKB / 1024).toFixed(1)} MB`
                      : `${sub.memoryKB} KB`}
                  </td>

                  {/* Date */}
                  <td className="px-3 sm:px-5 py-3 text-right text-dashboard-muted text-xs whitespace-nowrap">
                    {formatDate(sub.date)}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between px-4 sm:px-6 py-3 border-t border-dashboard-border">
          <span className="text-xs text-dashboard-muted">
            Page {page} of {totalPages} &mdash; {processed.length} total
          </span>
          <div className="flex items-center gap-1">
            <button
              id="submissions-prev-page"
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={page === 1}
              className="icon-button h-8 w-8 disabled:opacity-30 disabled:cursor-not-allowed"
            >
              <ChevronLeft size={16} />
            </button>

            {/* Page number pills */}
            {Array.from({ length: totalPages }, (_, i) => i + 1)
              .filter((p) => p === 1 || p === totalPages || Math.abs(p - page) <= 1)
              .reduce<(number | '…')[]>((acc, p, idx, arr) => {
                if (idx > 0 && typeof arr[idx - 1] === 'number' && (p as number) - (arr[idx - 1] as number) > 1) {
                  acc.push('…');
                }
                acc.push(p);
                return acc;
              }, [])
              .map((item, i) =>
                item === '…' ? (
                  <span key={`ellipsis-${i}`} className="px-1 text-dashboard-muted text-xs">…</span>
                ) : (
                  <button
                    key={item}
                    onClick={() => setPage(item as number)}
                    className={`h-8 w-8 rounded text-xs font-semibold transition-all ${
                      page === item
                        ? 'bg-dashboard-primary text-dashboard-primary-contrast'
                        : 'text-dashboard-muted hover:text-dashboard-text hover:bg-dashboard-elevated'
                    }`}
                  >
                    {item}
                  </button>
                )
              )}

            <button
              id="submissions-next-page"
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={page === totalPages}
              className="icon-button h-8 w-8 disabled:opacity-30 disabled:cursor-not-allowed"
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
