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

import { useState, useEffect, useCallback } from 'react';
import type { TraineeResponse } from '../types/api.types';
import { getCurrentUser } from '../services/userService';

interface UseCurrentUserResult {
  user: TraineeResponse | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useCurrentUser(): UseCurrentUserResult {
  const [user, setUser] = useState<TraineeResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await getCurrentUser();
      setUser(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load user profile');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { user, isLoading, error, refetch: fetchData };
}
