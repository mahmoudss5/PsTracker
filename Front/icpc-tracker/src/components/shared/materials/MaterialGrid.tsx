import type { MaterialResponseDto } from '../../../services/materialService';
import { MaterialCard } from './MaterialCard';

interface MaterialGridProps {
  materials: MaterialResponseDto[];
  canDelete: boolean;
  isDeleting: boolean;
  onView: (s3Key: string) => void;
  onDownload: (s3Key: string) => void;
  onDelete: (s3Key: string) => void;
}

export function MaterialGrid({
  materials,
  canDelete,
  isDeleting,
  onView,
  onDownload,
  onDelete,
}: MaterialGridProps) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
      {materials.map((material) => (
        <MaterialCard
          key={material.id}
          material={material}
          canDelete={canDelete}
          isDeleting={isDeleting}
          onView={onView}
          onDownload={onDownload}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
