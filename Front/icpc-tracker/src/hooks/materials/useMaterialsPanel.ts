import { useState } from 'react';
import { toast } from 'sonner';
import { useMaterialActions, type MaterialUploadValues } from './useMaterialActions';
import { useMaterials } from '../useMaterials';

interface UseMaterialsPanelOptions {
  teamId?: number;
  uploadOpen?: boolean;
  onUploadOpenChange?: (open: boolean) => void;
}

export function useMaterialsPanel({
  teamId,
  uploadOpen,
  onUploadOpenChange,
}: UseMaterialsPanelOptions) {
  const [localUploadOpen, setLocalUploadOpen] = useState(false);
  const materialsState = useMaterials(teamId);
  const actions = useMaterialActions(teamId);
  const setUploadDialogOpen = onUploadOpenChange ?? setLocalUploadOpen;

  const handleUpload = async (values: MaterialUploadValues) => {
    try {
      await actions.uploadMaterial(values);
      setUploadDialogOpen(false);
      toast.success('Material uploaded successfully');
      materialsState.refetch();
    } catch (cause) {
      toast.error(cause instanceof Error ? cause.message : 'Failed to upload material');
    }
  };

  const handleView = async (s3Key: string) => {
    try {
      await actions.viewMaterial(s3Key);
    } catch (cause) {
      toast.error(cause instanceof Error ? cause.message : 'Failed to open material');
    }
  };

  const handleDownload = async (s3Key: string) => {
    try {
      await actions.downloadMaterial(s3Key);
    } catch (cause) {
      toast.error(cause instanceof Error ? cause.message : 'Failed to download material');
    }
  };

  const handleDelete = async (s3Key: string) => {
    if (!window.confirm('Delete this material?')) return;

    try {
      await actions.deleteMaterial(s3Key);
      toast.success('Material deleted');
      materialsState.refetch();
    } catch (cause) {
      toast.error(cause instanceof Error ? cause.message : 'Failed to delete material');
    }
  };

  const openUploadDialog = () => {
    actions.clearError();
    setUploadDialogOpen(true);
  };

  const closeUploadDialog = () => {
    actions.clearError();
    setUploadDialogOpen(false);
  };

  return {
    ...materialsState,
    ...actions,
    isUploadDialogOpen: uploadOpen ?? localUploadOpen,
    handleUpload,
    handleView,
    handleDownload,
    handleDelete,
    openUploadDialog,
    closeUploadDialog,
  };
}
