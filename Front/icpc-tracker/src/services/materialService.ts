import { apiClient } from '../config/api';
import { normalizeApiError } from './ErrorService';

export interface MaterialResponseDto {
  id: number;
  fileType: string;
  title: string;
  subtitle: string;
  fileSize: string;
  s3Key: string;
  createdAt: string;
}

export interface MaterialCreateRequest {
  fileType: string;
  title: string;
  subtitle: string;
  fileSize: string;
  s3Key: string;
}

const BASE = '/materials';

export async function getTeamMaterials(teamId: number): Promise<MaterialResponseDto[]> {
  try {
    const res = await apiClient.get<MaterialResponseDto[]>(`${BASE}/team/${teamId}`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to fetch team materials');
  }
}

export async function createMaterial(
  teamId: number,
  material: MaterialCreateRequest,
): Promise<MaterialResponseDto> {
  try {
    const res = await apiClient.post<MaterialResponseDto>(`${BASE}/team/${teamId}`, material);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to save material metadata');
  }
}
