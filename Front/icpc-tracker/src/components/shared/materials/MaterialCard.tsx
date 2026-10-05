import { Download, Eye, File, FileText, ImageIcon, Link2, Loader2, Trash2 } from 'lucide-react';
import type { ReactNode } from 'react';
import type { MaterialResponseDto } from '../../../services/materialService';

interface MaterialCardProps {
  material: MaterialResponseDto;
  canDelete: boolean;
  isDeleting: boolean;
  onView: (s3Key: string) => void;
  onDownload: (s3Key: string) => void;
  onDelete: (s3Key: string) => void;
}

export function MaterialCard({
  material,
  canDelete,
  isDeleting,
  onView,
  onDownload,
  onDelete,
}: MaterialCardProps) {
  const presentation = getMaterialPresentation(material.fileType);

  return (
    <article className="glass-panel group flex items-start gap-4 p-4 transition-all duration-200 hover:border-dashboard-primary/30">
      <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg border ${presentation.background}`}>
        {presentation.icon}
      </div>

      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-bold text-dashboard-text transition-colors group-hover:text-dashboard-primary">
          {material.title}
        </p>
        <p className="mt-0.5 truncate text-xs text-dashboard-muted">
          {material.subtitle || material.fileSize || 'Uploaded material'}
        </p>
      </div>

      <div className="flex shrink-0 items-center gap-1">
        <MaterialActionButton label="View material" onClick={() => onView(material.s3Key)}>
          <Eye size={16} />
        </MaterialActionButton>
        <MaterialActionButton label="Download material" onClick={() => onDownload(material.s3Key)}>
          <Download size={16} />
        </MaterialActionButton>
        {canDelete && (
          <MaterialActionButton
            label="Delete material"
            disabled={isDeleting}
            onClick={() => onDelete(material.s3Key)}
          >
            {isDeleting ? <Loader2 size={16} className="animate-spin" /> : <Trash2 size={16} />}
          </MaterialActionButton>
        )}
      </div>
    </article>
  );
}

function MaterialActionButton({
  label,
  disabled = false,
  onClick,
  children,
}: {
  label: string;
  disabled?: boolean;
  onClick: () => void;
  children: ReactNode;
}) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      disabled={disabled}
      onClick={onClick}
      className="rounded-md p-1.5 text-dashboard-muted transition hover:bg-dashboard-elevated hover:text-dashboard-text disabled:cursor-not-allowed disabled:opacity-50"
    >
      {children}
    </button>
  );
}

function getMaterialPresentation(fileType: string) {
  const normalizedType = fileType.toLowerCase();

  if (normalizedType.includes('pdf')) {
    return {
      icon: <FileText size={18} className="text-red-400" />,
      background: 'bg-red-500/10 border-red-500/20',
    };
  }

  if (normalizedType.startsWith('image/') || normalizedType === 'image') {
    return {
      icon: <ImageIcon size={18} className="text-emerald-400" />,
      background: 'bg-emerald-500/10 border-emerald-500/20',
    };
  }

  if (normalizedType === 'link' || normalizedType === 'text/uri-list') {
    return {
      icon: <Link2 size={18} className="text-dashboard-primary" />,
      background: 'bg-dashboard-primary/10 border-dashboard-primary/20',
    };
  }

  return {
    icon: <File size={18} className="text-dashboard-primary" />,
    background: 'bg-dashboard-primary/10 border-dashboard-primary/20',
  };
}
