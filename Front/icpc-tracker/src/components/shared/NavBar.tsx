import { Menu, Search, Sun, Moon, Bell, User } from 'lucide-react';
import type { Theme } from '../../hooks/theme/useTheme';

interface NavBarProps {
  onOpenMenu: () => void;
  theme: Theme;
  onToggleTheme: () => void;
}

export function NavBar({ onOpenMenu, theme, onToggleTheme }: NavBarProps) {
  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-dashboard-border bg-dashboard/80 px-4 py-3 backdrop-blur md:px-8 lg:px-12">
      <div className="flex items-center gap-4 flex-1">
        <button onClick={onOpenMenu} className="icon-button md:hidden">
          <Menu size={20} />
        </button>
         
         <div>
          <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-dashboard-primary text-dashboard-primary-contrast font-bold">
            PT
          </span>
         </div>
        <div className="hidden text-lg font-bold text-dashboard-text md:block">
          PsTracker
           </div>
        <div className="relative hidden w-full max-w-md md:block mx-auto">
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-6">
            <Search size={18} className="text-dashboard-muted" />
          </div>
          <input
            type="text"
            placeholder="Search problems, contests..."
            className="w-full rounded-lg border border-dashboard-border bg-dashboard-panel mx-4 py-2 pr-4 pl-14 text-sm focus:border-dashboard-primary focus:outline-none focus:ring-1 focus:ring-dashboard-primary transition text-dashboard-text placeholder-dashboard-muted"
          />
        </div>
      </div>

      <div className="flex items-center gap-1 sm:gap-2 mr-0 sm:mr-2 lg:mr-4">
        <button onClick={onToggleTheme} className="icon-button" title="Toggle theme">
          {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
        </button>
        <button className="icon-button hidden sm:inline-flex" title="Notifications">
          <Bell size={20} />
        </button>
        <div className="ml-1 sm:ml-2 flex items-center gap-2 border-l border-dashboard-border pl-3 sm:pl-4">
          <div className="flex h-8 w-8 cursor-pointer items-center justify-center rounded-full bg-dashboard-primary/20 text-dashboard-primary hover:bg-dashboard-primary/30 transition">
            <User size={18} />
          </div>
        </div>
      </div>

    </header>
  );
}
