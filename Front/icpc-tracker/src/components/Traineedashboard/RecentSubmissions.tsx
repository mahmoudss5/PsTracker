import type { SubmissionEntry } from '../../types/dashboard.types';

interface RecentSubmissionsProps {
  submissions: SubmissionEntry[];
}

export function RecentSubmissions({ submissions }: RecentSubmissionsProps) {
  return (
    <div className="glass-panel flex flex-col">
      <div className="flex items-center justify-between border-b border-dashboard-border px-4 sm:px-6 py-4">
        <h2 className="dashboard-heading text-dashboard-text">Recent Submissions</h2>
        <button className="text-xs font-bold tracking-wider text-dashboard-primary hover:text-dashboard-primary-contrast transition-colors uppercase">
          View All
        </button>
      </div>
      
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-dashboard-border text-dashboard-muted">
              <th className="px-3 sm:px-6 py-3 sm:py-4 font-semibold uppercase tracking-wider">Problem</th>
              <th className="px-3 sm:px-6 py-3 sm:py-4 font-semibold uppercase tracking-wider">Verdict</th>
              <th className="hidden sm:table-cell px-6 py-4 font-semibold uppercase tracking-wider text-center">Time (ms)</th>
              <th className="hidden sm:table-cell px-6 py-4 font-semibold uppercase tracking-wider text-right">Memory (KB)</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-dashboard-border/50 text-dashboard-text font-mono">
            {submissions.map((sub) => (
              <tr key={sub.id} className="hover:bg-dashboard-elevated transition-colors">
                <td className="px-3 sm:px-6 py-3 sm:py-4 font-medium max-w-[140px] sm:max-w-none truncate">{sub.problemName}</td>
                <td className="px-3 sm:px-6 py-3 sm:py-4">
                  <span
                    className={`inline-flex rounded px-2 py-1 text-xs font-semibold tracking-wide whitespace-nowrap ${
                      sub.verdict === 'AC'
                        ? 'bg-emerald-500/10 text-emerald-500 border border-emerald-500/20'
                        : sub.verdict === 'TLE'
                        ? 'bg-orange-500/10 text-orange-500 border border-orange-500/20'
                        : sub.verdict === 'WA'
                        ? 'bg-red-500/10 text-red-500 border border-red-500/20'
                        : 'bg-gray-500/10 text-gray-500 border border-gray-500/20'
                    }`}
                  >
                    {sub.verdict}
                  </span>
                </td>
                <td className="hidden sm:table-cell px-6 py-4 text-center text-dashboard-muted">{sub.runtimeMs}</td>
                <td className="hidden sm:table-cell px-6 py-4 text-right text-dashboard-muted">{sub.memoryKB}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
