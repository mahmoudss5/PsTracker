import { useState } from "react";
import { Link } from "react-router-dom";
import {
  MessageSquare,
  Megaphone,
  BookOpen,
  BarChart2,
} from "lucide-react";
import { PiCrownSimpleFill } from "react-icons/pi";
import { MaterialsPanel } from "../components/shared/MaterialsPanel";
import { MOCK_ANNOUNCEMENTS } from "../data/mockProfile";
import { AnnouncementsPanel } from "../components/shared/AnnouncementsPanel";
import { TeamChatPanel } from "../components/shared/TeamChatPane";

type Tab = "chat" | "announcements" | "materials";

const TABS: { id: Tab; label: string; icon: React.ReactNode }[] = [
  { id: "chat", label: "Team Chat", icon: <MessageSquare size={16} /> },
  { id: "announcements", label: "Coach Announcements", icon: <Megaphone size={16} /> },
  { id: "materials", label: "Training Materials", icon: <BookOpen size={16} /> },
];
const teamMembers = [
  { name: "Mahmoud Aziz", handle: "tourist_wannabe", role: "coach"  },
  { name: "Ahmed Sayed",  handle: "ahmed_sayed",     role: "member" },
  { name: "Jana",          handle: "jana_cp",          role: "member" },
  { name: "Ahmed Mohsed", handle: "ahmed_mohsed",    role: "member" },
];

export function TeamPage() {
  const [activeTab, setActiveTab] = useState<Tab>("chat");

  const badgeCount: Partial<Record<Tab, number>> = {
    announcements: MOCK_ANNOUNCEMENTS.filter((a) => a.type === "URGENT").length,
  };

  return (
    // Stretch to fill the entire main content area, minus the top nav padding
    <div className="flex flex-col -mx-4 -my-6 md:-mx-12 md:-my-8 h-[calc(100vh-4rem)]">
      {/* Page header */}
      <div className="shrink-0 border-b border-dashboard-border bg-dashboard-panel/70 backdrop-blur px-4 md:px-12 pt-6 pb-0">
        <div className="mb-4">
          <h1 className="text-2xl font-extrabold tracking-tight text-dashboard-text">
            Team Hub
          </h1>
          <p className="text-sm text-dashboard-muted mt-0.5">
            Real-time coordination and technical resources.
          </p>
        </div>
       {/* team members  */}
       <div>
           <h1 className="text-lg font-semibold tracking-tight text-dashboard-text">
             Team members:
           </h1>
           <ul className="mt-3 flex">
            {teamMembers.map((member, index) => (
              <li
                className={`mb-3 flex items-center gap-3 ${
                  member.role === "coach" ? "mr-8" : ""
                } ${index !== teamMembers.length - 1 ? "mr-4" : ""}`}
                key={index}
              >
                <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-dashboard-primary text-dashboard-primary-contrast font-bold">
                  {member.name.charAt(0).toUpperCase()}
                </div>
                <span className="text-lg font-bold text-dashboard-text">{member.name}</span>
                {member.role === "coach" && (
                  <span className="text-yellow-500 text-sm"><PiCrownSimpleFill /></span>
                )}
                {member.role === "member" && (
                  <Link
                    to={`/dashboard/submissions/${member.handle}`}
                    id={`view-submissions-${member.handle}`}
                    title={`View ${member.name}'s submissions`}
                    className="ml-1 inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-[11px] font-semibold border border-dashboard-primary/30 text-dashboard-primary bg-dashboard-primary/5 hover:bg-dashboard-primary/15 transition-all duration-200"
                  >
                    <BarChart2 size={11} />
                    Stats
                  </Link>
                )}
              </li>
            ))}
           </ul>
       </div>


        {/* Tab bar */}
        <nav className="flex gap-0 overflow-x-auto">
          {TABS.map((tab) => {
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                id={`tab-${tab.id}`}
                onClick={() => setActiveTab(tab.id)}
                className={`relative flex shrink-0 items-center gap-2 px-5 py-3 text-sm font-semibold transition-colors duration-200 border-b-2
                  ${
                    isActive
                      ? "border-dashboard-primary text-dashboard-primary"
                      : "border-transparent text-dashboard-muted hover:text-dashboard-text hover:border-dashboard-border"
                  }`}
              >
                {tab.icon}
                {tab.label}
                {badgeCount[tab.id] ? (
                  <span className="ml-1 flex h-4.5 min-w-[18px] items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white">
                    {badgeCount[tab.id]}
                  </span>
                ) : null}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Tab content — fills the rest */}
      <div className="flex-1 overflow-hidden">
        {activeTab === "chat" && <TeamChatPanel />}
        {activeTab === "announcements" && <AnnouncementsPanel />}
        {activeTab === "materials" && <MaterialsPanel />}
      </div>
    </div>
  );
}
