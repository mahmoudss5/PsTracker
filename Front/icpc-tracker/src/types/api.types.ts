/**
 * Canonical TypeScript types that exactly mirror the backend Java DTOs.
 *
 * Backend package: com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto
 *
 * Naming convention:
 *  - Java record `SubmissionResponseDto`  →  TS interface `SubmissionResponse`
 *  - Java record `TraineResponse`         →  TS interface `TraineeResponse`
 *  - etc.
 */

// ── Auth ─────────────────────────────────────────────────────────────────────

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  codeforcesHandle: string;
  isCoach: boolean;
}

/** Shape returned by POST /api/auth/login and /api/auth/register */
export interface AuthResponse {
  token: string;
  userId: number;
  userName: string;
  isCoach: boolean;
}

// ── User ─────────────────────────────────────────────────────────────────────

/** Maps to Java record `TraineResponse` */
export interface TraineeResponse {
  id: number;
  userName: string;
  role: string;                        // "TRAINEE" | "COACH"
  email: string;
  teamName: string | null;
  numberOfSolveProblems: number;
  totalSumbissions: number;            // note: typo in backend kept intentionally
  numberOfTimeLimitVerdict: number;
  numberOfMemoryLimitVerdict: number;
  numberOfWrongAnswerVerdict: number;
}

// ── Team ─────────────────────────────────────────────────────────────────────

/** Maps to Java record `TeamResponseDto` */
export interface TeamResponse {
  id: number;
  teamName: string;
  teamCode: string;
  coachId: number;
  coachUsername: string;
  trainees: TraineeResponse[];
}

/** Maps to Java record `TeamCreateDto` */
export interface TeamCreateRequest {
  teamName: string;
}

/** Maps to Java record `TeamJoinDto` */
export interface TeamJoinRequest {
  teamCode: string;
}

/** POST /api/teams returns `{ teamCode: string }` */
export interface TeamCreateResponse {
  teamCode: string;
}

// ── Problem ───────────────────────────────────────────────────────────────────

/** Maps to Java record `ProblemResponseDto` */
export interface ProblemResponse {
  id: number;
  problemIndex: string;
  name: string;
  contestId: number;
  rating: number;
  tags: string[];
}

/** Maps to Java record `ProblemCreateDto` */
export interface ProblemCreateRequest {
  problemIndex: string;
  name: string;
  contestId: number;
  rating: number;
  tags: string[];
}

/** Maps to Java record `ProblemUpdateDto` */
export interface ProblemUpdateRequest {
  problemIndex: string;
  name: string;
  contestId: number;
  rating: number;
  tags: string[];
}

// ── Submission ─────────────────────────────────────────────────────────────────

/** Maps to Java record `SubmissionResponseDto` */
export interface SubmissionResponse {
  id: number;
  userId: number;
  userName: string;
  problemId: number;
  problemName: string;
  verdict: string;                     // "AC" | "WA" | "TLE" | "RE" | "MLE" (from Codeforces sync)
  timeConsumedMs: number;
  memoryConsumedBytes: number;
  createdAt: string;                   // ISO date-time string (LocalDateTime serialised by Jackson)
}

/** Maps to Java record `SubmissionCreateDto` */
export interface SubmissionCreateRequest {
  userId: number;
  problemId: number;
  verdict: string;
  timeConsumedMs: number;
  memoryConsumedBytes: number;
}

/** Maps to Java record `SubmissionUpdateDto` */
export interface SubmissionUpdateRequest {
  verdict: string;
  timeConsumedMs: number;
  memoryConsumedBytes: number;
}

// ── Tag ───────────────────────────────────────────────────────────────────────

/** Maps to Java record `TagResponseDto` */
export interface TagResponse {
  id: number;
  tagName: string;
}

export interface TagCreateRequest {
  tagName: string;
}

export interface TagUpdateRequest {
  tagName: string;
}
