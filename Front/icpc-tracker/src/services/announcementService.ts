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

export interface CreateTeamAnnouncementRequest {
  type: 'URGENT' | 'UPDATE' | 'INFO';
  content: string;
  senderId: number;
  teamId: number;
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

/**
 * POST /api/announcment/sendAnnouncmnet
 * Coach sends an announcement to a team.
 * The backend handles WebSocket broadcast to /topic/teams/{teamId}/announcments automatically.
 */
export async function createTeamAnnouncement(req: CreateTeamAnnouncementRequest): Promise<void> {
  try {
    await apiClient.post(`${BASE}/sendAnnouncmnet`, {
      type: req.type,
      content: req.content,
      senderId: req.senderId,
      isTeamAnnouncment: true,
      receiverId: req.teamId,
    });
  } catch (err) {
    throw normalizeApiError(err, 'Failed to send announcement');
  }
}
