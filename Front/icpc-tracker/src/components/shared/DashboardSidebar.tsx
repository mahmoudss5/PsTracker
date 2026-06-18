import { Link, useLocation } from "react-router-dom";
import {
  Home,
  ListTodo,
  Trophy,
  Medal,
  Settings,
  X,
  LogOutIcon,
} from "lucide-react";

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const navItems = [
  { path: "/dashboard/trainee", label: "Dashboard", icon: Home },
  { path: "/dashboard/problems", label: "Problems", icon: ListTodo },
  { path: "/dashboard/contests", label: "Contests", icon: Trophy },
  { path: "/dashboard/leaderboard", label: "Leaderboard", icon: Medal },
  { path: "/settings", label: "Settings", icon: Settings },
];

export function DashboardSidebar({ isOpen, onClose }: SidebarProps) {
  const location = useLocation();

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50 md:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r border-dashboard-border bg-dashboard-panel transition-all duration-300 md:w-[4.5rem] md:translate-x-0 ${
          isOpen ? "translate-x-0" : "-translate-x-full"
        } hover:w-64 group`}
      >
        <div className="flex h-16 items-center justify-between px-4 md:justify-center">
          <div className="flex items-center gap-2 overflow-hidden text-dashboard-text md:w-8 md:justify-center group-hover:w-full group-hover:justify-start">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-dashboard-primary text-dashboard-primary-contrast font-bold">
              PT
            </span>
            <span className="font-bold whitespace-nowrap md:hidden md:group-hover:block transition-opacity duration-300">
              PsTracker
            </span>
          </div>
          <button onClick={onClose} className="icon-button md:hidden">
            <X size={20} />
          </button>
        </div>

        <nav className="flex-1 flex flex-col p-2 overflow-y-auto mt-4">
          <div className="flex flex-col gap-1 space-y-2">
            {navItems.map((item) => {
              const isActive = location.pathname.startsWith(item.path);
              const Icon = item.icon;

              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-3 rounded-lg px-3 py-2.5 transition-colors ${
                    isActive
                      ? "bg-dashboard-primary/10 text-dashboard-primary"
                      : "text-dashboard-muted hover:bg-dashboard-elevated hover:text-dashboard-text"
                  }`}
                  title={item.label}
                >
                  <Icon size={22} className="shrink-0" />
                  <span className="text-sm font-medium whitespace-nowrap block md:hidden md:group-hover:block transition-opacity duration-300">
                    {item.label}
                  </span>
                </Link>
              );
            })}
          </div>

          <div className="mt-auto pt-2 border-t border-dashboard-border">
            <button className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 transition-colors text-red-500 hover:bg-dashboard-elevated hover:text-red-400">
              <LogOutIcon size={22} className="shrink-0" />
              <span className="text-sm font-medium whitespace-nowrap block md:hidden md:group-hover:block transition-opacity duration-300">
                Logout
              </span>
            </button>
          </div>
        </nav>
      </aside>
    </>
  );
}
