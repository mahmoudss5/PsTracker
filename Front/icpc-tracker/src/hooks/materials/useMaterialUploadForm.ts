import { useCallback, useState, type ChangeEvent, type FormEvent } from 'react';
import type { MaterialUploadValues } from './useMaterialActions';

interface UseMaterialUploadFormResult {
  file: File | null;
  title: string;
  subtitle: string;
  canSubmit: boolean;
  setTitle: (title: string) => void;
  setSubtitle: (subtitle: string) => void;
  selectFile: (event: ChangeEvent<HTMLInputElement>) => void;
  submit: (event: FormEvent<HTMLFormElement>) => Promise<void>;
  reset: () => void;
}

export function useMaterialUploadForm(
  onSubmit: (values: MaterialUploadValues) => Promise<void>,
): UseMaterialUploadFormResult {
  const [file, setFile] = useState<File | null>(null);
  const [title, setTitle] = useState('');
  const [subtitle, setSubtitle] = useState('');

  const selectFile = useCallback((event: ChangeEvent<HTMLInputElement>) => {
    const nextFile = event.target.files?.[0] ?? null;
    setFile(nextFile);

    if (nextFile && !title.trim()) {
      setTitle(removeExtension(nextFile.name));
    }
  }, [title]);

  const reset = useCallback(() => {
    setFile(null);
    setTitle('');
    setSubtitle('');
  }, []);

  const submit = useCallback(async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file || !title.trim()) return;

    await onSubmit({
      file,
      title: title.trim(),
      subtitle: subtitle.trim(),
    });
  }, [file, onSubmit, subtitle, title]);

  return {
    file,
    title,
    subtitle,
    canSubmit: Boolean(file && title.trim()),
    setTitle,
    setSubtitle,
    selectFile,
    submit,
    reset,
  };
}

function removeExtension(fileName: string): string {
  return fileName.replace(/\.[^/.]+$/, '');
}
