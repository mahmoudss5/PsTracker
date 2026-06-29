import { useState } from 'react';
import { UserPlus, X, Copy, Check } from 'lucide-react';
import { createTeam } from '../../services/teamService';

interface CoachOverviewProps {
  totalTrainees: number;
  activeTeams: number;
  avgSolveRate: number;
}

export function CoachOverview({
  totalTrainees,
  activeTeams,
  avgSolveRate,
}: CoachOverviewProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [createdCode, setCreatedCode] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  const handleOpenModal = () => {
    setIsModalOpen(true);
    setTeamName('');
    setError(null);
    setCreatedCode(null);
    setCopied(false);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleCreateTeam = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!teamName.trim()) return;

    setIsSubmitting(true);
    setError(null);
    try {
      const response = await createTeam({ teamName: teamName.trim() });
      setCreatedCode(response.teamCode);
    } catch (err: any) {
      setError(err.message || 'Failed to create team. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCopyCode = () => {
    if (createdCode) {
      navigator.clipboard.writeText(createdCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <>
      <div className="space-y-4">
        {/* Top Header / Stats Row */}
        <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-4 border-b border-dashboard-border pb-4">
          <div>
            <h2 className="text-2xl font-extrabold text-dashboard-text tracking-tight">Coach Overview</h2>
            <p className="text-sm text-dashboard-muted mt-1">Real-time performance metrics across all tiers.</p>
          </div>

          <div className="flex flex-wrap items-center gap-6 md:gap-12 text-sm font-semibold">
            <div className="flex flex-col">
              <span className="text-[11px] uppercase tracking-wider text-dashboard-muted">Total Trainees</span>
              <span className="text-xl text-blue-400 font-bold">{totalTrainees}</span>
            </div>
            <div className="flex flex-col">
              <span className="text-[11px] uppercase tracking-wider text-dashboard-muted">Active Teams</span>
              <span className="text-xl text-emerald-400 font-bold">{activeTeams < 10 ? `0${activeTeams}` : activeTeams}</span>
            </div>
            <div className="flex flex-col">
              <span className="text-[11px] uppercase tracking-wider text-dashboard-muted">Avg. Solve Rate</span>
              <span className="text-xl text-orange-400 font-bold">{avgSolveRate.toFixed(1)}%</span>
            </div>
          </div>
        </div>

        {/* Expand Roster Banner */}
        <div className="glass-panel p-6 flex flex-col md:flex-row items-center justify-between gap-6 border-dashboard-primary/20 bg-dashboard-primary/5">
          <div className="flex-1">
            <h3 className="text-lg font-bold text-dashboard-text mb-1">Expand Your Coaching Roster</h3>
            <p className="text-dashboard-muted text-sm max-w-xl">
              Initialize a new team environment with custom problem sets, private leaderboards, and automated performance tracking for your trainees.
            </p>
          </div>
          
          <button 
            onClick={handleOpenModal}
            className="flex items-center gap-2 px-5 py-2.5 bg-dashboard-primary hover:bg-dashboard-primary/90 text-dashboard-primary-contrast font-bold rounded-lg shadow-lg shadow-dashboard-primary/20 transition-all shrink-0"
          >
            <UserPlus size={18} />
            Create New Team
          </button>
        </div>
      </div>

      {/* Create Team Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="glass-panel w-full max-w-md p-6 bg-dashboard-panel shadow-2xl rounded-xl border border-dashboard-border relative">
            <button 
              onClick={handleCloseModal}
              className="absolute top-4 right-4 text-dashboard-muted hover:text-dashboard-text transition-colors"
            >
              <X size={20} />
            </button>
            
            {!createdCode ? (
              <>
                <h3 className="text-xl font-bold text-dashboard-text mb-2">Create New Team</h3>
                <p className="text-sm text-dashboard-muted mb-6">Enter a name for your new team to generate an invite code.</p>
                
                <form onSubmit={handleCreateTeam} className="space-y-4">
                  <div>
                    <label className="block text-xs font-bold text-dashboard-muted uppercase tracking-wider mb-1.5">
                      Team Name
                    </label>
                    <input
                      type="text"
                      value={teamName}
                      onChange={(e) => setTeamName(e.target.value)}
                      placeholder="e.g. Alpha Squad 2024"
                      className="w-full bg-dashboard-background border border-dashboard-border rounded-lg px-4 py-2.5 text-dashboard-text focus:outline-none focus:border-dashboard-primary focus:ring-1 focus:ring-dashboard-primary transition-all"
                      required
                      autoFocus
                      disabled={isSubmitting}
                    />
                  </div>
                  
                  {error && (
                    <div className="text-sm text-red-400 bg-red-400/10 border border-red-400/20 rounded-lg p-3">
                      {error}
                    </div>
                  )}

                  <button
                    type="submit"
                    disabled={isSubmitting || !teamName.trim()}
                    className="w-full py-2.5 bg-dashboard-primary hover:bg-dashboard-primary/90 text-dashboard-primary-contrast font-bold rounded-lg shadow-lg shadow-dashboard-primary/20 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex justify-center items-center h-11"
                  >
                    {isSubmitting ? 'Creating...' : 'Create Team'}
                  </button>
                </form>
              </>
            ) : (
              <div className="text-center py-4">
                <div className="w-16 h-16 bg-emerald-400/20 text-emerald-400 rounded-full flex items-center justify-center mx-auto mb-4 border border-emerald-400/30">
                  <Check size={32} />
                </div>
                <h3 className="text-xl font-bold text-dashboard-text mb-2">Team Created Successfully!</h3>
                <p className="text-sm text-dashboard-muted mb-6">Share this code with your trainees so they can join.</p>
                
                <div className="bg-dashboard-background border border-dashboard-border rounded-lg p-4 flex items-center justify-between mb-6 group">
                  <span className="font-mono text-2xl font-bold text-dashboard-text tracking-widest">{createdCode}</span>
                  <button 
                    onClick={handleCopyCode}
                    className="p-2 text-dashboard-muted hover:text-dashboard-primary bg-dashboard-panel rounded-md transition-colors border border-dashboard-border hover:border-dashboard-primary/50"
                    title="Copy code"
                  >
                    {copied ? <Check size={18} className="text-emerald-400" /> : <Copy size={18} />}
                  </button>
                </div>
                
                <button
                  onClick={handleCloseModal}
                  className="w-full py-2.5 bg-dashboard-panel hover:bg-dashboard-elevated border border-dashboard-border text-dashboard-text font-bold rounded-lg transition-all"
                >
                  Done
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
}
