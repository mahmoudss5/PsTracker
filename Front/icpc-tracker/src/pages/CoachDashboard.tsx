import { useState } from 'react';
import { History, AlertCircle, RefreshCw } from 'lucide-react';
import { CoachOverview } from '../components/CoachDashboard/CoachOverview';
import { SubmissionTrends } from '../components/CoachDashboard/SubmissionTrends';
import { ActiveTeamsList } from '../components/CoachDashboard/ActiveTeamsList';
import { TraineesTable } from '../components/CoachDashboard/TraineesTable';
import { useCoachDashboard } from '../hooks/useCoachDashboard';

type Tab = 'analytics' | 'reports' | 'codeReview';

export function CoachDashboard() {
  const [activeTab, setActiveTab] = useState<Tab>('analytics');
  const { teams, allTrainees, totalTrainees, activeTeams, avgSolveRate, acceptanceRatio, isLoading, error, refetch } =
    useCoachDashboard();

  return (
    <div className="space-y-6">
      {/* Coach Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-dashboard-border pb-4 -mx-4 md:-mx-12 px-4 md:px-12 -mt-2">
        <div className="flex items-center gap-6 overflow-x-auto">
          <button
            onClick={() => setActiveTab('analytics')}
            className={`font-extrabold text-lg tracking-tight whitespace-nowrap transition-colors ${activeTab === 'analytics' ? 'text-dashboard-text' : 'text-dashboard-muted hover:text-dashboard-text'}`}
          >
            Coach Analytics
          </button>
          <button
            onClick={() => setActiveTab('reports')}
            className={`font-semibold text-sm tracking-tight whitespace-nowrap transition-colors ${activeTab === 'reports' ? 'text-dashboard-primary border-b-2 border-dashboard-primary pb-1' : 'text-dashboard-muted hover:text-dashboard-text pb-1'}`}
          >
            Weekly Reports
          </button>
          <button
            onClick={() => setActiveTab('codeReview')}
            className={`font-semibold text-sm tracking-tight whitespace-nowrap transition-colors ${activeTab === 'codeReview' ? 'text-dashboard-primary border-b-2 border-dashboard-primary pb-1' : 'text-dashboard-muted hover:text-dashboard-text pb-1'}`}
          >
            Code Review
          </button>
        </div>

        <div className="flex items-center gap-4 shrink-0">
          <div className="flex items-center gap-2 text-dashboard-muted">
            <button onClick={refetch} className="icon-button hover:text-dashboard-text" title="Refresh data">
              <RefreshCw size={18} className={isLoading ? 'animate-spin' : ''} />
            </button>
            <button className="icon-button hover:text-dashboard-text">
              <History size={18} />
            </button>
          </div>
          <button className="flex items-center gap-1.5 px-3 py-1.5 bg-dashboard-panel border border-dashboard-border/50 text-dashboard-text font-bold text-xs rounded hover:bg-dashboard-primary/10 transition-colors">
            New Session
          </button>
          <div className="w-8 h-8 rounded bg-dashboard-primary/20 flex items-center justify-center text-dashboard-primary font-bold text-sm ml-2 overflow-hidden border border-dashboard-primary/30">
            <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Coach&backgroundColor=transparent" alt="Coach Avatar" className="w-full h-full object-cover" />
          </div>
        </div>
      </div>

      {/* Error banner */}
      {error && (
        <div className="glass-panel p-5 flex items-center gap-4 border-red-500/30 bg-red-500/5">
          <AlertCircle size={20} className="text-red-400 shrink-0" />
          <div className="flex-1">
            <p className="text-sm font-semibold text-red-400">Failed to load dashboard data</p>
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

      {/* Loading skeleton */}
      {isLoading && !error && (
        <div className="glass-panel p-8 flex flex-col items-center justify-center gap-3 text-dashboard-muted animate-pulse">
          <RefreshCw size={24} className="animate-spin text-dashboard-primary" />
          <span className="text-sm font-medium">Loading dashboard…</span>
        </div>
      )}

      {/* Analytics tab */}
      {activeTab === 'analytics' && !isLoading && !error && (
        <>
          <CoachOverview
            totalTrainees={totalTrainees}
            activeTeams={activeTeams}
            avgSolveRate={avgSolveRate}
          />

          <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
            {/* Left Column */}
            <div className="xl:col-span-1 space-y-6 flex flex-col">
              <SubmissionTrends
                acceptanceRatio={acceptanceRatio}
                dailyActivityTrend={null}
                dailyActivityData={[]}
              />
              <div className="flex-1">
                <ActiveTeamsList teams={teams} />
              </div>
            </div>

            {/* Right Column */}
            <div className="xl:col-span-2">
              <TraineesTable
                trainees={allTrainees}
                totalTrainees={totalTrainees}
              />
            </div>
          </div>
        </>
      )}

      {/* Other tabs */}
      {activeTab !== 'analytics' && (
        <div className="flex items-center justify-center h-64 text-dashboard-muted glass-panel">
          <p className="font-semibold text-lg">
            {activeTab === 'reports' ? 'Weekly Reports' : 'Code Review'} — Coming Soon
          </p>
        </div>
      )}
    </div>
  );
}
