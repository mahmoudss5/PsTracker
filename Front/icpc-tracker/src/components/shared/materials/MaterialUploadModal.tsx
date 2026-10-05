import { Loader2, Upload, X } from 'lucide-react';
import { useEffect, type ChangeEvent } from 'react';
import { useMaterialUploadForm } from '../../../hooks/materials/useMaterialUploadForm';
import type { MaterialUploadValues } from '../../../hooks/materials/useMaterialActions';

interface MaterialUploadModalProps {
  isOpen: boolean;
  isUploading: boolean;
  error: string | null;
  onClose: () => void;
  onSubmit: (values: MaterialUploadValues) => Promise<void>;
}

export function MaterialUploadModal({
  isOpen,
  isUploading,
  error,
  onClose,
  onSubmit,
}: MaterialUploadModalProps) {
  const form = useMaterialUploadForm(onSubmit);
  const { reset } = form;

  useEffect(() => {
    if (!isOpen) reset();
  }, [isOpen, reset]);

  if (!isOpen) return null;

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    form.selectFile(event);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="material-upload-title"
        className="w-full max-w-lg rounded-xl border border-dashboard-border bg-dashboard-panel p-6 shadow-2xl"
      >
        <div className="mb-5 flex items-center justify-between">
          <div>
            <h2 id="material-upload-title" className="text-lg font-bold text-dashboard-text">Upload material</h2>
            <p className="mt-1 text-xs text-dashboard-muted">Upload a file directly to the team storage.</p>
          </div>
          <button type="button" onClick={onClose} className="text-dashboard-muted transition hover:text-dashboard-text" aria-label="Close upload dialog">
            <X size={20} />
          </button>
        </div>

        <form className="space-y-4" onSubmit={form.submit}>
          <label className="block">
            <span className="mb-2 block text-xs font-semibold uppercase tracking-wider text-dashboard-muted">File</span>
            <input
              type="file"
              required
              onChange={handleFileChange}
              className="block w-full cursor-pointer rounded-lg border border-dashboard-border bg-dashboard-elevated px-3 py-2 text-sm text-dashboard-text file:mr-3 file:rounded-md file:border-0 file:bg-dashboard-primary file:px-3 file:py-2 file:text-xs file:font-semibold file:text-dashboard-primary-contrast"
            />
          </label>

          <label className="block">
            <span className="mb-2 block text-xs font-semibold uppercase tracking-wider text-dashboard-muted">Title</span>
            <input
              value={form.title}
              onChange={(event) => form.setTitle(event.target.value)}
              placeholder="e.g. Dynamic Programming Notes"
              className="w-full rounded-lg border border-dashboard-border bg-dashboard-elevated px-3 py-2 text-sm text-dashboard-text outline-none transition focus:border-dashboard-primary"
              required
            />
          </label>

          <label className="block">
            <span className="mb-2 block text-xs font-semibold uppercase tracking-wider text-dashboard-muted">Subtitle <span className="normal-case tracking-normal">(optional)</span></span>
            <input
              value={form.subtitle}
              onChange={(event) => form.setSubtitle(event.target.value)}
              placeholder="Short description"
              className="w-full rounded-lg border border-dashboard-border bg-dashboard-elevated px-3 py-2 text-sm text-dashboard-text outline-none transition focus:border-dashboard-primary"
            />
          </label>

          {error && <p className="text-sm text-red-400">{error}</p>}

          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="rounded-lg border border-dashboard-border px-4 py-2 text-sm font-semibold text-dashboard-muted transition hover:text-dashboard-text">
              Cancel
            </button>
            <button
              type="submit"
              disabled={!form.canSubmit || isUploading}
              className="inline-flex items-center gap-2 rounded-lg bg-dashboard-primary px-4 py-2 text-sm font-semibold text-dashboard-primary-contrast transition hover:bg-dashboard-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isUploading ? <Loader2 size={16} className="animate-spin" /> : <Upload size={16} />}
              {isUploading ? 'Uploading…' : 'Upload material'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
