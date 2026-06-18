export type SubmissionStatus = 'accepted' | 'time-limit' | 'wrong-answer';

export interface Submission {
  id: number;
  problem: string;
  language: string;
  status: SubmissionStatus;
  statusLabel: string;
  submittedAt: string;
}

export interface ActivityDay {
  date: string;
  submissions: number;
  intensity: 0 | 1 | 2 | 3 | 4;
}

export interface TraineeDashboardData {
  profile: {
    username: string;
    rank: number;
    level: string;
    initials: string;
  };
  rating: {
    current: number;
    change: number;
    solved: number;
    contests: number;
    history: number[];
  };
  consistency: {
    periodLabel: string;
    currentStreak: number;
    longestStreak: number;
    activity: ActivityDay[];
  };
  submissions: Submission[];
}
