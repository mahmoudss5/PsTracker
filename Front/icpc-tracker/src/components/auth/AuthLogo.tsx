import { BarChart3 } from 'lucide-react';

export function AuthLogo() {
  return (
    <div className="mb-10 flex items-center justify-center gap-3">
      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-accent/20 text-accent">
        <BarChart3 size={20} />
      </div>
      <span className="text-2xl font-semibold tracking-tight text-white">AlgoTrack</span>
    </div>
  );
}
