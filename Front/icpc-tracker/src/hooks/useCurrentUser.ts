/**
 * useCurrentUser.ts
 *
 * Loads the authenticated user's profile from GET /api/users/me.
 * The backend returns TraineResponse which contains all the stats fields
 * needed by the TraineeDashboard (ProfileCard, StatsGroup, RankCard, etc.).
 *
 * Usage:
 *   const { user, isLoading, error, refetch } = useCurrentUser();
 */

import { useQuery } from '@tanstack/react-query';
import type { TraineeResponse } from '../types/api.types';
import { getCurrentUser } from '../services/userService';

interface UseCurrentUserResult {
  user: TraineeResponse | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useCurrentUser(): UseCurrentUserResult {
  const query = useQuery({
    queryKey: ['current-user'],
    queryFn: getCurrentUser,
  });

  return {
    user: query.data ?? null,
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
