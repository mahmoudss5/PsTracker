export interface Submission {
  id: string;
  problemName: string;
  verdict: 'Accepted' | 'Time Limit Exceeded' | 'Wrong Answer' | 'Runtime Error';
  attempts: number;
  totalTime: string;
}

export const mockProfileData = {
  username: 'tourist_wannabe',
  avatarUrl: 'https://i.pravatar.cc/150?u=tourist_wannabe',
  title: 'EXPERT',
  streak: 14,
  bio: 'Focusing on Dynamic Programming and Advanced Graph Algorithms.\nPrepping for upcoming regional qualifiers.',
  globalRank: 4208,
  weeklyChange: 342,
  isPositiveChange: true,
  problemsSolved: 845,
  submissionsToday: 8,
  maxRank: 'Candidate Master',
  maxRate: 1985,
  avgSubmissionsToAccept: 1.4,
  heatmapData: Array.from({ length: 365 }, () => Math.floor(Math.random() * 5)),
  recentSubmissions: [
    {
      id: '1',
      problemName: '1482. Minimum Number of Days to Make m Bouquets',
      verdict: 'Accepted',
      attempts: 2,
      totalTime: '45m 12s',
    },
    {
      id: '2',
      problemName: '875. Koko Eating Bananas',
      verdict: 'Accepted',
      attempts: 1,
      totalTime: '22m 05s',
    },
    {
      id: '3',
      problemName: '410. Split Array Largest Sum',
      verdict: 'Time Limit Exceeded',
      attempts: 4,
      totalTime: '1h 15m 30s',
    },
    {
      id: '4',
      problemName: '1011. Capacity To Ship Packages',
      verdict: 'Accepted',
      attempts: 3,
      totalTime: '38m 45s',
    },
  ] as Submission[],
};
