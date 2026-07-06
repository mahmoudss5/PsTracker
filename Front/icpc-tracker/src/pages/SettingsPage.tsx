import { useCurrentUser } from '../hooks/useCurrentUser';
import { SettingsForm } from '../components/Settings/SettingsForm';

export function SettingsPage() {
  const { user, refetch } = useCurrentUser();

  return (
    <div className="max-w-5xl mx-auto space-y-8 p-4 sm:p-6 lg:p-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div className="mb-10 text-center">
        <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-dashboard-primary via-purple-500 to-pink-500 bg-clip-text text-transparent">
          Account Settings
        </h1>
        <p className="text-dashboard-muted mt-3 text-lg">Manage your profile details, handle, and security preferences.</p>
      </div>
      
      <SettingsForm user={user} onUpdated={refetch} />
    </div>
  );
}
