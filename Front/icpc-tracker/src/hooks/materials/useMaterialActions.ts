import { useCallback, useState } from 'react';
import {
  createMaterial,
  type MaterialCreateRequest,
} from '../../services/materialService';
import {
  deleteStoredFile,
  getFileDownloadUrl,
  getFileViewUrl,
  requestUploadUrl,
  uploadToStorage,
} from '../../services/storageService';

export interface MaterialUploadValues {
  file: File;
  title: string;
  subtitle: string;
}

interface UseMaterialActionsResult {
  isUploading: boolean;
  isDeleting: boolean;
  error: string | null;
  uploadMaterial: (values: MaterialUploadValues) => Promise<void>;
  viewMaterial: (s3Key: string) => Promise<void>;
  downloadMaterial: (s3Key: string) => Promise<void>;
  deleteMaterial: (s3Key: string) => Promise<void>;
  clearError: () => void;
}

export function useMaterialActions(teamId?: number): UseMaterialActionsResult {
  const [isUploading, setIsUploading] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const uploadMaterial = useCallback(async (values: MaterialUploadValues) => {
    if (!teamId) {
      throw new Error('A team is required to upload a material');
    }

    setIsUploading(true);
    setError(null);

    try {
      const fileType = values.file.type || 'application/octet-stream';
      const upload = await requestUploadUrl({
        teamId,
        fileName: values.file.name,
        fileType,
        fileSize: String(values.file.size),
      });

      await uploadToStorage(upload.uploadUrl, values.file, fileType);

      const material: MaterialCreateRequest = {
        fileType,
        title: values.title.trim(),
        subtitle: values.subtitle.trim(),
        fileSize: formatFileSize(values.file.size),
        s3Key: upload.s3Key,
      };

      await createMaterial(teamId, material);
    } catch (cause) {
      const message = cause instanceof Error ? cause.message : 'Failed to upload material';
      setError(message);
      throw cause;
    } finally {
      setIsUploading(false);
    }
  }, [teamId]);

  const viewMaterial = useCallback(async (s3Key: string) => {
    const previewWindow = window.open('about:blank', '_blank');

    try {
      const url = await getFileViewUrl(s3Key);
      if (previewWindow) {
        previewWindow.location.href = url;
      } else {
        window.location.assign(url);
      }
    } catch (cause) {
      previewWindow?.close();
      throw cause;
    }
  }, []);

  const downloadMaterial = useCallback(async (s3Key: string) => {
    const url = await getFileDownloadUrl(s3Key);
    const link = document.createElement('a');
    link.href = url;
    link.download = '';
    link.rel = 'noreferrer';
    document.body.appendChild(link);
    link.click();
    link.remove();
  }, []);

  const deleteMaterial = useCallback(async (s3Key: string) => {
    setIsDeleting(true);
    setError(null);
    try {
      await deleteStoredFile(s3Key);
    } catch (cause) {
      const message = cause instanceof Error ? cause.message : 'Failed to delete material';
      setError(message);
      throw cause;
    } finally {
      setIsDeleting(false);
    }
  }, []);

  const clearError = useCallback(() => setError(null), []);

  return {
    isUploading,
    isDeleting,
    error,
    uploadMaterial,
    viewMaterial,
    downloadMaterial,
    deleteMaterial,
    clearError,
  };
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';

  const units = ['Bytes', 'KB', 'MB', 'GB'];
  const unitIndex = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1);
  const value = bytes / 1024 ** unitIndex;
  return `${Number(value.toFixed(1))} ${units[unitIndex]}`;
}
