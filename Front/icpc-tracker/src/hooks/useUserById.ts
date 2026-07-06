/**
 * useUserById
 *
 * Fetches a single user profile from GET /api/users/{id}.
 * Returns null while loading or if id is null.
 */

import { useState, useEffect, useCallback } from 'react';
import type { TraineeResponse } from '../types/api.types';
import { getUserById } from '../services/userService';

interface UseUserByIdResult {
  user: TraineeResponse | null;
  isLoading: boolean;
  error: string | null;
}

export function useUserById(id: number | null): UseUserByIdResult {
  const [user, setUser] = useState<TraineeResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (id === null) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getUserById(id);
      setUser(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : `Failed to load user ${id}`);
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { user, isLoading, error };
}
