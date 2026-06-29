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

export interface Material {
  id: number;
  kind: "link" | "pdf" | "image";
  title: string;
  subtitle: string;
  size?: string;
}

export const MOCK_MATERIALS: Material[] = [
  {
    id: 1,
    kind: "link",
    title: "CF Edu Round Editorial",
    subtitle: "codeforces.com/blog/entry/...",
  },
  {
    id: 2,
    kind: "pdf",
    title: "Max Flow Min Cut Theorem",
    subtitle: "2.4 MB • Uploaded by Coach A",
    size: "2.4 MB",
  },
  {
    id: 3,
    kind: "image",
    title: "Convex Hull Proof",
    subtitle: "Geometric approach visualization",
  },
  {
    id: 4,
    kind: "pdf",
    title: "DP on Trees — Master Sheet",
    subtitle: "1.1 MB • Uploaded by Coach B",
    size: "1.1 MB",
  },
  {
    id: 5,
    kind: "link",
    title: "Segment Tree Lazy Propagation",
    subtitle: "cp-algorithms.com/data_structures/...",
  },
  {
    id: 6,
    kind: "image",
    title: "Network Flow Diagram",
    subtitle: "Uploaded by Coach A",
  },
];

//  announcment
interface Announcement {
  id: number;
  type: "URGENT" | "UPDATE" | "INFO";
  title: string;
  body: string;
  author: string;
  date: string;
  time: string;
}

export const MOCK_ANNOUNCEMENTS: Announcement[] = [
  {
    id: 1,
    type: "URGENT",
    title: "Weekly Practice Set #4 Live",
    body: "Focus is on Advanced Graph Theory (Flows/Matchings). Mandatory submission by Friday 23:59 UTC.",
    author: "Coach A.",
    date: "Today",
    time: "09:00",
  },
  {
    id: 2,
    type: "UPDATE",
    title: "Codeforces Edu Round Analysis",
    body: "We will go over the editorial for problem F during tomorrow's team meeting. Review the editorial beforehand.",
    author: "Coach A.",
    date: "Yesterday",
    time: "14:30",
  },
  {
    id: 3,
    type: "INFO",
    title: "New Training Schedule Posted",
    body: "The updated weekly schedule has been uploaded to Training Materials. Check your assigned problem sets.",
    author: "Coach B.",
    date: "Mon",
    time: "11:15",
  },
  {
    id: 4,
    type: "INFO",
    title: "Regional Qualifier Registration Open",
    body: "Registration for the regional ICPC qualifier closes on July 15. Make sure your team profile is up to date.",
    author: "Coach A.",
    date: "Sun",
    time: "16:00",
  },
];

export const typeColor: Record<Announcement["type"], string> = {
  URGENT: "text-red-400 bg-red-500/10 border-red-500/20",
  UPDATE: "text-yellow-400 bg-yellow-500/10 border-yellow-500/20",
  INFO: "text-dashboard-primary bg-dashboard-primary/10 border-dashboard-primary/20",
};
export const typeBorder: Record<Announcement["type"], string> = {
  URGENT: "border-l-red-500",
  UPDATE: "border-l-yellow-400",
  INFO: "border-l-dashboard-primary",
};


export interface Message {
  id: number;
  sender: string;
  avatar: string; // initials
  content: string;
  code?: string;
  time: string;
  isYou?: boolean;
  isCoach?: boolean;
}



// ─────────────────────────────────────────────
// Mock data
// ─────────────────────────────────────────────
export const MOCK_MESSAGES: Message[] = [
  {
    id: 1,
    sender: "tourist_fan",
    avatar: "TF",
    content:
        "Did anyone look at problem D from yesterday? The segment tree approach is TLEing on test 45.",
    time: "10:42 AM",
  },
  {
    id: 2,
    sender: "You",
    avatar: "ME",
    content: "You need lazy propagation. I pushed a snippet to the library.",
    code: `void push(int v) {\n  if (lazy[v]) {\n    tree[v*2] += lazy[v];\n    ...\n  }\n}`,
    time: "10:45 AM",
    isYou: true,
  },
  {
    id: 3,
    sender: "dp_master",
    avatar: "DM",
    content: "Ah, nice. Checking now.",
    time: "10:47 AM",
  },
  {
    id: 4,
    sender: "Coach A.",
    avatar: "CA",
    content:
        "Good debug work. For the mandatory set, focus on problems E and F — both require segment trees with lazy propagation. Deadline is Friday 23:59 UTC.",
    time: "10:50 AM",
    isCoach: true,
  },
  {
    id: 5,
    sender: "graph_god",
    avatar: "GG",
    content: "I had the same issue yesterday — the trick is to push before both recursive calls.",
    time: "10:52 AM",
  },
  {
    id: 6,
    sender: "You",
    avatar: "ME",
    content: "Exactly. Also make sure you reset lazy[v] to 0 after pushing.",
    time: "10:55 AM",
    isYou: true,
  },
  {
    id: 7,
    sender: "Coach A.",
    avatar: "CA",
    content: "Review the editorial for Edu Round 145 problem F before tomorrow's session. I'll post a summary in Training Materials tonight.",
    time: "11:03 AM",
    isCoach: true,
  },
];

// ── Submissions Page Mock Data ───────────────────────────────────────────────

import type { SubmissionEntry, TraineeProfile } from '../types/dashboard.types';

// Helper to generate a date string N days ago at a given hour
function daysAgo(days: number, hour = 14, minute = 0): string {
  const d = new Date();
  d.setDate(d.getDate() - days);
  d.setHours(hour, minute, 0, 0);
  return d.toISOString();
}

const PROBLEMS = [
  '1. Two Sum',
  '2. Add Two Numbers',
  '3. Longest Substring Without Repeating',
  '4. Median of Two Sorted Arrays',
  '5. Longest Palindromic Substring',
  '11. Container With Most Water',
  '15. 3Sum',
  '20. Valid Parentheses',
  '21. Merge Two Sorted Lists',
  '42. Trapping Rain Water',
  '53. Maximum Subarray',
  '56. Merge Intervals',
  '84. Largest Rectangle in Histogram',
  '98. Validate Binary Search Tree',
  '104. Maximum Depth of Binary Tree',
  '121. Best Time to Buy and Sell Stock',
  '128. Longest Consecutive Sequence',
  '141. Linked List Cycle',
  '200. Number of Islands',
  '207. Course Schedule',
  '212. Word Search II',
  '238. Product of Array Except Self',
  '295. Find Median from Data Stream',
  '300. Longest Increasing Subsequence',
  '322. Coin Change',
  '347. Top K Frequent Elements',
  '394. Decode String',
  '416. Partition Equal Subset Sum',
  '438. Find All Anagrams in a String',
  '567. Permutation in String',
];

const LANGUAGES = ['C++', 'C++', 'C++', 'Python', 'Python', 'Java', 'Go'];

function makeEntry(id: number, problemIdx: number, verdict: SubmissionEntry['verdict'], days: number, hour: number): SubmissionEntry {
  const lang = LANGUAGES[id % LANGUAGES.length];
  const baseRuntime = verdict === 'AC' ? 50 + (id * 17) % 800 : 2000 + (id * 31) % 1000;
  const baseMem = 8000 + (id * 53) % 40000;
  return {
    id: String(id),
    problemName: PROBLEMS[problemIdx % PROBLEMS.length],
    verdict,
    language: lang,
    runtimeMs: baseRuntime,
    memoryKB: baseMem,
    date: daysAgo(days, hour, (id * 7) % 60),
  };
}

export const MOCK_FULL_SUBMISSIONS: SubmissionEntry[] = [
  // Today (day 0)
  makeEntry(1,  0,  'AC',  0, 9),
  makeEntry(2,  1,  'WA',  0, 10),
  makeEntry(3,  1,  'AC',  0, 11),
  makeEntry(4,  2,  'TLE', 0, 14),
  makeEntry(5,  3,  'AC',  0, 16),
  makeEntry(6,  3,  'RE',  0, 17),
  // Day 1
  makeEntry(7,  4,  'AC',  1, 8),
  makeEntry(8,  5,  'WA',  1, 9),
  makeEntry(9,  5,  'WA',  1, 10),
  makeEntry(10, 5,  'AC',  1, 11),
  makeEntry(11, 6,  'MLE', 1, 15),
  makeEntry(12, 6,  'AC',  1, 16),
  // Day 2
  makeEntry(13, 7,  'AC',  2, 9),
  makeEntry(14, 8,  'TLE', 2, 10),
  makeEntry(15, 9,  'AC',  2, 13),
  makeEntry(16, 10, 'WA',  2, 18),
  // Day 3 (no submissions — breaks streak)
  // Day 4
  makeEntry(17, 11, 'AC',  4, 10),
  makeEntry(18, 12, 'RE',  4, 11),
  makeEntry(19, 12, 'AC',  4, 12),
  makeEntry(20, 13, 'WA',  4, 15),
  makeEntry(21, 13, 'AC',  4, 16),
  // Day 5
  makeEntry(22, 14, 'AC',  5, 8),
  makeEntry(23, 15, 'TLE', 5, 9),
  makeEntry(24, 15, 'MLE', 5, 10),
  makeEntry(25, 15, 'AC',  5, 11),
  makeEntry(26, 16, 'AC',  5, 14),
  // Day 6
  makeEntry(27, 17, 'WA',  6, 9),
  makeEntry(28, 17, 'AC',  6, 10),
  makeEntry(29, 18, 'AC',  6, 13),
  // Day 7
  makeEntry(30, 19, 'AC',  7, 11),
  makeEntry(31, 20, 'RE',  7, 12),
  makeEntry(32, 20, 'AC',  7, 13),
  makeEntry(33, 21, 'TLE', 7, 16),
  makeEntry(34, 21, 'AC',  7, 17),
  // Day 10
  makeEntry(35, 22, 'AC',  10, 10),
  makeEntry(36, 23, 'WA',  10, 11),
  makeEntry(37, 23, 'AC',  10, 12),
  // Day 12
  makeEntry(38, 24, 'AC',  12, 9),
  makeEntry(39, 25, 'WA',  12, 10),
  makeEntry(40, 25, 'TLE', 12, 11),
  makeEntry(41, 25, 'AC',  12, 14),
  makeEntry(42, 26, 'MLE', 12, 15),
  // Day 15
  makeEntry(43, 27, 'AC',  15, 8),
  makeEntry(44, 28, 'AC',  15, 9),
  makeEntry(45, 29, 'RE',  15, 11),
  makeEntry(46, 29, 'WA',  15, 12),
  makeEntry(47, 29, 'AC',  15, 13),
  // Day 18
  makeEntry(48, 0,  'WA',  18, 10),
  makeEntry(49, 0,  'AC',  18, 11),
  makeEntry(50, 1,  'AC',  18, 14),
  // Day 20
  makeEntry(51, 2,  'TLE', 20, 9),
  makeEntry(52, 2,  'AC',  20, 10),
  makeEntry(53, 3,  'AC',  20, 13),
  makeEntry(54, 4,  'WA',  20, 16),
  // Day 25
  makeEntry(55, 5,  'AC',  25, 8),
  makeEntry(56, 6,  'RE',  25, 9),
  makeEntry(57, 6,  'AC',  25, 10),
  makeEntry(58, 7,  'MLE', 25, 14),
  // Day 30
  makeEntry(59, 8,  'AC',  30, 10),
  makeEntry(60, 9,  'WA',  30, 11),
  makeEntry(61, 9,  'AC',  30, 13),
  makeEntry(62, 10, 'TLE', 30, 15),
  makeEntry(63, 10, 'AC',  30, 16),
];

export const MOCK_TRAINEES: TraineeProfile[] = [
  { handle: 'tourist_wannabe', name: 'Mahmoud Aziz', avatarInitials: 'MA', role: 'coach' },
  { handle: 'ahmed_sayed',     name: 'Ahmed Sayed',  avatarInitials: 'AS', role: 'member' },
  { handle: 'jana_cp',         name: 'Jana',          avatarInitials: 'JA', role: 'member' },
  { handle: 'ahmed_mohsed',    name: 'Ahmed Mohsed', avatarInitials: 'AM', role: 'member' },
];
