import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { DashboardSidebar } from '../components/shared/DashboardSidebar';
import { NavBar } from '../components/shared/NavBar';
import { useTheme } from '../hooks/theme/useTheme';

export function RootLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { theme, toggleTheme } = useTheme();

  return (
    <div className="relative min-h-screen bg-dashboard text-dashboard-text">
      <div className="dashboard-ambient" aria-hidden="true" />
      <DashboardSidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <div className="relative z-10 flex min-h-screen flex-col md:ml-[4.5rem]">
        <NavBar
          theme={theme}
          onToggleTheme={toggleTheme}
          onOpenMenu={() => setSidebarOpen(true)}
        />
        <main className="flex-1 overflow-y-auto px-4 py-6 md:px-12 md:py-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
