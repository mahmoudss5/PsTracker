import type { ActiveTeam } from '../../data/mockCoach';
import { Link } from 'react-router-dom';

interface ActiveTeamsListProps {
  teams: ActiveTeam[];
}

export function ActiveTeamsList({ teams }: ActiveTeamsListProps) {
  return (
    <div className="glass-panel flex flex-col h-full">
      <div className="p-5 border-b border-dashboard-border/50">
        <h3 className="text-base font-bold text-dashboard-text">Active Teams</h3>
      </div>
      
      <div className="flex-1 p-5 space-y-4">
        {teams.map((team) => (
          <div key={team.id} className="bg-dashboard-background/50 rounded-lg p-4 border border-dashboard-border/50 flex flex-col gap-3 hover:border-dashboard-primary/30 transition-colors">
            <div className="flex justify-between items-start">
              <h4 className="font-bold text-dashboard-text text-sm">{team.name}</h4>
              <div className="bg-dashboard-panel px-2 py-1 rounded text-[10px] font-bold text-dashboard-primary tracking-wider border border-dashboard-border/50">
                {team.code}
              </div>
            </div>
            
            <div className="flex justify-between items-end mt-1">
              {/* Overlapping Avatars */}
              <div className="flex -space-x-2">
                {team.members.map((member, i) => (
                  <div 
                    key={i} 
                    className="w-8 h-8 rounded-full border-2 border-dashboard-background bg-dashboard-panel flex items-center justify-center text-xs font-bold text-dashboard-muted shadow-sm"
                    title={member.name}
                  >
                    {member.name.charAt(0)}
                  </div>
                ))}
                {team.members.length >= 3 && (
                  <div className="w-8 h-8 rounded-full border-2 border-dashboard-background bg-dashboard-primary/20 flex items-center justify-center text-[10px] font-bold text-dashboard-primary shadow-sm z-10">
                    +{team.members.length - 2}
                  </div>
                )}
              </div>
              
              <div className="text-right">
                <div className="text-[10px] uppercase font-bold text-dashboard-muted mb-0.5">Avg. Rating</div>
                <div className="text-sm font-bold text-orange-400">{team.avgRating}</div>
              </div>
            </div>
          </div>
        ))}
      </div>
      
      <div className="p-4 border-t border-dashboard-border/50 text-center">
        <Link to="/dashboard/team" className="text-xs font-bold text-dashboard-primary hover:text-dashboard-primary/80 transition-colors">
          View All Teams
        </Link>
      </div>
    </div>
  );
}
