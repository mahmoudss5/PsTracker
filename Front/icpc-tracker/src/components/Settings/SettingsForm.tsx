import { useState, useEffect, type FormEvent } from 'react';
import { updateProfile, updatePassword } from '../../services/userService';
import { toast } from 'sonner';
import { User, Globe, Lock, Key, ShieldAlert, ChevronDown, ChevronUp } from 'lucide-react';
import type { TraineeResponse } from '../../types/api.types';
import { useCfAvatar } from '../../hooks/useCfAvatar';

interface SettingsFormProps {
  user: TraineeResponse | null;
  onUpdated: () => void;
}

export function SettingsForm({ user, onUpdated }: SettingsFormProps) {
  const [userName, setUserName] = useState('');
  const [codeforcesHandle, setCodeforcesHandle] = useState('');
  
  const [showPasswordFields, setShowPasswordFields] = useState(false);
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      setUserName(user.userName || '');
      setCodeforcesHandle(user.codeforcesHandle || '');
    }
  }, [user]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      // 1. Update Profile if changed
      if (userName !== user?.userName || codeforcesHandle !== user?.codeforcesHandle) {
        if (!userName.trim() || !codeforcesHandle.trim()) {
            throw new Error('Username and Codeforces Handle cannot be empty.');
        }
        await updateProfile({ userName, codeforcesHandle });
        toast.success('Profile updated successfully!');
        onUpdated();
      }

      // 2. Update Password if requested
      if (showPasswordFields) {
        if (!oldPassword || !newPassword || !confirmPassword) {
          throw new Error('All password fields are required.');
        }
        if (newPassword !== confirmPassword) {
          throw new Error('New passwords do not match.');
        }
        await updatePassword({ oldPassword, newPassword });
        toast.success('Password updated successfully!');
        setOldPassword('');
        setNewPassword('');
        setConfirmPassword('');
        setShowPasswordFields(false);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to update settings';
      setError(message);
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const cfAvatar = useCfAvatar(user?.codeforcesHandle);
  const avatarUrl = cfAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user?.userName || 'User'}`;

  const roleText = user?.role ? user.role.toUpperCase() : 'USER';
  const roleBadgeColor = roleText === 'COACH' 
    ? 'bg-dashboard-primary/20 text-dashboard-primary border-dashboard-primary/30' 
    : 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30';

  return (
    <div className="relative group max-w-2xl mx-auto">
      <div className="absolute -inset-0.5 bg-gradient-to-br from-dashboard-primary/30 to-purple-500/30 rounded-3xl blur opacity-30 group-hover:opacity-50 transition duration-500"></div>
      <div className="relative p-6 sm:p-10 border border-dashboard-border/50 rounded-3xl bg-dashboard-panel/80 backdrop-blur-xl shadow-xl">
        
        {/* User Header */}
        <div className="flex flex-col items-center mb-8 pb-8 border-b border-dashboard-border/50">
           <img
            src={avatarUrl}
            alt={user?.userName || 'User'}
            className="h-32 w-32 shrink-0 rounded-full border-4 border-dashboard-border object-cover bg-dashboard-elevated shadow-lg mb-4"
            onError={(e) => {
              (e.target as HTMLImageElement).src = `https://api.dicebear.com/7.x/avataaars/svg?seed=${user?.userName || 'User'}`;
            }}
          />
          <h2 className="text-2xl font-bold text-dashboard-text tracking-tight">{user?.userName || 'User'}</h2>
          <span className={`mt-2 px-3 py-1 rounded-full text-xs font-bold tracking-wider border ${roleBadgeColor}`}>
            {roleText}
          </span>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {error && <div className="text-red-400 text-sm bg-red-500/10 p-4 rounded-xl border border-red-500/20 animate-in fade-in slide-in-from-top-2">{error}</div>}
          
          <div className="space-y-3">
            <label className="text-sm font-semibold text-dashboard-text/90 flex items-center gap-2">
               Username
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <User size={18} className="text-dashboard-primary" />
              </div>
              <input
                type="text"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                className="w-full bg-white/30 border border-dashboard-border rounded-xl pl-11 pr-4 py-3.5 text-black placeholder-dashboard-muted/50 focus:outline-none focus:border-dashboard-primary focus:ring-2 focus:ring-dashboard-primary/20 transition-all shadow-inner"
                placeholder="Enter username"
                required
                minLength={6}
              />
            </div>
          </div>

          <div className="space-y-3">
            <label className="text-sm font-semibold text-dashboard-text/90 flex items-center gap-2">
               Codeforces Handle
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Globe size={18} className="text-purple-500" />
              </div>
              <input
                type="text"
                value={codeforcesHandle}
                onChange={(e) => setCodeforcesHandle(e.target.value)}
                className="w-full bg-white/30 border border-dashboard-border rounded-xl pl-11 pr-4 py-3.5 text-black placeholder-dashboard-muted/50 focus:outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-500/20 transition-all shadow-inner"
                placeholder="Enter Codeforces handle"
                required
              />
            </div>
          </div>

          <div className="pt-4">
             <button
               type="button"
               onClick={() => setShowPasswordFields(!showPasswordFields)}
               className="flex items-center gap-2 text-sm font-medium text-dashboard-muted hover:text-dashboard-text transition-colors"
             >
               {showPasswordFields ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
               {showPasswordFields ? 'Cancel Password Change' : 'Change Password'}
             </button>
          </div>

          {showPasswordFields && (
            <div className="space-y-6 pt-4 border-t border-dashboard-border/50 animate-in fade-in slide-in-from-top-4 duration-300">
               <div className="space-y-3">
                <label className="text-sm font-semibold text-dashboard-text/90 flex items-center gap-2">
                   Old Password
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <Lock size={18} className="text-dashboard-muted" />
                  </div>
                  <input
                    type="password"
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                    className="w-full bg-white/30 border border-dashboard-border rounded-xl pl-11 pr-4 py-3.5 text-black placeholder-dashboard-muted/50 focus:outline-none focus:border-red-500 focus:ring-2 focus:ring-red-500/20 transition-all shadow-inner"
                    placeholder="Enter your current password"
                    required={showPasswordFields}
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div className="space-y-3">
                  <label className="text-sm font-semibold text-dashboard-text/90 flex items-center gap-2">
                     New Password
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Key size={18} className="text-dashboard-primary" />
                    </div>
                    <input
                      type="password"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      className="w-full bg-white/30 border border-dashboard-border rounded-xl pl-11 pr-4 py-3.5 text-black placeholder-dashboard-muted/50 focus:outline-none focus:border-dashboard-primary focus:ring-2 focus:ring-dashboard-primary/20 transition-all shadow-inner"
                      placeholder="New password"
                      required={showPasswordFields}
                      minLength={6}
                    />
                  </div>
                </div>

                <div className="space-y-3">
                  <label className="text-sm font-semibold text-dashboard-text/90 flex items-center gap-2">
                     Confirm Password
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <ShieldAlert size={18} className="text-dashboard-primary opacity-50" />
                    </div>
                    <input
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="w-full bg-white/30 border border-dashboard-border rounded-xl pl-11 pr-4 py-3.5 text-black placeholder-dashboard-muted/50 focus:outline-none focus:border-dashboard-primary focus:ring-2 focus:ring-dashboard-primary/20 transition-all shadow-inner"
                      placeholder="Confirm new password"
                      required={showPasswordFields}
                      minLength={6}
                    />
                  </div>
                </div>
              </div>
            </div>
          )}

          <div className="pt-8 mt-4 border-t border-dashboard-border/50 flex justify-end">
            <button
              type="submit"
              disabled={isSubmitting || (userName.trim().length < 6 || !codeforcesHandle.trim())}
              className="w-full sm:w-auto px-10 py-4 bg-gradient-to-r from-dashboard-primary to-purple-600 hover:from-dashboard-primary-hover hover:to-purple-500 text-white font-bold rounded-xl transition-all shadow-lg shadow-dashboard-primary/25 disabled:opacity-50 disabled:cursor-not-allowed hover:-translate-y-0.5 active:translate-y-0"
            >
              {isSubmitting ? 'Saving Changes...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
