import { CheckCircle, Terminal, Flame, ArrowUpRight } from "lucide-react";

interface StatItemProps {
  title: string;
  value: string;
  subtext: string;
  icon: "solved" | "submissions" | "streak";
  valueSuffix?: string;
}

export function StatItem({ title, value, subtext, icon, valueSuffix }: StatItemProps) {
  const renderIcon = () => {
    switch (icon) {
      case "solved":
        return (
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-emerald-500/10 text-emerald-500 ring-4 ring-emerald-500/5">
            <CheckCircle size={20} className="stroke-[2]" />
          </div>
        );
      case "submissions":
        return (
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-dashboard-primary/10 text-dashboard-primary ring-4 ring-dashboard-primary/5">
            <Terminal size={20} className="stroke-[2]" />
          </div>
        );
      case "streak":
        return (
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-orange-500/10 text-orange-500 ring-4 ring-orange-500/5 animate-pulse">
            <Flame size={20} className="fill-orange-500/20 stroke-[2]" />
          </div>
        );
    }
  };

  return (
    <div className="glass-panel p-6 flex items-start justify-between relative overflow-hidden group hover:border-dashboard-primary/30 transition-all duration-300">
      {/* Subtle hover background highlight */}
      <div className="absolute inset-0 bg-gradient-to-br from-dashboard-primary/5 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none" />

      <div className="space-y-3 z-10">
        <span className="text-xs font-bold uppercase tracking-wider text-dashboard-muted">
          {title}
        </span>
        <div className="flex items-baseline gap-1">
          <span className="text-3xl font-extrabold tracking-tight text-dashboard-text sm:text-4xl">
            {value}
          </span>
          {valueSuffix && (
            <span className="text-sm font-semibold text-dashboard-muted">
              {valueSuffix}
            </span>
          )}
        </div>
        <p className="flex items-center gap-1.5 text-xs font-medium text-dashboard-muted">
          {icon === "solved" && (
            <ArrowUpRight size={14} className="text-emerald-500 animate-bounce" />
          )}
          <span>{subtext}</span>
        </p>
      </div>

      <div className="z-10 shrink-0">
        {renderIcon()}
      </div>
    </div>
  );
}

export function StatsGroup() {
  return (
    <div className="grid grid-cols-1 gap-4 sm:gap-6 sm:grid-cols-3">
      <StatItem
        title="Total Solved"
        value="1,492"
        subtext="+24 this week"
        icon="solved"
      />
      <StatItem
        title="Total Submissions"
        value="4,815"
        subtext="31% Acceptance Rate"
        icon="submissions"
      />
      <StatItem
        title="Current Streak"
        value="42"
        valueSuffix="days"
        subtext="Best: 68 days"
        icon="streak"
      />
    </div>
  );
}
