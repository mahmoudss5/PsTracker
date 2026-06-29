import { useState } from 'react';
import { Search, Filter, MoreVertical, ArrowUp, ArrowDown } from 'lucide-react';
import type { TraineeOverview } from '../../data/mockCoach';

interface TraineesTableProps {
  trainees: TraineeOverview[];
  totalTrainees: number;
}

export function TraineesTable({ trainees, totalTrainees }: TraineesTableProps) {
  const [searchTerm, setSearchTerm] = useState('');
  
  const filteredTrainees = trainees.filter(t => 
    t.handle.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="glass-panel flex flex-col h-full overflow-hidden">
      {/* Table Header / Controls */}
      <div className="p-5 border-b border-dashboard-border/50 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <h3 className="text-base font-bold text-dashboard-text">Trainees Overview</h3>
        
        <div className="flex items-center gap-2">
          <div className="relative">
            <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-dashboard-muted" />
            <input 
              type="text" 
              placeholder="Search handle..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="bg-dashboard-background border border-dashboard-border/50 rounded-md py-1.5 pl-8 pr-3 text-sm text-dashboard-text focus:outline-none focus:border-dashboard-primary/50 focus:ring-1 focus:ring-dashboard-primary/50 transition-all w-full sm:w-48"
            />
          </div>
          <button className="p-1.5 border border-dashboard-border/50 rounded-md bg-dashboard-background text-dashboard-muted hover:text-dashboard-text hover:border-dashboard-primary/50 transition-colors">
            <Filter size={16} />
          </button>
        </div>
      </div>

      {/* Table Content */}
      <div className="flex-1 overflow-x-auto">
        <table className="w-full text-left text-sm whitespace-nowrap min-w-[500px]">
          <thead className="bg-dashboard-background/30 text-[10px] uppercase tracking-wider text-dashboard-muted border-b border-dashboard-border/50 font-bold">
            <tr>
              <th className="px-5 py-3 font-bold">Handle</th>
              <th className="px-5 py-3 font-bold">Rating</th>
              <th className="px-5 py-3 font-bold">Solved (7D)</th>
              <th className="px-5 py-3 font-bold">Status</th>
              <th className="px-5 py-3 text-right"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-dashboard-border/30">
            {filteredTrainees.map((trainee) => (
              <tr key={trainee.id} className="hover:bg-dashboard-panel/50 transition-colors group">
                <td className="px-5 py-4">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-sm bg-dashboard-primary/10 flex items-center justify-center text-dashboard-text font-bold text-xs border border-dashboard-primary/20">
                      {trainee.handle.substring(0, 2).toUpperCase()}
                    </div>
                    <div className="flex flex-col">
                      <span className="font-bold text-dashboard-text text-[13px]">{trainee.handle}</span>
                      <span className="text-[11px] text-dashboard-muted">{trainee.ratingRank}</span>
                    </div>
                  </div>
                </td>
                <td className="px-5 py-4">
                  <span className="font-bold text-orange-400">{trainee.rating}</span>
                </td>
                <td className="px-5 py-4">
                  <div className="flex items-center gap-2 font-bold text-dashboard-text">
                    {trainee.solved7d}
                    {trainee.solved7dTrend > 0 ? (
                      <span className="flex items-center text-[11px] text-emerald-400 bg-emerald-400/10 px-1 py-0.5 rounded">
                        <ArrowUp size={10} className="mr-0.5" />
                        {trainee.solved7dTrend}%
                      </span>
                    ) : (
                      <span className="flex items-center text-[11px] text-red-400 bg-red-400/10 px-1 py-0.5 rounded">
                        <ArrowDown size={10} className="mr-0.5" />
                        {Math.abs(trainee.solved7dTrend)}%
                      </span>
                    )}
                  </div>
                </td>
                <td className="px-5 py-4">
                  <span className={`text-[10px] font-bold px-2 py-1 rounded-sm border ${
                    trainee.status === 'ACTIVE' 
                      ? 'text-emerald-400 border-emerald-400/30 bg-emerald-400/5' 
                      : trainee.status === 'IN CONTEST'
                      ? 'text-orange-400 border-orange-400/30 bg-orange-400/5'
                      : 'text-dashboard-muted border-dashboard-border/50 bg-dashboard-background'
                  }`}>
                    {trainee.status}
                  </span>
                </td>
                <td className="px-5 py-4 text-right">
                  <button className="text-dashboard-muted hover:text-dashboard-text p-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <MoreVertical size={16} />
                  </button>
                </td>
              </tr>
            ))}
            
            {filteredTrainees.length === 0 && (
              <tr>
                <td colSpan={5} className="px-5 py-8 text-center text-dashboard-muted text-sm">
                  No trainees found matching "{searchTerm}"
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Footer */}
      <div className="p-4 border-t border-dashboard-border/50 flex items-center justify-between text-xs">
        <span className="text-dashboard-muted">
          Showing 1-{Math.min(filteredTrainees.length, 5)} of {totalTrainees} trainees
        </span>
        <div className="flex items-center gap-1">
          <button className="px-2.5 py-1 text-dashboard-muted hover:text-dashboard-text transition-colors">Previous</button>
          <button className="w-6 h-6 flex items-center justify-center bg-dashboard-primary text-dashboard-primary-contrast rounded font-bold">1</button>
          <button className="w-6 h-6 flex items-center justify-center bg-dashboard-background text-dashboard-muted hover:text-dashboard-text hover:bg-dashboard-panel border border-dashboard-border/50 rounded font-bold transition-colors">2</button>
          <button className="px-2.5 py-1 text-dashboard-muted hover:text-dashboard-text transition-colors">Next</button>
        </div>
      </div>
    </div>
  );
}
