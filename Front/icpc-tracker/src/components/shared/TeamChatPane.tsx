/**
 * TeamChatPanel
 *
 * Real-time team chat powered by STOMP over SockJS.
 * – Uses `useTeamChat` for connection management.
 * – New messages slide in with animate-msg-slide-in.
 * – `getUserId` / `getUserName` detect own messages.
 * – Coach messages get amber styling.
 */

import { useEffect, useRef, useState, type KeyboardEvent } from "react";
import { AlertCircle, Loader2, Send, Star, Wifi, WifiOff } from "lucide-react";
import { useTeamChat } from "../../hooks/useTeamChat";
import { getUserId, getUserName } from "../../services/AuthService";
import type { ChatMessage } from "../../types/api.types";

// ─── helpers ──────────────────────────────────────────────────────────────────

function initials(name: string): string {
  return (
    name
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((p) => p[0]?.toUpperCase())
      .join("") || "?"
  );
}

function formatTime(isoString: string): string {
  try {
    return new Date(isoString).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return "";
  }
}

// ─── status badge ─────────────────────────────────────────────────────────────

function StatusBadge({ status }: { status: string }) {
  if (status === "connected")
    return (
      <span className="flex items-center gap-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 px-2 py-0.5 text-[10px] font-bold uppercase tracking-widest text-emerald-500">
        <Wifi size={9} /> Live
      </span>
    );
  if (status === "connecting")
    return (
      <span className="flex items-center gap-1 rounded-full bg-dashboard-primary/10 border border-dashboard-primary/20 px-2 py-0.5 text-[10px] font-bold uppercase tracking-widest text-dashboard-primary">
        <Loader2 size={9} className="animate-spin" /> Connecting
      </span>
    );
  return (
    <span className="flex items-center gap-1 rounded-full bg-red-500/10 border border-red-500/20 px-2 py-0.5 text-[10px] font-bold uppercase tracking-widest text-red-400">
      <WifiOff size={9} /> Offline
    </span>
  );
}

// ─── message bubbles ──────────────────────────────────────────────────────────

function OwnMessage({ msg, isNew }: { msg: ChatMessage; isNew: boolean }) {
  return (
    <div className={`flex flex-col items-end gap-1 ${isNew ? "animate-msg-slide-in" : ""}`}>
      <span className="text-xs text-dashboard-muted mr-1">
        {formatTime(msg.createdAt)} ·{" "}
        <span className="font-semibold text-dashboard-text">You</span>
      </span>
      <div className="max-w-lg rounded-2xl rounded-tr-sm bg-dashboard-primary/15 border border-dashboard-primary/20 px-4 py-3 text-sm text-dashboard-text shadow">
        <p className="leading-relaxed whitespace-pre-wrap break-words">{msg.content}</p>
      </div>
    </div>
  );
}

function CoachMessage({ msg, isNew }: { msg: ChatMessage; isNew: boolean }) {
  return (
    <div className={`flex items-start gap-3 ${isNew ? "animate-msg-slide-in" : ""}`}>
      {/* Avatar with glowing ring */}
      <div className="relative shrink-0">
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-amber-500/20 border-2 border-amber-400/60 text-xs font-extrabold text-amber-300 shadow-[0_0_12px_rgba(251,191,36,0.3)]">
          {initials(msg.senderUsername)}
        </div>
        <span className="absolute -bottom-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-amber-400 shadow-[0_0_8px_rgba(251,191,36,0.6)]">
          <Star size={9} className="fill-white text-white" />
        </span>
      </div>

      <div className="flex flex-col gap-1 max-w-lg">
        <div className="flex items-center gap-1.5">
          <Star size={11} className="fill-amber-400 text-amber-400" />
          <span className="text-xs font-extrabold text-amber-300 tracking-wide">
            {msg.senderUsername}
          </span>
          <span className="rounded-full bg-amber-400/15 border border-amber-400/25 px-1.5 py-0.5 text-[9px] font-bold uppercase tracking-wider text-amber-300">
            Coach
          </span>
          <span className="text-xs text-dashboard-muted">· {formatTime(msg.createdAt)}</span>
        </div>
        <div className="rounded-2xl rounded-tl-sm border border-amber-400/25 bg-amber-500/[0.07] px-4 py-3 text-sm text-dashboard-text shadow-[0_0_20px_rgba(251,191,36,0.06)] ring-1 ring-amber-400/10">
          <p className="leading-relaxed whitespace-pre-wrap break-words">{msg.content}</p>
        </div>
      </div>
    </div>
  );
}

function PeerMessage({ msg, isNew }: { msg: ChatMessage; isNew: boolean }) {
  return (
    <div className={`flex items-start gap-3 ${isNew ? "animate-msg-slide-in" : ""}`}>
      <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-dashboard-elevated border border-dashboard-border text-xs font-bold text-dashboard-muted">
        {initials(msg.senderUsername)}
      </div>
      <div className="flex flex-col gap-1">
        <span className="text-xs text-dashboard-muted">
          <span className="font-semibold text-dashboard-text">{msg.senderUsername}</span>{" "}
          · {formatTime(msg.createdAt)}
        </span>
        <div className="max-w-lg rounded-2xl rounded-tl-sm bg-dashboard-elevated border border-dashboard-border px-4 py-3 text-sm text-dashboard-text shadow">
          <p className="leading-relaxed whitespace-pre-wrap break-words">{msg.content}</p>
        </div>
      </div>
    </div>
  );
}

// ─── main component ───────────────────────────────────────────────────────────

interface TeamChatPanelProps {
  teamId: number;
  coachUsername?: string;
}

export function TeamChatPanel({ teamId, coachUsername }: TeamChatPanelProps) {
  const currentUserId = getUserId() as number | null;
  const currentUserName = getUserName() as string | null;

  const { messages, status, send, historyError, newIds } = useTeamChat(teamId);

  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Auto-scroll whenever messages change
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    const trimmed = input.trim();
    if (!trimmed) return;
    send(trimmed);
    setInput("");
    inputRef.current?.focus();
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const renderMessage = (msg: ChatMessage) => {
    const isNew = newIds.has(msg.id);
    const isOwn =
      msg.senderId === currentUserId || msg.senderUsername === currentUserName;
    const isMsgCoach =
      coachUsername !== undefined && msg.senderUsername === coachUsername;

    if (isOwn) return <OwnMessage key={msg.id} msg={msg} isNew={isNew} />;
    if (isMsgCoach) return <CoachMessage key={msg.id} msg={msg} isNew={isNew} />;
    return <PeerMessage key={msg.id} msg={msg} isNew={isNew} />;
  };

  return (
    <div className="flex flex-col h-full">
      {/* ── Connection status bar ── */}
      <div className="shrink-0 flex items-center justify-between px-4 py-2 md:px-8 border-b border-dashboard-border/50 bg-dashboard-panel/40">
        <span className="text-xs font-semibold text-dashboard-muted">Team Chat</span>
        <StatusBadge status={status} />
      </div>

      {/* ── History error banner ── */}
      {historyError && (
        <div className="shrink-0 flex items-center gap-2 px-4 py-2 md:px-8 bg-red-500/10 border-b border-red-500/20 text-xs font-semibold text-red-400">
          <AlertCircle size={13} />
          Could not load history: {historyError}
        </div>
      )}

      {/* ── Messages ── */}
      <div className="flex-1 overflow-y-auto space-y-6 px-4 py-4 md:px-8">
        {messages.length === 0 && status === "connected" && (
          <div className="flex flex-col items-center justify-center h-full gap-3 text-dashboard-muted">
            <span className="text-4xl">💬</span>
            <p className="text-sm font-semibold">No messages yet — say hello!</p>
          </div>
        )}

        {messages.length === 0 && status === "connecting" && (
          <div className="flex items-center justify-center h-full gap-2 text-dashboard-muted text-sm">
            <Loader2 size={16} className="animate-spin text-dashboard-primary" />
            Connecting to team chat…
          </div>
        )}

        {messages.map(renderMessage)}
        <div ref={bottomRef} />
      </div>

      {/* ── Input bar ── */}
      <div className="shrink-0 border-t border-dashboard-border bg-dashboard-panel/80 backdrop-blur px-4 py-3 md:px-8">
        <div className="flex items-center gap-3 rounded-xl border border-dashboard-border bg-dashboard-elevated px-4 py-2 focus-within:border-dashboard-primary/50 transition-colors">
          <input
            ref={inputRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder={
              status === "connected"
                ? "Type a message… (Enter to send)"
                : "Waiting for connection…"
            }
            disabled={status !== "connected"}
            maxLength={256}
            className="flex-1 bg-transparent text-sm text-dashboard-text placeholder:text-dashboard-muted outline-none disabled:opacity-50"
          />
          <span className="shrink-0 text-[10px] text-dashboard-muted tabular-nums">
            {input.length}/256
          </span>
          <button
            onClick={handleSend}
            disabled={!input.trim() || status !== "connected"}
            className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-dashboard-primary text-dashboard-primary-contrast hover:brightness-110 transition disabled:opacity-40 disabled:cursor-not-allowed"
            aria-label="Send message"
          >
            <Send size={15} />
          </button>
        </div>
      </div>
    </div>
  );
}
