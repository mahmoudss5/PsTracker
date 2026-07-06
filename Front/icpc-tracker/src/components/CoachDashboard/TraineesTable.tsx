import { useState } from 'react';
import { ArrowUpDown, ChevronDown, ChevronUp, TrendingUp, Users } from 'lucide-react';
import { Link } from 'react-router-dom';
import type { TraineeResponse } from '../../types/api.types';

interface TraineesTableProps {
  trainees: TraineeResponse[];
  totalTrainees: number;
}

type SortKey = 'userName' | 'rate' | 'numberOfSolveProblems' | 'totalSumbissions';
type SortDir = 'asc' | 'desc';

const RANK_COLOR: Record<string, string> = {
  'Legendary Grandmaster': 'text-red-400',
  'International Grandmaster': 'text-red-400',
  'Grandmaster': 'text-red-400',
  'International Master': 'text-orange-400',
  'Master': 'text-orange-400',
  'Candidate Master': 'text-purple-400',
  'Expert': 'text-blue-400',
  'Specialist': 'text-cyan-400',
  'Pupil': 'text-emerald-400',
  'Newbie': 'text-dashboard-muted',
};

function rankColor(rank: string): string {
  return RANK_COLOR[rank] ?? 'text-dashboard-muted';
}

function initials(name: string): string {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join('') || '?';
}

function SortIcon({ col, active, dir }: { col: string; active: boolean; dir: SortDir }) {
  if (!active) return <ArrowUpDown size={12} className="text-dashboard-muted opacity-50" />;
  return dir === 'asc'
    ? <ChevronUp size={12} className="text-dashboard-primary" />
    : <ChevronDown size={12} className="text-dashboard-primary" />;
}

export function TraineesTable({ trainees, totalTrainees }: TraineesTableProps) {
  const [sortKey, setSortKey] = useState<SortKey>('rate');
  const [sortDir, setSortDir] = useState<SortDir>('desc');
  const [search, setSearch] = useState('');

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDir('desc');
    }
  };

  const filtered = trainees
    .filter((t) => {
      const q = search.toLowerCase();
      return (
        t.userName.toLowerCase().includes(q) ||
        (t.codeforcesHandle ?? '').toLowerCase().includes(q) ||
        (t.rank ?? '').toLowerCase().includes(q)
      );
    })
    .sort((a, b) => {
      const mul = sortDir === 'asc' ? 1 : -1;
      if (sortKey === 'userName') return mul * a.userName.localeCompare(b.userName);
      return mul * ((a[sortKey] as number) - (b[sortKey] as number));
    });

  return (
    <div className="glass-panel p-5">
      {/* Header */}
      <div className="flex items-center justify-between mb-4 gap-3 flex-wrap">
        <div className="flex items-center gap-2">
          <Users size={16} className="text-dashboard-primary" />
          <h3 className="text-sm font-bold text-dashboard-text">Trainees</h3>
          <span className="text-xs text-dashboard-muted">
            {filtered.length} / {totalTrainees}
          </span>
        </div>

        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by name, handle, rank…"
          className="flex-1 min-w-40 max-w-xs bg-dashboard-elevated border border-dashboard-border rounded-lg px-3 py-1.5 text-xs text-dashboard-text placeholder:text-dashboard-muted focus:outline-none focus:border-dashboard-primary transition-colors"
        />
      </div>

      {/* Empty */}
      {trainees.length === 0 && (
        <div className="flex flex-col items-center justify-center py-12 text-dashboard-muted">
          <TrendingUp size={24} className="mb-2 opacity-40" />
          <p className="text-sm font-semibold">No trainees yet</p>
          <p className="text-xs mt-1">Share a team code with your trainees to get started.</p>
        </div>
      )}

      {/* Table */}
      {trainees.length > 0 && (
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-dashboard-border text-[10px] uppercase tracking-wider text-dashboard-muted">
                <th className="pb-2 text-left font-semibold">
                  <button
                    onClick={() => toggleSort('userName')}
                    className="flex items-center gap-1 hover:text-dashboard-text transition-colors"
                  >
                    Trainee <SortIcon col="userName" active={sortKey === 'userName'} dir={sortDir} />
                  </button>
                </th>
                <th className="pb-2 text-right font-semibold">
                  <button
                    onClick={() => toggleSort('rate')}
                    className="flex items-center gap-1 ml-auto hover:text-dashboard-text transition-colors"
                  >
                    Rating <SortIcon col="rate" active={sortKey === 'rate'} dir={sortDir} />
                  </button>
                </th>
                <th className="pb-2 text-right font-semibold">
                  <button
                    onClick={() => toggleSort('numberOfSolveProblems')}
                    className="flex items-center gap-1 ml-auto hover:text-dashboard-text transition-colors"
                  >
                    Solved <SortIcon col="numberOfSolveProblems" active={sortKey === 'numberOfSolveProblems'} dir={sortDir} />
                  </button>
                </th>
                <th className="pb-2 text-right font-semibold">
                  <button
                    onClick={() => toggleSort('totalSumbissions')}
                    className="flex items-center gap-1 ml-auto hover:text-dashboard-text transition-colors"
                  >
                    Submissions <SortIcon col="totalSumbissions" active={sortKey === 'totalSumbissions'} dir={sortDir} />
                  </button>
                </th>
                <th className="pb-2 text-right font-semibold">Submissions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dashboard-border/40">
              {filtered.map((t) => (
                <tr key={t.id} className="hover:bg-dashboard-primary/5 transition-colors group">
                  {/* Name + handle */}
                  <td className="py-3 pr-4">
                    <div className="flex items-center gap-3">
                      <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-dashboard-elevated border border-dashboard-border text-xs font-bold text-dashboard-muted group-hover:border-dashboard-primary/40 transition-colors">
                        {initials(t.userName)}
                      </div>
                      <div>
                        <p className="font-semibold text-dashboard-text leading-tight">{t.userName}</p>
                        {t.codeforcesHandle && (
                          <p className="text-[10px] text-dashboard-muted">@{t.codeforcesHandle}</p>
                        )}
                      </div>
                    </div>
                  </td>

                  {/* Rating */}
                  <td className="py-3 text-right">
                    <div>
                      <p className="font-bold text-dashboard-text tabular-nums">{t.rate.toLocaleString()}</p>
                      {t.rank && (
                        <p className={`text-[10px] font-semibold ${rankColor(t.rank)}`}>{t.rank}</p>
                      )}
                    </div>
                  </td>

                  {/* Solved problems (all-time) */}
                  <td className="py-3 text-right">
                    <span className="font-bold text-emerald-400 tabular-nums">
                      {t.numberOfSolveProblems.toLocaleString()}
                    </span>
                  </td>

                  {/* Total submissions */}
                  <td className="py-3 text-right">
                    <span className="tabular-nums text-dashboard-muted">
                      {t.totalSumbissions.toLocaleString()}
                    </span>
                  </td>

                  {/* Link to submissions page */}
                  <td className="py-3 text-right">
                    <Link
                      to={`/dashboard/submissions/${t.id}`}
                      className="text-xs font-semibold text-dashboard-primary hover:text-dashboard-primary/80 opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      View →
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
