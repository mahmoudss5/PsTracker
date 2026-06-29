import { useState } from 'react';
import { Bell, History } from 'lucide-react';
import { mockCoachData } from '../data/mockCoach';
import { CoachOverview } from '../components/CoachDashboard/CoachOverview';
import { SubmissionTrends } from '../components/CoachDashboard/SubmissionTrends';
import { ActiveTeamsList } from '../components/CoachDashboard/ActiveTeamsList';
import { TraineesTable } from '../components/CoachDashboard/TraineesTable';

type Tab = 'analytics' | 'reports' | 'codeReview';

export function CoachDashboard() {
  const [activeTab, setActiveTab] = useState<Tab>('analytics');

  return (
    <div className="space-y-6">
      {/* Custom Coach Header (Replaces page title area) */}
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
            <button className="icon-button hover:text-dashboard-text">
              <Bell size={18} />
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

      {activeTab === 'analytics' && (
        <>
          {/* Top Overview Section */}
          <CoachOverview 
            totalTrainees={mockCoachData.totalTrainees}
            activeTeams={mockCoachData.activeTeams}
            avgSolveRate={mockCoachData.avgSolveRate}
          />

          {/* Main Grid: Left sidebar (Trends/Teams) + Right Main (Trainees Table) */}
          <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
            {/* Left Column */}
            <div className="xl:col-span-1 space-y-6 flex flex-col">
              <SubmissionTrends 
                acceptanceRatio={mockCoachData.acceptanceRatio}
                dailyActivityTrend={mockCoachData.dailyActivityTrend}
                dailyActivityData={mockCoachData.dailyActivityData}
              />
              <div className="flex-1">
                <ActiveTeamsList teams={mockCoachData.activeTeamsList} />
              </div>
            </div>

            {/* Right Column */}
            <div className="xl:col-span-2">
              <TraineesTable 
                trainees={mockCoachData.trainees} 
                totalTrainees={mockCoachData.totalTrainees} 
              />
            </div>
          </div>
        </>
      )}

      {activeTab !== 'analytics' && (
        <div className="flex items-center justify-center h-64 text-dashboard-muted glass-panel">
          <p className="font-semibold text-lg">{activeTab === 'reports' ? 'Weekly Reports' : 'Code Review'} - Coming Soon</p>
        </div>
      )}
    </div>
  );
}
