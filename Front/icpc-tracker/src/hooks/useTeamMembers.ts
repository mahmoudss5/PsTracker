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

import { useQuery } from '@tanstack/react-query';
import type { TraineeResponse } from '../types/api.types';
import { getUsersByTeamId } from '../services/userService';

interface UseTeamMembersResult {
  members: TraineeResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useTeamMembers(teamId: number | null): UseTeamMembersResult {
  const query = useQuery({
    queryKey: ['team-members', teamId],
    queryFn: () => getUsersByTeamId(teamId as number),
    enabled: teamId !== null,
  });

  return {
    members: query.data ?? [],
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
