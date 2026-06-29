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

// ── Submissions Page ─────────────────────────────────────────────────────────

export type Verdict = 'AC' | 'WA' | 'TLE' | 'RE' | 'MLE';

export interface SubmissionEntry {
  id: string;
  problemName: string;
  verdict: Verdict;
  language: string;
  runtimeMs: number;
  memoryKB: number;
  date: string; // ISO date string: "2026-06-15T14:30:00Z"
}

export type TimePeriod = '1d' | '7d' | '30d' | 'custom' | 'all';

export interface CustomRange {
  from: Date;
  to: Date;
}

export interface VerdictsBreakdown {
  AC: number;
  WA: number;
  TLE: number;
  RE: number;
  MLE: number;
}

export interface TraineeSubmissionsStats {
  totalSubmissions: number;
  acceptedCount: number;
  acceptanceRate: number;       // 0–100
  longestStreak: number;        // consecutive days with ≥1 submission
  avgRuntimeMs: number;         // avg runtime of AC submissions
  bestRuntimeMs: number;        // fastest AC submission
  problemsSolved: number;       // unique problems with at least 1 AC
  mostActiveDay: string;        // "Mon", "Tue", …
  verdictsBreakdown: VerdictsBreakdown;
  languageCounts: Record<string, number>;
  dailyActivity: { date: string; count: number }[];
}

export interface TraineeProfile {
  handle: string;
  name: string;
  avatarInitials: string;
  role: 'member' | 'coach';
}
