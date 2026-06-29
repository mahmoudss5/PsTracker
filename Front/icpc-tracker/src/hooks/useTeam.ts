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

import { useState, useEffect, useCallback } from 'react';
import type { TeamResponse } from '../types/api.types';
import { getCurrentCoachTeams, getTeamById } from '../services/teamService';

interface UseTeamResult {
  team: TeamResponse | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useTeam(teamId: number | null): UseTeamResult {
  const [team, setTeam] = useState<TeamResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (teamId === null) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getTeamById(teamId);
      setTeam(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load team');
    } finally {
      setIsLoading(false);
    }
  }, [teamId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { team, isLoading, error, refetch: fetchData };
}

interface UseCoachTeamsResult {
  teams: TeamResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useCoachTeams(enabled = true): UseCoachTeamsResult {
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (!enabled) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getCurrentCoachTeams();
      setTeams(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load coach teams');
    } finally {
      setIsLoading(false);
    }
  }, [enabled]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { teams, isLoading, error, refetch: fetchData };
}
