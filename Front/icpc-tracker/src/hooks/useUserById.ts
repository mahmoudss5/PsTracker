/**
 * useUserById
 *
 * Fetches a single user profile from GET /api/users/{id}.
 * Returns null while loading or if id is null.
 */

import { useQuery } from '@tanstack/react-query';
import type { TraineeResponse } from '../types/api.types';
import { getUserById } from '../services/userService';

interface UseUserByIdResult {
  user: TraineeResponse | null;
  isLoading: boolean;
  error: string | null;
}

export function useUserById(id: number | null): UseUserByIdResult {
  const query = useQuery({
    queryKey: ['user', id],
    queryFn: () => getUserById(id as number),
    enabled: id !== null,
  });

  return {
    user: query.data ?? null,
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
  };
}
