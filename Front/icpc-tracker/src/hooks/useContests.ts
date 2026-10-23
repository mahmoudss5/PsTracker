import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../config/api";
import { normalizeApiError } from "../services/ErrorService";

export interface ContestResponseDto {
  id: number;
  contestId: number;
  contestName: string;
  rank: number;
  oldRating: number;
  newRating: number;
  ratingUpdateTime: string;
}

export function useContests(userId?: number) {
  const query = useQuery({
    queryKey: ['contests', userId],
    queryFn: async (): Promise<ContestResponseDto[]> => {
      const endpoint = userId ? `/contests/user/${userId}` : '/contests/me';
      try {
        const response = await apiClient.get<ContestResponseDto[]>(endpoint);
        return response.data;
      } catch (error) {
        throw normalizeApiError(error, 'Failed to load contests');
      }
    },
  });

  return {
    contests: query.data ?? [],
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
