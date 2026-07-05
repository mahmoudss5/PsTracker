import { useCallback, useEffect, useState } from 'react';
import { getAnnouncementsForTeam, getAnnouncementsForUser, type AnnouncementResponseDto } from '../services/announcementService';

interface UseAnnouncementsResult {
  announcements: AnnouncementResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useAnnouncements(teamId?: number, userId?: number): UseAnnouncementsResult {
  const [announcements, setAnnouncements] = useState<AnnouncementResponseDto[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (!teamId && !userId) return;
    setIsLoading(true);
    setError(null);
    try {
      if (teamId) {
        setAnnouncements(await getAnnouncementsForTeam(teamId));
      } else if (userId) {
        setAnnouncements(await getAnnouncementsForUser(userId));
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load announcements');
    } finally {
      setIsLoading(false);
    }
  }, [teamId, userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { announcements, isLoading, error, refetch: fetchData };
}
