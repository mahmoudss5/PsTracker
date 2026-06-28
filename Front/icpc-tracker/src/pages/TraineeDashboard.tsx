import { ProfileCard } from '../components/dashboard/ProfileCard';
import { RankCard } from '../components/dashboard/RankCard';
import { ConsistencyHeatmap } from '../components/dashboard/ConsistencyHeatmap';
import { RecentSubmissions } from '../components/dashboard/RecentSubmissions';
import { mockProfileData } from '../data/mockProfile';
import { StatsGroup } from '../components/dashboard/StatsCard';
import { RatingProgressChart } from '../components/dashboard/RatingProgressChart';
import { RecentVerdictsChart } from '../components/dashboard/RecentVerdictsChart';

export function TraineeDashboard() {
  return (
    <div className="space-y-4 sm:space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 sm:gap-6">
        <div className="md:col-span-2">
          <ProfileCard
            username={mockProfileData.username}
            avatarUrl={mockProfileData.avatarUrl}
            title={mockProfileData.title}
            streak={mockProfileData.streak}
            bio={mockProfileData.bio}
            maxRank={mockProfileData.maxRank}
            maxRate={mockProfileData.maxRate}
          />
        </div>
        <div className="md:col-span-1">
          <RankCard
            rank={mockProfileData.globalRank}
            change={mockProfileData.weeklyChange}
            isPositive={mockProfileData.isPositiveChange}
          />
        </div>
      </div>
      
      {/* 3 Core Stats Cards */}
      <StatsGroup />

      {/* Visual Graphs: Rating Progress and Recent Verdicts Breakdown */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 sm:gap-6">
        <div className="md:col-span-2">
          <RatingProgressChart />
        </div>
        <div className="md:col-span-1">
          <RecentVerdictsChart />
        </div>
      </div>
      
      <ConsistencyHeatmap 
        data={mockProfileData.heatmapData} 
        totalSolved={mockProfileData.problemsSolved}
        submissionsToday={mockProfileData.submissionsToday}
      />
      
      <RecentSubmissions submissions={mockProfileData.recentSubmissions} />
    </div>
  );
}


