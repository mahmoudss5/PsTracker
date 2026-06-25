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

          {/* Search bar — desktop */}
          <div className="relative hidden w-full max-w-md md:block mx-auto">
            <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-4">
              <Search size={16} className="text-dashboard-muted" />
            </div>
            <input
              type="text"
              placeholder="Search problems, contests…"
              className="w-full rounded-lg border border-dashboard-border bg-dashboard-panel py-2 pr-4 pl-10 text-sm focus:border-dashboard-primary focus:outline-none focus:ring-1 focus:ring-dashboard-primary transition text-dashboard-text placeholder-dashboard-muted"
            />
          </div>
        </div>

        <div className="flex items-center gap-1 sm:gap-2">
          {/* Search toggle — mobile only */}
          <button
            onClick={() => setSearchOpen((v) => !v)}
            className="icon-button md:hidden"
            aria-label="Search"
          >
            {searchOpen ? <X size={20} /> : <Search size={20} />}
          </button>

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

      {/* Collapsible mobile search bar */}
      {searchOpen && (
        <div className="border-t border-dashboard-border px-4 py-3 md:hidden">
          <div className="relative">
            <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
              <Search size={16} className="text-dashboard-muted" />
            </div>
            <input
              autoFocus
              type="text"
              placeholder="Search problems, contests…"
              className="w-full rounded-lg border border-dashboard-border bg-dashboard-panel py-2 pr-4 pl-9 text-sm focus:border-dashboard-primary focus:outline-none focus:ring-1 focus:ring-dashboard-primary transition text-dashboard-text placeholder-dashboard-muted"
            />
          </div>
        </div>
      )}
    </header>
  );
}
