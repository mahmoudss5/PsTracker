import type { InputHTMLAttributes } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  labelAction?: React.ReactNode;
}

export function Input({ label, labelAction, id, className = '', ...props }: InputProps) {
  const inputId = id ?? label.toLowerCase().replace(/\s+/g, '-');

  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-center justify-between">
        <label
          htmlFor={inputId}
          className="text-xs font-medium uppercase tracking-wider text-muted"
        >
          {label}
        </label>
        {labelAction}
      </div>
      <input
        id={inputId}
        className={[
          'w-full rounded-lg border border-surface-border bg-surface-input px-4 py-3',
          'text-sm text-white placeholder:text-muted/60',
          'outline-none transition-colors focus:border-accent/60 focus:ring-1 focus:ring-accent/30',
          className,
        ]
          .filter(Boolean)
          .join(' ')}
        {...props}
      />
    </div>
  );
}
