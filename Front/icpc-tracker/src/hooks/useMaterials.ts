import { useCallback, useEffect, useState } from 'react';
import { getTeamMaterials, type MaterialResponseDto } from '../services/materialService';

interface UseMaterialsResult {
  materials: MaterialResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useMaterials(teamId?: number): UseMaterialsResult {
  const [materials, setMaterials] = useState<MaterialResponseDto[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (!teamId) return;
    setIsLoading(true);
    setError(null);
    try {
      setMaterials(await getTeamMaterials(teamId));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load materials');
    } finally {
      setIsLoading(false);
    }
  }, [teamId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { materials, isLoading, error, refetch: fetchData };
}
