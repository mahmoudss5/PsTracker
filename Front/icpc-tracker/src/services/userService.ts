/**
 * userService.ts
 *
 * Wraps every endpoint exposed by:
 *   UserController  →  /api/users
 *
 * Response shape: TraineResponse (Java) → TraineeResponse (TypeScript)
 */

import { apiClient } from '../config/api';
import { normalizeApiError } from './ErrorService';
import type { TraineeResponse, UpdateProfileRequest, UpdatePasswordRequest } from '../types/api.types';

const BASE = '/users';

/**
 * GET /api/users/me
 * Returns the full profile of the currently authenticated user.
 */
export async function getCurrentUser(): Promise<TraineeResponse> {
  try {
    const res = await apiClient.get<TraineeResponse>(`${BASE}/me`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch current user');
  }
}

/**
 * GET /api/users
 * Returns all users (admin / coach usage).
 */
export async function getAllUsers(): Promise<TraineeResponse[]> {
  try {
    const res = await apiClient.get<TraineeResponse[]>(BASE);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch users');
  }
}

/**
 * GET /api/users/team/:teamId
 * Returns all trainees that belong to a specific team.
 */
export async function getUsersByTeamId(
  teamId: number,
): Promise<TraineeResponse[]> {
  try {
    const res = await apiClient.get<TraineeResponse[]>(`${BASE}/team/${teamId}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch members for team ${teamId}`);
  }
}

/**
 * GET /api/users/:id
 */
export async function getUserById(id: number): Promise<TraineeResponse> {
  try {
    const res = await apiClient.get<TraineeResponse>(`${BASE}/${id}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch user ${id}`);
  }
}

export async function updateProfile(data: UpdateProfileRequest): Promise<TraineeResponse> {
  try {
    const response = await apiClient.put<TraineeResponse>(`${BASE}/me/profile`, data);
    return response.data;
  } catch (error) {
    throw normalizeApiError(error, 'Failed to update profile');
  }
}

export async function updatePassword(data: UpdatePasswordRequest): Promise<void> {
  try {
    await apiClient.put(`${BASE}/me/password`, data);
  } catch (error) {
    throw normalizeApiError(error, 'Failed to update password');
  }
}
