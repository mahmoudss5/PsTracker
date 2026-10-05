import { Upload } from 'lucide-react';
import { useMaterialsPanel } from '../../hooks/materials/useMaterialsPanel';
import { MaterialGrid } from './materials/MaterialGrid';
import {
  MaterialEmptyState,
  MaterialErrorState,
  MaterialLoadingState,
} from './materials/MaterialPanelStates';
import { MaterialUploadModal } from './materials/MaterialUploadModal';

interface MaterialsPanelProps {
  teamId?: number;
  canUpload?: boolean;
  uploadOpen?: boolean;
  onUploadOpenChange?: (open: boolean) => void;
}

export function MaterialsPanel({
  teamId,
  canUpload = false,
  uploadOpen,
  onUploadOpenChange,
}: MaterialsPanelProps) {
  const {
    materials,
    isLoading,
    error,
    refetch,
    isUploading,
    isDeleting,
    error: actionError,
    isUploadDialogOpen,
    handleUpload,
    handleView,
    handleDownload,
    handleDelete,
    openUploadDialog,
    closeUploadDialog,
  } = useMaterialsPanel({ teamId, uploadOpen, onUploadOpenChange });

  return (
    <div className="h-full overflow-y-auto px-4 py-4 md:px-8">
      <div className="mb-4 flex items-center justify-between gap-4">
        <p className="text-xs text-dashboard-muted">
          {materials.length} resource{materials.length === 1 ? '' : 's'} uploaded by your coach
        </p>
        {canUpload && (
          <button
            type="button"
            onClick={openUploadDialog}
            className="inline-flex items-center gap-2 rounded-lg bg-dashboard-primary px-3 py-2 text-xs font-semibold text-dashboard-primary-contrast transition hover:bg-dashboard-primary/90"
          >
            <Upload size={14} />
            Upload material
          </button>
        )}
      </div>

      {isLoading && <MaterialLoadingState />}
      {!isLoading && error && <MaterialErrorState message={error} onRetry={refetch} />}
      {!isLoading && !error && materials.length === 0 && <MaterialEmptyState />}

      {!isLoading && !error && materials.length > 0 && (
        <MaterialGrid
          materials={materials}
          canDelete={canUpload}
          isDeleting={isDeleting}
          onView={(s3Key) => void handleView(s3Key)}
          onDownload={(s3Key) => void handleDownload(s3Key)}
          onDelete={(s3Key) => void handleDelete(s3Key)}
        />
      )}

      <MaterialUploadModal
        isOpen={isUploadDialogOpen}
        isUploading={isUploading}
        error={actionError}
        onClose={closeUploadDialog}
        onSubmit={handleUpload}
      />
    </div>
  );
}
