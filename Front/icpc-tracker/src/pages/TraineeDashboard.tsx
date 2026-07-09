import { useState, useEffect } from 'react';
import { useCfAvatar } from '../hooks/useCfAvatar';
import { ProfileCard } from '../components/Traineedashboard/ProfileCard';
import { RankCard } from '../components/Traineedashboard/RankCard';
import { ConsistencyHeatmap } from '../components/Traineedashboard/ConsistencyHeatmap';
import { RecentSubmissions } from '../components/Traineedashboard/RecentSubmissions';
import { StatsGroup } from '../components/Traineedashboard/StatsCard';
import { RatingProgressChart } from '../components/Traineedashboard/RatingProgressChart';
import { RecentVerdictsChart } from '../components/Traineedashboard/RecentVerdictsChart';
import { useCurrentUser } from '../hooks/useCurrentUser';
import { useSubmissions } from '../hooks/useSubmissions';
import { AlertCircle, RefreshCw } from 'lucide-react';

export function TraineeDashboard() {
  const { user, isLoading: userLoading, error: userError } = useCurrentUser();
  
  const { submissions } = useSubmissions(
    user ? { userId: user.id } : {}
  );

  const cfAvatar = useCfAvatar(user?.codeforcesHandle);

  if (userLoading) {
    return (
      <div className="flex min-h-[12rem] flex-col items-center justify-center gap-3 p-8 text-center text-dashboard-muted">
        <RefreshCw size={28} className="animate-spin" />
        <p className="text-sm font-semibold">Loading dashboard...</p>
      </div>
    );
  }

  if (userError || !user) {
    return (
      <div className="flex min-h-[12rem] flex-col items-center justify-center gap-3 p-8 text-center">
        <AlertCircle size={32} className="text-red-500" />
        <p className="text-sm font-semibold text-dashboard-text">{userError || 'Failed to load user'}</p>
        <button
          onClick={() => window.location.reload()}
          className="mt-2 inline-flex items-center gap-2 rounded bg-dashboard-primary/10 px-4 py-2 text-sm font-semibold text-dashboard-primary transition-colors hover:bg-dashboard-primary hover:text-dashboard-primary-contrast"
        >
          <RefreshCw size={16} />
          Retry
        </button>
      </div>
    );
  }

  const acceptanceRate = user.totalSumbissions > 0 
    ? Math.round((user.numberOfSolveProblems / user.totalSumbissions) * 100) 
    : 0;

  // Consistency Heatmap requires submissions
  // Here we just use the raw submissions, ConsistencyHeatmap can process them.
  const submissionsData = submissions || [];
  const submissionsToday = submissionsData.filter(
    (s) => new Date(s.date).toDateString() === new Date().toDateString()
  ).length;

  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 sm:gap-6">
        <div className="md:col-span-2">
          <ProfileCard
            username={user.userName}
            avatarUrl={cfAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.codeforcesHandle || user.userName}&backgroundColor=transparent`}
            title={user.role}
            streak={0} // Backend doesn't have streak
            codeforcesHandle={user.codeforcesHandle}
            maxRank={user.maxRank || "Unrated"}
            maxRate={user.maxRate || 0}
            rate={user.rate || 0}
            rank={user.rank || "Unrated"}
          />
        </div>
        <div className="md:col-span-1">
          <RankCard
            acceptanceRate={acceptanceRate}
            totalSubmissions={user.totalSumbissions}
          />
        </div>
      </div>
      
      {/* 3 Core Stats Cards */}
      <StatsGroup 
        solved={user.numberOfSolveProblems}
        submissions={user.totalSumbissions}
        streak={0} // Mocked streak
      />

      {/* Visual Graphs: Rating Progress and Recent Verdicts Breakdown */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 sm:gap-6">
        <div className="md:col-span-2">
          <RatingProgressChart codeforcesHandle={user.codeforcesHandle} />
        </div>
        <div className="md:col-span-1">
          <RecentVerdictsChart 
            ac={user.numberOfSolveProblems}
            wa={user.numberOfWrongAnswerVerdict}
            tle={user.numberOfTimeLimitVerdict}
            mle={user.numberOfMemoryLimitVerdict}
            re={0} // RE is not in backend currently
            total={user.totalSumbissions}
          />
        </div>
      </div>
      
      <ConsistencyHeatmap 
        totalSolved={user.numberOfSolveProblems}
        submissionsToday={submissionsToday}
        submissions={submissionsData}
      />
      
      <RecentSubmissions submissions={submissionsData.slice(0, 5)} />
    </div>
  );
}


