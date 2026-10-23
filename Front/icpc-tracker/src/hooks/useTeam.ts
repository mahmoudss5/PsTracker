/**
 * useTeam.ts
 *
 * Loads full team details from GET /api/teams/:id.
 * Returns coach + trainee list (with per-trainee stats) from TeamResponseDto.
 *
 * Usage — pass the teamId from the authenticated user's profile:
 *   const { user } = useCurrentUser();
 *   const teamId = ... // derive from user or localStorage
 *   const { team, isLoading, error } = useTeam(teamId);
 */

import { useQuery } from '@tanstack/react-query';
import type { TeamResponse } from '../types/api.types';
import { getCurrentCoachTeams, getTeamById } from '../services/teamService';

interface UseTeamResult {
  team: TeamResponse | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useTeam(teamId: number | null): UseTeamResult {
  const query = useQuery({
    queryKey: ['team', teamId],
    queryFn: () => getTeamById(teamId as number),
    enabled: teamId !== null,
  });

  return {
    team: query.data ?? null,
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}

interface UseCoachTeamsResult {
  teams: TeamResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useCoachTeams(enabled = true): UseCoachTeamsResult {
  const query = useQuery({
    queryKey: ['coach-teams'],
    queryFn: getCurrentCoachTeams,
    enabled,
  });

  return {
    teams: query.data ?? [],
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
