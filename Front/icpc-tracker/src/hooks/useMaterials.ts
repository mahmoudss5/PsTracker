import { useQuery } from '@tanstack/react-query';
import { getTeamMaterials, type MaterialResponseDto } from '../services/materialService';

interface UseMaterialsResult {
  materials: MaterialResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useMaterials(teamId?: number): UseMaterialsResult {
  const query = useQuery({
    queryKey: ['materials', teamId],
    queryFn: () => getTeamMaterials(teamId as number),
    enabled: teamId !== undefined,
  });

  return {
    materials: query.data ?? [],
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
