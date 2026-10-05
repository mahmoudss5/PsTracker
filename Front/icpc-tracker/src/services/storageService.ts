import { apiClient } from '../config/api';
import { normalizeApiError } from './ErrorService';

export interface StorageUploadRequest {
  teamId: number;
  fileName: string;
  fileType: string;
  fileSize: string;
}

export interface StorageUploadResponse {
  uploadUrl: string;
  s3Key: string;
}

interface StorageUrlResponse {
  viewUrl?: string;
  downloadUrl?: string;
}

const BASE = '/storage';

export async function requestUploadUrl(
  request: StorageUploadRequest,
): Promise<StorageUploadResponse> {
  try {
    const response = await apiClient.post<StorageUploadResponse>(`${BASE}/upload-url`, null, {
      params: request,
    });
    return response.data;
  } catch (err) {
    throw normalizeApiError(err, 'Failed to prepare file upload');
  }
}

export async function uploadToStorage(
  uploadUrl: string,
  file: File,
  fileType: string,
): Promise<void> {
  const response = await fetch(uploadUrl, {
    method: 'PUT',
    headers: { 'Content-Type': fileType },
    body: file,
  });

  if (!response.ok) {
    throw new Error('Failed to upload file to storage');
  }
}

export async function getFileViewUrl(s3Key: string): Promise<string> {
  try {
    const response = await apiClient.get<StorageUrlResponse>(`${BASE}/view-url`, {
      params: { s3Key },
    });
    return requireUrl(response.data.viewUrl, 'view');
  } catch (err) {
    throw normalizeApiError(err, 'Failed to prepare file preview');
  }
}

export async function getFileDownloadUrl(s3Key: string): Promise<string> {
  try {
    const response = await apiClient.get<StorageUrlResponse>(`${BASE}/download-url`, {
      params: { s3Key },
    });
    return requireUrl(response.data.downloadUrl, 'download');
  } catch (err) {
    throw normalizeApiError(err, 'Failed to prepare file download');
  }
}

export async function deleteStoredFile(s3Key: string): Promise<void> {
  try {
    await apiClient.delete(BASE, { params: { s3Key } });
  } catch (err) {
    throw normalizeApiError(err, 'Failed to delete material');
  }
}

function requireUrl(url: string | undefined, action: string): string {
  if (!url) {
    throw new Error(`The storage service did not return a ${action} URL`);
  }
  return url;
}
