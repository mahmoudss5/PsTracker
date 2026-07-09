import { Menu, Sun, Moon, User } from 'lucide-react';
import type { Theme } from '../../hooks/theme/useTheme';
import { NotificationBell } from './NotificationBell';
import { useCurrentUser } from '../../hooks/useCurrentUser';
import { useCfAvatar } from '../../hooks/useCfAvatar';

interface NavBarProps {
  onOpenMenu: () => void;
  theme: Theme;
  onToggleTheme: () => void;
}

export function NavBar({ onOpenMenu, theme, onToggleTheme }: NavBarProps) {
  const { user } = useCurrentUser();
  const userName = user?.userName || user?.codeforcesHandle;
  const cfAvatar = useCfAvatar(user?.codeforcesHandle);

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

          {/* Live notification bell */}
          <div className="hidden sm:block">
            <NotificationBell />
          </div>

          <div className="ml-1 sm:ml-2 flex items-center gap-3 border-l border-dashboard-border pl-2 sm:pl-4">
            <span className="text-sm font-bold text-dashboard-text hidden sm:block">
              {userName || 'User'}
            </span>
            {cfAvatar ? (
              <img src={cfAvatar} alt={userName || 'User'} className="h-8 w-8 rounded-full shadow-sm object-cover cursor-pointer" />
            ) : (
              <div className="flex h-8 w-8 cursor-pointer items-center justify-center rounded-full bg-dashboard-primary/20 text-dashboard-primary hover:bg-dashboard-primary/30 transition shadow-sm">
                <User size={18} />
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
