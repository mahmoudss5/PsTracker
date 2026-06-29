import { useState } from 'react';
import { Menu, Search, Sun, Moon, Bell, User, X } from 'lucide-react';
import type { Theme } from '../../hooks/theme/useTheme';

interface NavBarProps {
  onOpenMenu: () => void;
  theme: Theme;
  onToggleTheme: () => void;
}

export function NavBar({ onOpenMenu, theme, onToggleTheme }: NavBarProps) {
  const [searchOpen, setSearchOpen] = useState(false);

  return (
    <header className="sticky top-0 z-30 border-b border-dashboard-border bg-dashboard/80 backdrop-blur">
      {/* Main bar */}
      <div className="flex h-16 items-center justify-between px-4 md:px-8 lg:px-12">
        <div className="flex items-center gap-3 flex-1">
          {/* Hamburger — mobile only */}
          <button onClick={onOpenMenu} className="icon-button md:hidden" aria-label="Open menu">
            <Menu size={20} />
          </button>

          {/* Brand — desktop only (sidebar shows it on mobile) */}
          <div className="hidden md:flex items-center gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-dashboard-primary text-dashboard-primary-contrast font-bold text-sm">
              PT
            </span>
            <span className="text-lg font-bold text-dashboard-text">PsTracker</span>
          </div>

       
        </div>

        <div className="flex items-center gap-1 sm:gap-2">
        
          <button onClick={onToggleTheme} className="icon-button" title="Toggle theme">
            {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
          </button>

          <button className="icon-button hidden sm:inline-flex" title="Notifications">
            <Bell size={20} />
          </button>

          <div className="ml-1 sm:ml-2 flex items-center gap-2 border-l border-dashboard-border pl-2 sm:pl-4">
            <div className="flex h-8 w-8 cursor-pointer items-center justify-center rounded-full bg-dashboard-primary/20 text-dashboard-primary hover:bg-dashboard-primary/30 transition">
              <User size={18} />
            </div>
          </div>
        </div>
      </div>

    
    </header>
  );
}
