import type { ButtonHTMLAttributes, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'secondary' | 'disabled';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  children: ReactNode;
  fullWidth?: boolean;
}

const variantStyles: Record<ButtonVariant, string> = {
  primary:
    'bg-accent text-surface font-semibold hover:bg-accent-hover disabled:opacity-60',
  secondary:
    'bg-transparent border border-surface-border text-white hover:border-accent/50 hover:bg-white/5',
  disabled: 'bg-surface-border/60 text-muted cursor-not-allowed',
};

export function Button({
  variant = 'primary',
  children,
  fullWidth = false,
  className = '',
  disabled,
  type = 'button',
  ...props
}: ButtonProps) {
  const resolvedVariant = disabled && variant !== 'secondary' ? 'disabled' : variant;

  return (
    <button
      type={type}
      disabled={disabled || resolvedVariant === 'disabled'}
      className={[
        'inline-flex items-center justify-center gap-2 rounded-lg px-4 py-3 text-sm transition-colors',
        fullWidth ? 'w-full' : '',
        variantStyles[resolvedVariant],
        className,
      ]
        .filter(Boolean)
        .join(' ')}
      {...props}
    >
      {children}
    </button>
  );
}
