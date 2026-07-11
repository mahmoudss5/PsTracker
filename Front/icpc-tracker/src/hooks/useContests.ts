import { useCallback, useEffect, useState } from "react";
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
  const [contests, setContests] = useState<ContestResponseDto[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const endpoint = userId ? `/contests/user/${userId}` : `/contests/me`;
      const res = await apiClient.get<ContestResponseDto[]>(endpoint);
      setContests(res.data);
    } catch (err) {
      setError(normalizeApiError(err, "Failed to load contests").message);
    } finally {
      setIsLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { contests, isLoading, error, refetch: fetchData };
}
