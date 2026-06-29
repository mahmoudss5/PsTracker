/**
 * submissionsService.ts
 *
 * Wraps every endpoint exposed by:
 *   SubmissionController  →  /api/submissions
 *
 * All functions throw a normalised Error on failure (via ErrorService).
 */

import { apiClient } from '../config/api.tsx';
import { normalizeApiError } from './ErrorService';
import type {
  SubmissionResponse,
  SubmissionCreateRequest,
  SubmissionUpdateRequest,
} from '../types/api.types';

const BASE = '/submissions';

/**
 * GET /api/submissions
 * Coach-level: fetch every submission in the system.
 */
export async function getAllSubmissions(): Promise<SubmissionResponse[]> {
  try {
    const res = await apiClient.get<SubmissionResponse[]>(BASE);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch all submissions');
  }
}

/**
 * GET /api/submissions/me
 * Returns the authenticated user's own submissions (synced from Codeforces).
 */
export async function getMySubmissions(): Promise<SubmissionResponse[]> {
  try {
    const res = await apiClient.get<SubmissionResponse[]>(`${BASE}/me`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch your submissions');
  }
}

/**
 * GET /api/submissions/user/:userId
 * Coach: fetch all submissions for a specific user.
 */
export async function getSubmissionsByUserId(
  userId: number,
): Promise<SubmissionResponse[]> {
  try {
    const res = await apiClient.get<SubmissionResponse[]>(`${BASE}/user/${userId}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch submissions for user ${userId}`);
  }
}

/**
 * GET /api/submissions/problem/:problemId
 */
export async function getSubmissionsByProblemId(
  problemId: number,
): Promise<SubmissionResponse[]> {
  try {
    const res = await apiClient.get<SubmissionResponse[]>(`${BASE}/problem/${problemId}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch submissions for problem ${problemId}`);
  }
}

/**
 * GET /api/submissions/:id
 */
export async function getSubmissionById(id: number): Promise<SubmissionResponse> {
  try {
    const res = await apiClient.get<SubmissionResponse>(`${BASE}/${id}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch submission ${id}`);
  }
}

/**
 * POST /api/submissions
 * Manually create a submission record (admin / coach usage).
 */
export async function createSubmission(
  dto: SubmissionCreateRequest,
): Promise<SubmissionResponse> {
  try {
    const res = await apiClient.post<SubmissionResponse>(BASE, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to create submission');
  }
}

/**
 * POST /api/submissions/me
 * Create a submission attributed to the authenticated user.
 */
export async function createMySubmission(
  dto: SubmissionCreateRequest,
): Promise<SubmissionResponse> {
  try {
    const res = await apiClient.post<SubmissionResponse>(`${BASE}/me`, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to create submission');
  }
}

/**
 * PUT /api/submissions/:id
 */
export async function updateSubmission(
  id: number,
  dto: SubmissionUpdateRequest,
): Promise<SubmissionResponse> {
  try {
    const res = await apiClient.put<SubmissionResponse>(`${BASE}/${id}`, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to update submission ${id}`);
  }
}

/**
 * DELETE /api/submissions/:id
 */
export async function deleteSubmission(id: number): Promise<void> {
  try {
    await apiClient.delete(`${BASE}/${id}`);
  } catch (err) {
    throw normalizeApiError(err, `Failed to delete submission ${id}`);
  }
}
