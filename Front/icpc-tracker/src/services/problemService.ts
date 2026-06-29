/**
 * problemService.ts
 *
 * Wraps every endpoint exposed by:
 *   ProblemController  →  /api/problems
 *   TagController      →  /api/tags
 *
 * Problems and Tags are tightly coupled (problem carries a list of tag names),
 * so both are in this file.
 */

import { apiClient } from '../config/api.tsx';
import { normalizeApiError } from './ErrorService';
import type {
  ProblemResponse,
  ProblemCreateRequest,
  ProblemUpdateRequest,
  TagResponse,
  TagCreateRequest,
  TagUpdateRequest,
} from '../types/api.types';

// ── Problems ─────────────────────────────────────────────────────────────────

const PROBLEMS_BASE = '/problems';

/**
 * GET /api/problems
 */
export async function getAllProblems(): Promise<ProblemResponse[]> {
  try {
    const res = await apiClient.get<ProblemResponse[]>(PROBLEMS_BASE);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch problems');
  }
}

/**
 * GET /api/problems/:id
 */
export async function getProblemById(id: number): Promise<ProblemResponse> {
  try {
    const res = await apiClient.get<ProblemResponse>(`${PROBLEMS_BASE}/${id}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch problem ${id}`);
  }
}

/**
 * POST /api/problems
 */
export async function createProblem(
  dto: ProblemCreateRequest,
): Promise<ProblemResponse> {
  try {
    const res = await apiClient.post<ProblemResponse>(PROBLEMS_BASE, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to create problem');
  }
}

/**
 * PUT /api/problems/:id
 */
export async function updateProblem(
  id: number,
  dto: ProblemUpdateRequest,
): Promise<ProblemResponse> {
  try {
    const res = await apiClient.put<ProblemResponse>(`${PROBLEMS_BASE}/${id}`, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to update problem ${id}`);
  }
}

/**
 * DELETE /api/problems/:id
 */
export async function deleteProblem(id: number): Promise<void> {
  try {
    await apiClient.delete(`${PROBLEMS_BASE}/${id}`);
  } catch (err) {
    throw normalizeApiError(err, `Failed to delete problem ${id}`);
  }
}

// ── Tags ─────────────────────────────────────────────────────────────────────

const TAGS_BASE = '/tags';

/**
 * GET /api/tags
 */
export async function getAllTags(): Promise<TagResponse[]> {
  try {
    const res = await apiClient.get<TagResponse[]>(TAGS_BASE);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch tags');
  }
}

/**
 * GET /api/tags/:id
 */
export async function getTagById(id: number): Promise<TagResponse> {
  try {
    const res = await apiClient.get<TagResponse>(`${TAGS_BASE}/${id}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to fetch tag ${id}`);
  }
}

/**
 * POST /api/tags
 */
export async function createTag(dto: TagCreateRequest): Promise<TagResponse> {
  try {
    const res = await apiClient.post<TagResponse>(TAGS_BASE, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to create tag');
  }
}

/**
 * PUT /api/tags/:id
 */
export async function updateTag(
  id: number,
  dto: TagUpdateRequest,
): Promise<TagResponse> {
  try {
    const res = await apiClient.put<TagResponse>(`${TAGS_BASE}/${id}`, dto);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, `Failed to update tag ${id}`);
  }
}

/**
 * DELETE /api/tags/:id
 */
export async function deleteTag(id: number): Promise<void> {
  try {
    await apiClient.delete(`${TAGS_BASE}/${id}`);
  } catch (err) {
    throw normalizeApiError(err, `Failed to delete tag ${id}`);
  }
}
