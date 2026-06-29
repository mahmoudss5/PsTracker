import { useCallback, useEffect, useState } from 'react';
import { getAllUsers } from '../services/userService';
import type { TraineeResponse } from '../types/api.types';

interface UseUsersResult {
  users: TraineeResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useUsers(enabled = true): UseUsersResult {
  const [users, setUsers] = useState<TraineeResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (!enabled) return;
    setIsLoading(true);
    setError(null);
    try {
      setUsers(await getAllUsers());
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load users');
    } finally {
      setIsLoading(false);
    }
  }, [enabled]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { users, isLoading, error, refetch: fetchData };
}
