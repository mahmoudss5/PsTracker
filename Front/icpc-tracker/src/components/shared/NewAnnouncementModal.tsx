import { useState, useEffect, useRef, FormEvent } from "react";
import { X, Megaphone, AlertTriangle, Info, RefreshCw } from "lucide-react";
import { toast } from "sonner";
import { createTeamAnnouncement } from "../../services/announcementService";

type AnnouncementType = "INFO" | "UPDATE" | "URGENT";

interface NewAnnouncementModalProps {
  isOpen: boolean;
  onClose: () => void;
  teamId: number;
  senderId: number;
  /** Called after a successful send so the panel can refetch */
  onSent?: () => void;
}

const TYPES: {
  value: AnnouncementType;
  label: string;
  icon: React.ReactNode;
  color: string;
  bg: string;
  border: string;
  ring: string;
}[] = [
  {
    value: "INFO",
    label: "Info",
    icon: <Info size={15} />,
    color: "text-dashboard-primary",
    bg: "bg-dashboard-primary/10",
    border: "border-dashboard-primary/40",
    ring: "ring-dashboard-primary/30",
  },
  {
    value: "UPDATE",
    label: "Update",
    icon: <Megaphone size={15} />,
    color: "text-yellow-400",
    bg: "bg-yellow-500/10",
    border: "border-yellow-400/40",
    ring: "ring-yellow-400/30",
  },
  {
    value: "URGENT",
    label: "Urgent",
    icon: <AlertTriangle size={15} />,
    color: "text-red-400",
    bg: "bg-red-500/10",
    border: "border-red-400/40",
    ring: "ring-red-400/30",
  },
];

const MAX_CHARS = 500;

export function NewAnnouncementModal({
  isOpen,
  onClose,
  teamId,
  senderId,
  onSent,
}: NewAnnouncementModalProps) {
  const [type, setType] = useState<AnnouncementType>("INFO");
  const [content, setContent] = useState("");
  const [isSending, setIsSending] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  // Focus textarea when modal opens
  useEffect(() => {
    if (isOpen) {
      setTimeout(() => textareaRef.current?.focus(), 80);
    } else {
      // Reset on close
      setType("INFO");
      setContent("");
    }
  }, [isOpen]);

  // Close on Escape
  useEffect(() => {
    if (!isOpen) return;
    const handler = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [isOpen, onClose]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const trimmed = content.trim();
    if (!trimmed) return;

    setIsSending(true);
    try {
      await createTeamAnnouncement({ type, content: trimmed, senderId, teamId });
      toast.success("Announcement sent!", {
        description: "Your team will see it in real-time.",
      });
      onSent?.();
      onClose();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Failed to send announcement");
    } finally {
      setIsSending(false);
    }
  };

  if (!isOpen) return null;

  const selected = TYPES.find((t) => t.value === type)!;
  const remaining = MAX_CHARS - content.length;
  const overLimit = remaining < 0;

  return (
    /* Backdrop */
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      {/* Blur overlay */}
      <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" />

      {/* Modal card */}
      <div
        className="relative w-full max-w-lg rounded-2xl border border-dashboard-border bg-dashboard-panel shadow-2xl animate-in fade-in zoom-in-95 duration-200"
        role="dialog"
        aria-modal="true"
        aria-label="New Announcement"
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-dashboard-border px-6 py-4">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-dashboard-primary/15">
              <Megaphone size={15} className="text-dashboard-primary" />
            </div>
            <h2 className="text-sm font-bold text-dashboard-text">New Announcement</h2>
          </div>
          <button
            onClick={onClose}
            className="flex h-7 w-7 items-center justify-center rounded-full text-dashboard-muted transition hover:bg-dashboard-elevated hover:text-dashboard-text"
            aria-label="Close"
          >
            <X size={16} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="px-6 py-5 space-y-5">
          {/* Type selector */}
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wider text-dashboard-muted">
              Type
            </label>
            <div className="grid grid-cols-3 gap-2">
              {TYPES.map((t) => (
                <button
                  key={t.value}
                  type="button"
                  onClick={() => setType(t.value)}
                  className={`flex flex-col items-center gap-1.5 rounded-xl border py-3 text-xs font-semibold transition-all duration-150 ${
                    type === t.value
                      ? `${t.bg} ${t.border} ${t.color} ring-2 ${t.ring} scale-[1.02]`
                      : "border-dashboard-border text-dashboard-muted hover:border-dashboard-primary/30 hover:text-dashboard-text"
                  }`}
                >
                  {t.icon}
                  {t.label}
                </button>
              ))}
            </div>
          </div>

          {/* Preview badge */}
          <div className={`inline-flex items-center gap-1.5 rounded-full border px-2.5 py-1 text-[11px] font-bold uppercase tracking-wider ${selected.bg} ${selected.border} ${selected.color}`}>
            {selected.icon}
            {selected.label} announcement
          </div>

          {/* Content textarea */}
          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-dashboard-muted">
              Message
            </label>
            <textarea
              ref={textareaRef}
              value={content}
              onChange={(e) => setContent(e.target.value.slice(0, MAX_CHARS + 10))}
              placeholder="Write your announcement here…"
              rows={5}
              className={`w-full resize-none rounded-xl border bg-dashboard-elevated px-4 py-3 text-sm text-dashboard-text placeholder:text-dashboard-muted/50 outline-none transition-all focus:ring-2 ${
                overLimit
                  ? "border-red-400/60 focus:border-red-400 focus:ring-red-400/20"
                  : "border-dashboard-border focus:border-dashboard-primary focus:ring-dashboard-primary/20"
              }`}
            />
            {/* Char count */}
            <div className={`text-right text-[11px] font-semibold transition-colors ${overLimit ? "text-red-400" : remaining < 50 ? "text-yellow-400" : "text-dashboard-muted"}`}>
              {remaining < MAX_CHARS ? `${remaining} remaining` : `${MAX_CHARS} chars max`}
            </div>
          </div>

          {/* Footer actions */}
          <div className="flex items-center justify-end gap-3 pt-1">
            <button
              type="button"
              onClick={onClose}
              className="rounded-lg border border-dashboard-border px-4 py-2 text-sm font-semibold text-dashboard-muted transition hover:border-dashboard-primary/30 hover:text-dashboard-text"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSending || !content.trim() || overLimit}
              className={`flex items-center gap-2 rounded-lg px-5 py-2 text-sm font-bold transition-all ${
                type === "URGENT"
                  ? "bg-red-500 hover:bg-red-400 text-white shadow-lg shadow-red-500/20"
                  : type === "UPDATE"
                  ? "bg-yellow-500 hover:bg-yellow-400 text-black shadow-lg shadow-yellow-500/20"
                  : "bg-dashboard-primary hover:bg-dashboard-primary/90 text-dashboard-primary-contrast shadow-lg shadow-dashboard-primary/20"
              } disabled:cursor-not-allowed disabled:opacity-50`}
            >
              {isSending ? (
                <>
                  <RefreshCw size={14} className="animate-spin" />
                  Sending…
                </>
              ) : (
                <>
                  <Megaphone size={14} />
                  Send Announcement
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
