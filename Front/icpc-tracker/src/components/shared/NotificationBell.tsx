/**
 * NotificationBell
 *
 * Navbar bell icon that:
 *  - Shows a red badge with unread count
 *  - Rings (CSS animation) when a new notification arrives
 *  - Opens a dropdown panel with the notification list
 *  - Marks notifications as read on click
 */

import { useEffect, useRef, useState } from "react";
import { Bell, CheckCheck, Loader2 } from "lucide-react";
import { useNotifications } from "../../hooks/useNotifications";
import type { NotificationResponse } from "../../types/api.types";

const typeColor: Record<string, string> = {
  SYSTEM:       "bg-dashboard-primary/10 text-dashboard-primary",
  ANNOUNCEMENT: "bg-amber-500/10 text-amber-400",
  URGENT:       "bg-red-500/10 text-red-400",
  INFO:         "bg-emerald-500/10 text-emerald-400",
};

function formatTime(iso: string) {
  try {
    const d = new Date(iso);
    const now = Date.now();
    const diff = now - d.getTime();
    if (diff < 60_000) return "just now";
    if (diff < 3_600_000) return `${Math.floor(diff / 60_000)}m ago`;
    if (diff < 86_400_000) return `${Math.floor(diff / 3_600_000)}h ago`;
    return d.toLocaleDateString();
  } catch {
    return "";
  }
}

function NotifItem({
  notif,
  isNew,
  onRead,
}: {
  notif: NotificationResponse;
  isNew: boolean;
  onRead: (id: number) => void;
}) {
  const color = typeColor[notif.type] ?? typeColor["SYSTEM"];

  return (
    <button
      onClick={() => !notif.isRead && onRead(notif.id)}
      className={`w-full text-left flex gap-3 px-4 py-3 transition-colors hover:bg-dashboard-primary/5
        ${notif.isRead ? "opacity-60" : "bg-dashboard-elevated"}
        ${isNew ? "animate-notif-drop-in" : ""}
      `}
    >
      <div
        className={`mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-xs font-bold ${color}`}
      >
        {notif.type?.[0] ?? "?"}
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-1">
          <p className={`text-xs font-bold truncate ${notif.isRead ? "text-dashboard-muted" : "text-dashboard-text"}`}>
            {notif.title}
          </p>
          <span className="shrink-0 text-[10px] text-dashboard-muted">{formatTime(notif.createdAt)}</span>
        </div>
        {notif.message && (
          <p className="text-[11px] text-dashboard-muted leading-snug mt-0.5 line-clamp-2">
            {notif.message}
          </p>
        )}
      </div>
      {!notif.isRead && (
        <span className="mt-1.5 h-2 w-2 shrink-0 rounded-full bg-dashboard-primary" />
      )}
    </button>
  );
}

export function NotificationBell() {
  const { notifications, unreadCount, isLoading, markRead, newIds, refetch } =
    useNotifications();
  const [open, setOpen] = useState(false);
  const [ringing, setRinging] = useState(false);
  const prevUnreadRef = useRef(0);
  const panelRef = useRef<HTMLDivElement>(null);

  // Ring animation when unread count increases
  useEffect(() => {
    if (unreadCount > prevUnreadRef.current) {
      setRinging(true);
      const t = setTimeout(() => setRinging(false), 700);
      return () => clearTimeout(t);
    }
    prevUnreadRef.current = unreadCount;
  }, [unreadCount]);

  // Close on outside click
  useEffect(() => {
    if (!open) return;
    const handler = (e: MouseEvent) => {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, [open]);

  // Mark all visible as read when panel is opened
  const handleOpen = () => {
    setOpen((v) => !v);
    if (!open) refetch();
  };

  return (
    <div ref={panelRef} className="relative">
      {/* Bell button */}
      <button
        onClick={handleOpen}
        className={`icon-button relative ${ringing ? "animate-bell-ring" : ""}`}
        title="Notifications"
        aria-label="Notifications"
      >
        <Bell size={20} />
        {unreadCount > 0 && (
          <span className="absolute -right-1 -top-1 flex h-4 min-w-[16px] items-center justify-center rounded-full bg-red-500 px-0.5 text-[9px] font-bold text-white leading-none">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown panel */}
      {open && (
        <div className="absolute right-0 top-full mt-2 z-50 w-80 rounded-xl border border-dashboard-border bg-dashboard-panel shadow-2xl shadow-black/20 backdrop-blur-lg overflow-hidden">
          {/* Header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-dashboard-border">
            <h3 className="text-sm font-bold text-dashboard-text">Notifications</h3>
            {unreadCount > 0 && (
              <button
                onClick={() =>
                  notifications
                    .filter((n) => !n.isRead)
                    .forEach((n) => markRead(n.id))
                }
                className="flex items-center gap-1 text-xs font-semibold text-dashboard-primary hover:text-dashboard-primary/80 transition-colors"
              >
                <CheckCheck size={13} />
                Mark all read
              </button>
            )}
          </div>

          {/* Body */}
          <div className="max-h-96 overflow-y-auto divide-y divide-dashboard-border/50">
            {isLoading && (
              <div className="flex items-center justify-center gap-2 py-8 text-dashboard-muted text-sm">
                <Loader2 size={16} className="animate-spin text-dashboard-primary" />
                Loading…
              </div>
            )}

            {!isLoading && notifications.length === 0 && (
              <div className="flex flex-col items-center justify-center gap-2 py-10 text-dashboard-muted">
                <Bell size={24} className="opacity-40" />
                <p className="text-xs font-semibold">You're all caught up!</p>
              </div>
            )}

            {!isLoading &&
              notifications.map((n) => (
                <NotifItem
                  key={n.id}
                  notif={n}
                  isNew={newIds.has(n.id)}
                  onRead={markRead}
                />
              ))}
          </div>
        </div>
      )}
    </div>
  );
}
