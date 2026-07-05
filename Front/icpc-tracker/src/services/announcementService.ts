import { apiClient } from '../config/api';
import { normalizeApiError } from './ErrorService';

export interface AnnouncementResponseDto {
  id: number;
  type: 'URGENT' | 'UPDATE' | 'INFO';
  content: string;
  senderId: number;
  receiverId: number;
  createdAt: string;
}

const BASE = '/announcment';

export async function getAnnouncementsForUser(userId: number): Promise<AnnouncementResponseDto[]> {
  try {
    const res = await apiClient.get<AnnouncementResponseDto[]>(`${BASE}/getAllForUser`, {
      params: { userId },
    });
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch user announcements');
  }
}

export async function getAnnouncementsForTeam(teamId: number): Promise<AnnouncementResponseDto[]> {
  try {
    const res = await apiClient.get<AnnouncementResponseDto[]>(`${BASE}/getAllForTeam`, {
      params: { teamId },
    });
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch team announcements');
  }
}
