import { useQuery } from '@tanstack/react-query';
import { getAnnouncementsForTeam, getAnnouncementsForUser, type AnnouncementResponseDto } from '../services/announcementService';

interface UseAnnouncementsResult {
  announcements: AnnouncementResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useAnnouncements(teamId?: number, userId?: number): UseAnnouncementsResult {
  const query = useQuery({
    queryKey: ['announcements', teamId, userId],
    queryFn: async (): Promise<AnnouncementResponseDto[]> => {
      if (teamId) return getAnnouncementsForTeam(teamId);
      if (userId) return getAnnouncementsForUser(userId);
      return [];
    },
    enabled: Boolean(teamId || userId),
  });

  return {
    announcements: query.data ?? [],
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
