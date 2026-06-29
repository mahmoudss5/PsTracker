import {ChevronRight, Filter} from "lucide-react";
import {MOCK_ANNOUNCEMENTS, typeBorder, typeColor} from "../../data/mockProfile.ts";

export function AnnouncementsPanel() {
    return (
        <div className="h-full overflow-y-auto px-4 py-4 md:px-8 space-y-4">
            <div className="flex items-center justify-between mb-2">
                <p className="text-xs text-dashboard-muted">
                    {MOCK_ANNOUNCEMENTS.length} announcements from your coach
                </p>
                <button className="flex items-center gap-1.5 rounded-lg border border-dashboard-border px-3 py-1.5 text-xs font-semibold text-dashboard-muted hover:border-dashboard-primary/40 hover:text-dashboard-text transition">
                    <Filter size={13} />
                    Filter
                </button>
            </div>

            {MOCK_ANNOUNCEMENTS.map((ann) => (
                <div
                    key={ann.id}
                    className={`glass-panel border-l-4 ${typeBorder[ann.type]} p-5 hover:border-r-dashboard-primary/20 transition-all duration-200 group cursor-pointer`}
                >
                    <div className="flex items-start justify-between gap-3">
                        <div className="flex-1 space-y-2">
                            <div className="flex items-center gap-2 flex-wrap">
                <span
                    className={`inline-flex items-center rounded-full border px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider ${typeColor[ann.type]}`}
                >
                  {ann.type}
                </span>
                                <span className="text-xs text-dashboard-muted">
                  {ann.date}, {ann.time}
                </span>
                            </div>
                            <h3 className="text-base font-bold text-dashboard-text group-hover:text-dashboard-primary transition-colors">
                                {ann.title}
                            </h3>
                            <p className="text-sm leading-relaxed text-dashboard-muted">{ann.body}</p>
                            <div className="flex items-center gap-2 pt-1">
                                <div className="flex h-6 w-6 items-center justify-center rounded-full bg-dashboard-elevated border border-dashboard-border text-[10px] font-bold text-dashboard-muted">
                                    CA
                                </div>
                                <span className="text-xs font-semibold text-dashboard-muted">{ann.author}</span>
                            </div>
                        </div>
                        <ChevronRight
                            size={16}
                            className="shrink-0 text-dashboard-muted opacity-0 group-hover:opacity-100 transition-opacity mt-1"
                        />
                    </div>
                </div>
            ))}
        </div>
    );
}
