import type { ReactNode } from 'react';
import { AuthLogo } from './AuthLogo';

interface AuthLayoutProps {
  children: ReactNode;
  wide?: boolean;
}

export function AuthLayout({ children, wide = false }: AuthLayoutProps) {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-surface px-4 py-12">
      <div
        className="pointer-events-none absolute inset-0 opacity-30"
        style={{
          backgroundImage: `
            linear-gradient(rgba(86, 193, 242, 0.06) 1px, transparent 1px),
            linear-gradient(90deg, rgba(86, 193, 242, 0.06) 1px, transparent 1px)
          `,
          backgroundSize: '48px 48px',
        }}
      />
      <div className="pointer-events-none absolute left-1/2 top-1/2 h-[500px] w-[90vw] max-w-[700px] -translate-x-1/2 -translate-y-1/2 rounded-full bg-accent/5 blur-3xl" />

      <div className={`relative z-10 w-full ${wide ? 'max-w-5xl' : 'max-w-md'}`}>
        <AuthLogo />
        <div className="overflow-hidden rounded-2xl border border-accent/20 bg-surface-card shadow-card">
          {children}
        </div>
      </div>
    </div>
  );
}
