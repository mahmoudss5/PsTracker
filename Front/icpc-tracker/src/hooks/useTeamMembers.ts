/**
 * useTeamMembers.ts
 *
 * Loads all members of a specific team from GET /api/users/team/:teamId.
 * Returns a flat list of TraineeResponse objects (each includes stats).
 *
 * Useful for:
 *  - Coach's team overview
 *  - Populating the "view submissions" buttons in TeamPage
 *
 * Usage:
 *   const { members, isLoading, error } = useTeamMembers(teamId);
 */

import { useState, useEffect, useCallback } from 'react';
import type { TraineeResponse } from '../types/api.types';
import { getUsersByTeamId } from '../services/userService';

interface UseTeamMembersResult {
  members: TraineeResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useTeamMembers(teamId: number | null): UseTeamMembersResult {
  const [members, setMembers] = useState<TraineeResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (teamId === null) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getUsersByTeamId(teamId);
      setMembers(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load team members');
    } finally {
      setIsLoading(false);
    }
  }, [teamId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { members, isLoading, error, refetch: fetchData };
}
