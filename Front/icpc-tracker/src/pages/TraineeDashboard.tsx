

import { ProfileCard } from '../components/dashboard/ProfileCard';
import { RankCard } from '../components/dashboard/RankCard';
import { ConsistencyHeatmap } from '../components/dashboard/ConsistencyHeatmap';
import { RecentSubmissions } from '../components/dashboard/RecentSubmissions';
import { mockProfileData } from '../data/mockProfile';

export function TraineeDashboard() {
  
  return (
    <div className="p-4 space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="md:col-span-2">
          <ProfileCard
            username={mockProfileData.username}
            avatarUrl={mockProfileData.avatarUrl}
            title={mockProfileData.title}
            streak={mockProfileData.streak}
            bio={mockProfileData.bio}
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
      
      <ConsistencyHeatmap data={mockProfileData.heatmapData} />
      
      <RecentSubmissions submissions={mockProfileData.recentSubmissions} />
    </div>
  );
}
