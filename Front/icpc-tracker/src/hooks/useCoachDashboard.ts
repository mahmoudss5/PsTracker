/**
 * useCoachDashboard
 *
 * Derives all coach-dashboard metrics from the real backend:
 *   GET /api/teams/coach/me  →  TeamResponseDto[]
 *
 * Computed stats:
 *  - totalTrainees  : unique trainees across all teams
 *  - activeTeams    : team count
 *  - avgSolveRate   : average accepted-submissions ratio across all trainees
 *  - acceptanceRatio: same value (used by SubmissionTrends)
 */

import { useCoachTeams } from './useTeam';
import type { TraineeResponse, TeamResponse } from '../types/api.types';

export interface CoachDashboardStats {
  teams: TeamResponse[];
  /** All unique trainees collected from every team */
  allTrainees: TraineeResponse[];
  totalTrainees: number;
  activeTeams: number;
  /** Avg (solved / total submissions * 100) across all trainees. 0 when no data. */
  avgSolveRate: number;
  /** Same ratio — used by SubmissionTrends acceptanceRatio prop */
  acceptanceRatio: number;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

function computeAvgSolveRate(trainees: TraineeResponse[]): number {
  const withSubmissions = trainees.filter((t) => t.totalSumbissions > 0);
  if (withSubmissions.length === 0) return 0;
  const total = withSubmissions.reduce(
    (sum, t) => sum + (t.numberOfSolveProblems / t.totalSumbissions) * 100,
    0,
  );
  return Math.round((total / withSubmissions.length) * 10) / 10;
}

export function useCoachDashboard(): CoachDashboardStats {
  const { teams, isLoading, error, refetch } = useCoachTeams();

  // Collect unique trainees (a trainee can theoretically appear in only one team,
  // but we de-dup by id just to be safe)
  const seenIds = new Set<number>();
  const allTrainees: TraineeResponse[] = [];
  for (const team of teams) {
    for (const trainee of team.trainees) {
      if (!seenIds.has(trainee.id)) {
        seenIds.add(trainee.id);
        allTrainees.push(trainee);
      }
    }
  }

  const totalTrainees = allTrainees.length;
  const activeTeams = teams.length;
  const avgSolveRate = computeAvgSolveRate(allTrainees);

  return {
    teams,
    allTrainees,
    totalTrainees,
    activeTeams,
    avgSolveRate,
    acceptanceRatio: avgSolveRate,
    isLoading,
    error,
    refetch,
  };
}
