import { useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  AlertCircle,
  BarChart2,
  BookOpen,
  Clipboard,
  Loader2,
  Megaphone,
  MessageSquare,
  Plus,
  RefreshCw,
  Search,
  Upload,
  Users,
  UsersRound,
} from "lucide-react";
import { PiCrownSimpleFill } from "react-icons/pi";
import { toast } from "sonner";
import { MaterialsPanel } from "../components/shared/MaterialsPanel";
import { MOCK_ANNOUNCEMENTS } from "../data/mockProfile";
import { AnnouncementsPanel } from "../components/shared/AnnouncementsPanel";
import { TeamChatPanel } from "../components/shared/TeamChatPane";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { useCoachTeams, useTeam } from "../hooks/useTeam";
import { useUsers } from "../hooks/useUsers";
import { createTeam, joinTeam } from "../services/teamService";
import type { TeamResponse, TraineeResponse } from "../types/api.types";

type Tab = "chat" | "announcements" | "materials";

const TABS: { id: Tab; label: string; icon: React.ReactNode }[] = [
  { id: "chat", label: "Team Chat", icon: <MessageSquare size={16} /> },
  { id: "announcements", label: "Coach Announcements", icon: <Megaphone size={16} /> },
  { id: "materials", label: "Training Materials", icon: <BookOpen size={16} /> },
];

function isCoachRole(role?: string) {
  return role?.toLowerCase() === "coach";
}

function initials(name: string) {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("") || "U";
}

function EmptyState({ title, action }: { title: string; action?: React.ReactNode }) {
  return (
    <div className="flex min-h-[12rem] flex-col items-center justify-center rounded-lg border border-dashed border-dashboard-border px-6 py-10 text-center">
      <UsersRound size={28} className="text-dashboard-muted" />
      <p className="mt-3 text-sm font-semibold text-dashboard-text">{title}</p>
      {action}
    </div>
  );
}

function CoachOverview() {
  const navigate = useNavigate();
  const [teamName, setTeamName] = useState("");
  const [query, setQuery] = useState("");
  const [isCreating, setIsCreating] = useState(false);
  const { teams, isLoading: teamsLoading, error: teamsError, refetch: refetchTeams } = useCoachTeams();
  const { users, isLoading: usersLoading, error: usersError, refetch: refetchUsers } = useUsers();

  const trainees = useMemo(
    () =>
      users
        .filter((user) => !isCoachRole(user.role))
        .filter((user) => {
          const needle = query.trim().toLowerCase();
          if (!needle) return true;
          return (
            user.userName.toLowerCase().includes(needle) ||
            user.email.toLowerCase().includes(needle) ||
            user.teamName?.toLowerCase().includes(needle)
          );
        }),
    [query, users],
  );

  const handleCreateTeam = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!teamName.trim()) return;
    setIsCreating(true);
    try {
      const created = await createTeam({ teamName: teamName.trim() });
      toast.success(`Team created. Invite code: ${created.teamCode}`);
      setTeamName("");
      refetchTeams();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Failed to create team");
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-dashboard-text">Coach Workspace</h1>
          <p className="mt-1 text-sm text-dashboard-muted">Manage trainees and open any team hub.</p>
        </div>
        <form onSubmit={handleCreateTeam} className="flex w-full gap-2 sm:w-auto">
          <input
            value={teamName}
            onChange={(event) => setTeamName(event.target.value)}
            placeholder="New team name"
            className="min-w-0 flex-1 rounded border border-dashboard-border bg-dashboard-panel px-3 py-2 text-sm text-dashboard-text outline-none focus:border-dashboard-primary sm:w-56"
          />
          <button
            type="submit"
            disabled={isCreating || !teamName.trim()}
            className="inline-flex items-center justify-center gap-2 rounded bg-dashboard-primary px-4 py-2 text-sm font-semibold text-dashboard-primary-contrast transition hover:brightness-105 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isCreating ? <Loader2 size={16} className="animate-spin" /> : <Plus size={16} />}
            Create
          </button>
        </form>
      </div>

      <div className="grid grid-cols-1 gap-5 xl:grid-cols-[minmax(0,1.15fr)_minmax(22rem,0.85fr)]">
        <section className="glass-panel overflow-hidden">
          <div className="flex flex-col gap-3 border-b border-dashboard-border p-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-base font-bold text-dashboard-text">Users</h2>
              <p className="text-xs text-dashboard-muted">{trainees.length} trainees</p>
            </div>
            <div className="flex items-center gap-2 rounded border border-dashboard-border bg-dashboard-elevated px-3 py-2">
              <Search size={15} className="text-dashboard-muted" />
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Search users"
                className="w-full bg-transparent text-sm text-dashboard-text outline-none placeholder:text-dashboard-muted sm:w-56"
              />
            </div>
          </div>

          {usersLoading ? (
            <LoadingBlock label="Loading users" />
          ) : usersError ? (
            <ErrorBlock message={usersError} onRetry={refetchUsers} />
          ) : trainees.length === 0 ? (
            <EmptyState title="No trainees found" />
          ) : (
            <div className="divide-y divide-dashboard-border">
              {trainees.map((user) => (
                <UserRow key={user.id} user={user} />
              ))}
            </div>
          )}
        </section>

        <section className="glass-panel overflow-hidden">
          <div className="border-b border-dashboard-border p-4">
            <h2 className="text-base font-bold text-dashboard-text">Teams</h2>
            <p className="text-xs text-dashboard-muted">{teams.length} active teams</p>
          </div>

          {teamsLoading ? (
            <LoadingBlock label="Loading teams" />
          ) : teamsError ? (
            <ErrorBlock message={teamsError} onRetry={refetchTeams} />
          ) : teams.length === 0 ? (
            <EmptyState title="No teams created yet" />
          ) : (
            <div className="grid grid-cols-1 gap-3 p-4">
              {teams.map((team) => (
                <button
                  key={team.id}
                  onClick={() => navigate(`/dashboard/team/${team.id}`)}
                  className="rounded-lg border border-dashboard-border bg-dashboard-elevated p-4 text-left transition hover:border-dashboard-primary/50 hover:bg-dashboard-primary/5"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="truncate text-sm font-bold text-dashboard-text">{team.teamName}</p>
                      <p className="mt-1 font-mono text-xs text-dashboard-muted">{team.teamCode}</p>
                    </div>
                    <span className="rounded-full bg-dashboard-primary/10 px-2 py-1 text-xs font-semibold text-dashboard-primary">
                      {team.trainees.length}/4
                    </span>
                  </div>
                  <div className="mt-4 flex -space-x-2">
                    {team.trainees.slice(0, 4).map((member) => (
                      <span
                        key={member.id}
                        className="flex h-8 w-8 items-center justify-center rounded-full border border-dashboard-panel bg-dashboard-primary text-xs font-bold text-dashboard-primary-contrast"
                      >
                        {initials(member.userName)}
                      </span>
                    ))}
                  </div>
                </button>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}

function UserRow({ user }: { user: TraineeResponse }) {
  return (
    <div className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex min-w-0 items-center gap-3">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-dashboard-primary text-sm font-bold text-dashboard-primary-contrast">
          {initials(user.userName)}
        </div>
        <div className="min-w-0">
          <p className="truncate text-sm font-bold text-dashboard-text">{user.userName}</p>
          <p className="truncate text-xs text-dashboard-muted">{user.email}</p>
        </div>
      </div>
      <div className="flex flex-wrap items-center gap-2 sm:justify-end">
        <span className="rounded-full border border-dashboard-border px-2 py-1 text-xs font-semibold text-dashboard-muted">
          {user.teamName ?? "No team"}
        </span>
        <span className="rounded-full border border-dashboard-border px-2 py-1 text-xs font-semibold text-dashboard-muted">
          {user.numberOfSolveProblems ?? 0} solved
        </span>
        <Link
          to={`/dashboard/submissions/${user.id}`}
          className="inline-flex items-center gap-1 rounded bg-dashboard-primary/10 px-2.5 py-1.5 text-xs font-semibold text-dashboard-primary transition hover:bg-dashboard-primary/20"
        >
          <BarChart2 size={13} />
          Stats
        </Link>
      </div>
    </div>
  );
}

function LoadingBlock({ label }: { label: string }) {
  return (
    <div className="flex min-h-[12rem] items-center justify-center gap-2 p-8 text-sm text-dashboard-muted">
      <Loader2 size={18} className="animate-spin text-dashboard-primary" />
      {label}
    </div>
  );
}

function ErrorBlock({ message, onRetry }: { message: string; onRetry: () => void }) {
  return (
    <div className="flex min-h-[12rem] flex-col items-center justify-center gap-3 p-8 text-center">
      <AlertCircle size={24} className="text-red-400" />
      <p className="text-sm font-semibold text-dashboard-text">{message}</p>
      <button
        onClick={onRetry}
        className="inline-flex items-center gap-2 rounded border border-dashboard-border px-3 py-2 text-xs font-semibold text-dashboard-muted transition hover:border-dashboard-primary/50 hover:text-dashboard-text"
      >
        <RefreshCw size={13} />
        Retry
      </button>
    </div>
  );
}

function TeamDetail({ team, isCoach }: { team: TeamResponse; isCoach: boolean }) {
  const [activeTab, setActiveTab] = useState<Tab>("chat");
  const badgeCount: Partial<Record<Tab, number>> = {
    announcements: MOCK_ANNOUNCEMENTS.filter((announcement) => announcement.type === "URGENT").length,
  };

  const copyInviteCode = async () => {
    try {
      await navigator.clipboard.writeText(team.teamCode);
      toast.success("Invite code copied");
    } catch {
      toast.error("Could not copy invite code");
    }
  };

  return (
    <div className="flex h-[calc(100vh-4rem)] flex-col -mx-4 -my-6 md:-mx-12 md:-my-8">
      <div className="shrink-0 border-b border-dashboard-border bg-dashboard-panel/70 px-4 pb-0 pt-6 backdrop-blur md:px-12">
        <div className="mb-4 flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
          <div className="min-w-0">
            {isCoach && (
              <Link
                to="/dashboard/team"
                className="mb-2 inline-flex items-center text-xs font-semibold text-dashboard-muted transition hover:text-dashboard-primary"
              >
                Back to teams
              </Link>
            )}
            <h1 className="truncate text-2xl font-extrabold tracking-tight text-dashboard-text">{team.teamName}</h1>
            <p className="mt-0.5 text-sm text-dashboard-muted">Coach: {team.coachUsername}</p>
          </div>

          {isCoach && (
            <div className="flex flex-wrap gap-2">
              <button onClick={copyInviteCode} className="team-action-button">
                <Clipboard size={15} />
                {team.teamCode}
              </button>
              <button className="team-action-button">
                <Megaphone size={15} />
                Announcement
              </button>
              <button className="team-action-button">
                <Upload size={15} />
                Material
              </button>
            </div>
          )}
        </div>

        <div className="mb-3">
          <h2 className="mb-3 text-sm font-semibold tracking-tight text-dashboard-text">Team members</h2>
          <ul className="flex gap-3 overflow-x-auto pb-1">
            <li className="flex shrink-0 items-center gap-2 rounded-lg border border-amber-400/25 bg-amber-500/10 px-3 py-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-amber-400/20 text-xs font-bold text-amber-300">
                {initials(team.coachUsername)}
              </div>
              <span className="text-sm font-bold text-dashboard-text">{team.coachUsername}</span>
              <PiCrownSimpleFill className="text-amber-400" />
            </li>
            {team.trainees.map((member) => (
              <li key={member.id} className="flex shrink-0 items-center gap-2 rounded-lg border border-dashboard-border bg-dashboard-elevated px-3 py-2">
                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-dashboard-primary text-xs font-bold text-dashboard-primary-contrast">
                  {initials(member.userName)}
                </div>
                <span className="text-sm font-bold text-dashboard-text">{member.userName}</span>
                {isCoach && (
                  <Link
                    to={`/dashboard/submissions/${member.id}`}
                    className="inline-flex items-center gap-1 rounded-full px-2 py-1 text-[11px] font-semibold text-dashboard-primary transition hover:bg-dashboard-primary/10"
                  >
                    <BarChart2 size={11} />
                    Stats
                  </Link>
                )}
              </li>
            ))}
          </ul>
        </div>

        <nav className="flex gap-0 overflow-x-auto">
          {TABS.map((tab) => {
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`relative flex shrink-0 items-center gap-2 border-b-2 px-5 py-3 text-sm font-semibold transition-colors ${
                  isActive
                    ? "border-dashboard-primary text-dashboard-primary"
                    : "border-transparent text-dashboard-muted hover:border-dashboard-border hover:text-dashboard-text"
                }`}
              >
                {tab.icon}
                {tab.label}
                {badgeCount[tab.id] ? (
                  <span className="ml-1 flex min-w-[18px] items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white">
                    {badgeCount[tab.id]}
                  </span>
                ) : null}
              </button>
            );
          })}
        </nav>
      </div>

      <div className="flex-1 overflow-hidden">
        {activeTab === "chat" && <TeamChatPanel />}
        {activeTab === "announcements" && <AnnouncementsPanel />}
        {activeTab === "materials" && <MaterialsPanel />}
      </div>
    </div>
  );
}

function JoinTeamView({ onJoined }: { onJoined: () => void }) {
  const [teamCode, setTeamCode] = useState("");
  const [isJoining, setIsJoining] = useState(false);

  const handleJoinTeam = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!teamCode.trim()) return;
    setIsJoining(true);
    try {
      await joinTeam({ teamCode: teamCode.trim() });
      toast.success("Successfully joined team");
      onJoined();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Failed to join team");
    } finally {
      setIsJoining(false);
    }
  };

  return (
    <div className="flex h-[calc(100vh-4rem)] flex-col items-center justify-center px-4">
      <div className="glass-panel w-full max-w-md p-8 text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-dashboard-primary/10">
          <Users size={32} className="text-dashboard-primary" />
        </div>
        <h2 className="mb-2 text-2xl font-extrabold text-dashboard-text">Join a Team</h2>
        <p className="mb-8 text-sm text-dashboard-muted">Enter the invite code provided by your coach.</p>
        <form onSubmit={handleJoinTeam} className="flex flex-col gap-4">
          <input
            value={teamCode}
            onChange={(event) => setTeamCode(event.target.value)}
            placeholder="Team invite code"
            className="w-full rounded-lg border border-dashboard-border bg-dashboard-background px-4 py-2.5 text-dashboard-text outline-none transition focus:border-transparent focus:ring-2 focus:ring-dashboard-primary"
            disabled={isJoining}
          />
          <button
            type="submit"
            disabled={isJoining || !teamCode.trim()}
            className="flex w-full items-center justify-center gap-2 rounded-lg bg-dashboard-primary px-4 py-2.5 font-semibold text-dashboard-primary-contrast transition hover:bg-dashboard-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isJoining && <Loader2 size={16} className="animate-spin" />}
            Join Team
          </button>
        </form>
      </div>
    </div>
  );
}

export function TeamPage() {
  const { teamId: routeTeamId } = useParams<{ teamId?: string }>();
  const parsedRouteTeamId = routeTeamId ? Number(routeTeamId) : null;
  const { user, isLoading: userLoading, error: userError, refetch: refetchUser } = useCurrentUser();
  const isCoach = isCoachRole(user?.role);
  const selectedTeamId = parsedRouteTeamId ?? user?.teamId ?? null;
  const shouldLoadTeam = selectedTeamId !== null && Number.isFinite(selectedTeamId);
  const { team, isLoading: teamLoading, error: teamError, refetch: refetchTeam } = useTeam(
    shouldLoadTeam ? selectedTeamId : null,
  );

  if (userLoading) {
    return <LoadingBlock label="Loading profile" />;
  }

  if (userError) {
    return <ErrorBlock message={userError} onRetry={refetchUser} />;
  }

  if (isCoach && parsedRouteTeamId === null) {
    return <CoachOverview />;
  }

  if (!isCoach && !user?.teamId) {
    return <JoinTeamView onJoined={refetchUser} />;
  }

  if (teamLoading) {
    return <LoadingBlock label="Loading team" />;
  }

  if (teamError) {
    return <ErrorBlock message={teamError} onRetry={refetchTeam} />;
  }

  if (!team) {
    return (
      <EmptyState
        title="Team not found"
        action={
          isCoach ? (
            <Link to="/dashboard/team" className="mt-4 text-sm font-semibold text-dashboard-primary">
              Back to teams
            </Link>
          ) : null
        }
      />
    );
  }

  return <TeamDetail team={team} isCoach={isCoach} />;
}
