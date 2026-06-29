export interface TraineeOverview {
  id: string;
  handle: string;
  name: string;
  rating: number;
  ratingRank: string;
  solved7d: number;
  solved7dTrend: number;
  status: 'ACTIVE' | 'IN CONTEST' | 'OFFLINE';
}

export interface ActiveTeam {
  id: string;
  name: string;
  code: string;
  avgRating: number;
  members: { name: string; avatarUrl?: string }[];
}

export const mockCoachData = {
  totalTrainees: 124,
  activeTeams: 8,
  avgSolveRate: 68.4,
  acceptanceRatio: 72,
  dailyActivityTrend: 12.5,
  dailyActivityData: [40, 60, 45, 80, 50, 110, 75, 40, 65, 85, 55],
  activeTeamsList: [
    {
      id: 't1',
      name: 'Alpha Squad',
      code: 'AT-99X2',
      avgRating: 2140,
      members: [
        { name: 'BinaryTamer' },
        { name: 'QuickKort' },
        { name: 'DataXLR' },
      ],
    },
    {
      id: 't2',
      name: 'Beta Testers',
      code: 'AT-42B1',
      avgRating: 1865,
      members: [
        { name: 'SegManiac' },
        { name: 'NullPointer' },
      ],
    },
  ] as ActiveTeam[],
  trainees: [
    {
      id: 'u1',
      handle: 'BinaryTamer',
      name: 'BinaryTamer',
      rating: 2984,
      ratingRank: 'LGM • Codeforces',
      solved7d: 42,
      solved7dTrend: 12,
      status: 'ACTIVE',
    },
    {
      id: 'u2',
      handle: 'QuickKort',
      name: 'QuickKort',
      rating: 2410,
      ratingRank: 'Master • AtCoder',
      solved7d: 28,
      solved7dTrend: -8,
      status: 'ACTIVE',
    },
    {
      id: 'u3',
      handle: 'DataXLR',
      name: 'DataXLR',
      rating: 1950,
      ratingRank: 'Candidate • CF',
      solved7d: 15,
      solved7dTrend: 4,
      status: 'IN CONTEST',
    },
    {
      id: 'u4',
      handle: 'SegManiac',
      name: 'SegManiac',
      rating: 1720,
      ratingRank: 'Expert • AtCoder',
      solved7d: 31,
      solved7dTrend: 22,
      status: 'OFFLINE',
    },
    {
      id: 'u5',
      handle: 'NullPointer',
      name: 'NullPointer',
      rating: 1340,
      ratingRank: 'Pupil • Codeforces',
      solved7d: 5,
      solved7dTrend: -15,
      status: 'ACTIVE',
    },
  ] as TraineeOverview[],
};
