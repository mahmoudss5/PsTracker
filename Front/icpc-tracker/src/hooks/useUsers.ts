import { useQuery } from '@tanstack/react-query';
import { getAllUsers } from '../services/userService';
import type { TraineeResponse } from '../types/api.types';

interface UseUsersResult {
  users: TraineeResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useUsers(enabled = true): UseUsersResult {
  const query = useQuery({
    queryKey: ['users'],
    queryFn: getAllUsers,
    enabled,
  });

  return {
    users: query.data ?? [],
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: query.refetch,
  };
}
