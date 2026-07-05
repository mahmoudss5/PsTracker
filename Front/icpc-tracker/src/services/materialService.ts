import { apiClient } from '../config/api';
import { normalizeApiError } from './ErrorService';

export interface MaterialResponseDto {
  id: number;
  kind: 'link' | 'pdf' | 'image';
  title: string;
  subtitle: string;
  size: string;
  createdAt: string;
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
