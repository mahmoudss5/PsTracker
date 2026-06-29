/**
 * teamService.ts
 *
 * Wraps every endpoint exposed by:
 *   TeamController  →  /api/teams
 *
 * Response shapes: TeamResponseDto  →  TeamResponse
 */

import { apiClient } from '../config/api.tsx';
import { normalizeApiError } from './ErrorService';
import type {
  TeamResponse,
  TeamCreateRequest,
  TeamJoinRequest,
  TeamCreateResponse,
} from '../types/api.types';

const BASE = '/teams';

/**
 * POST /api/teams
 * Coach creates a new team. Returns the generated team invite code.
 */
export async function createTeam(
  dto: TeamCreateRequest,
): Promise<TeamCreateResponse> {
  try {
    const res = await apiClient.post<TeamCreateResponse>(BASE, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to create team');
  }
}

/**
 * POST /api/teams/join
 * Trainee joins a team using an invite code.
 */
export async function joinTeam(dto: TeamJoinRequest): Promise<void> {
  try {
    await apiClient.post(`${BASE}/join`, dto);
  } catch (err) {
    throw normalizeApiError(err, 'Failed to join team');
  }
}

/**
 * POST /api/teams/leave/:teamId
 * Authenticated user leaves the given team.
 */
export async function leaveTeam(teamId: number): Promise<void> {
  try {
    await apiClient.post(`${BASE}/leave/${teamId}`);
  } catch (err) {
    throw normalizeApiError(err, 'Failed to leave team');
  }
}

/**
 * GET /api/teams/:id
 * Returns full team details including the coach and all trainees.
 */
export async function getTeamById(id: number): Promise<TeamResponse> {
  try {
    const res = await apiClient.get<TeamResponse>(`${BASE}/${id}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch team ${id}`);
  }
}

/**
 * GET /api/teams/coach/me
 * Returns teams created by the authenticated coach.
 */
export async function getCurrentCoachTeams(): Promise<TeamResponse[]> {
  try {
    const res = await apiClient.get<TeamResponse[]>(`${BASE}/coach/me`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch coach teams');
  }
}
