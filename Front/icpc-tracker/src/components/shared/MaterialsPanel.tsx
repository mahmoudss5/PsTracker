import {Download, FileText, ImageIcon, Link2} from "lucide-react";
import {MOCK_MATERIALS} from "../../data/mockProfile.ts";
import type {Material} from "../../data/mockProfile.ts";

export function MaterialsPanel() {
    const iconFor = (kind: Material["kind"]) => {
        if (kind === "link") return <Link2 size={18} className="text-dashboard-primary" />;
        if (kind === "pdf") return <FileText size={18} className="text-red-400" />;
        return <ImageIcon size={18} className="text-emerald-400" />;
    };

    const bgFor = (kind: Material["kind"]) => {
        if (kind === "link") return "bg-dashboard-primary/10 border-dashboard-primary/20";
        if (kind === "pdf") return "bg-red-500/10 border-red-500/20";
        return "bg-emerald-500/10 border-emerald-500/20";
    };

    return (
        <div className="h-full overflow-y-auto px-4 py-4 md:px-8">
            <p className="text-xs text-dashboard-muted mb-4">
                {MOCK_MATERIALS.length} resources uploaded by your coach
            </p>
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">
                {MOCK_MATERIALS.map((mat) => (
                    <div
                        key={mat.id}
                        className="glass-panel p-4 flex items-start gap-4 hover:border-dashboard-primary/30 transition-all duration-200 group cursor-pointer"
                    >
                        <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg border ${bgFor(mat.kind)}`}>
                            {iconFor(mat.kind)}
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-bold text-dashboard-text group-hover:text-dashboard-primary transition-colors truncate">
                                {mat.title}
                            </p>
                            <p className="text-xs text-dashboard-muted truncate mt-0.5">{mat.subtitle}</p>
                        </div>
                        <button className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity text-dashboard-muted hover:text-dashboard-text">
                            <Download size={16} />
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}
