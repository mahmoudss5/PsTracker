import { useEffect, useRef, useState } from "react";
import { MOCK_MESSAGES } from "../../data/mockProfile.ts";
import { Send, Star } from "lucide-react";


export function TeamChatPanel() {
    const [input, setInput] = useState("");
    const bottomRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, []);

    return (
        <div className="flex flex-col h-full">
            {/* Messages */}
            <div className="flex-1 overflow-y-auto space-y-6 px-4 py-4 md:px-8">
                {MOCK_MESSAGES.map((msg) =>
                    msg.isYou ? (
                        <div key={msg.id} className="flex flex-col items-end gap-1">
                            <span className="text-xs text-dashboard-muted mr-1">
                                {msg.time} · <span className="font-semibold text-dashboard-text">You</span>
                            </span>
                            <div
                                className="max-w-lg rounded-2xl rounded-tr-sm bg-dashboard-primary/15 border border-dashboard-primary/20 px-4 py-3 text-sm text-dashboard-text shadow">
                                <p>{msg.content}</p>
                                {msg.code && (
                                    <pre
                                        className="mt-3 rounded-lg bg-black/40 p-3 text-xs font-mono text-emerald-300 border border-dashboard-border overflow-x-auto">
                                        {msg.code}
                                    </pre>
                                )}
                            </div>
                        </div>
                    ) : msg.isCoach ? (
                        /* ── Coach message: golden amber special style ── */
                        <div key={msg.id} className="flex items-start gap-3">
                            {/* Avatar with glowing ring */}
                            <div className="relative shrink-0">
                                <div
                                    className="flex h-10 w-10 items-center justify-center rounded-full bg-amber-500/20 border-2 border-amber-400/60 text-xs font-extrabold text-amber-300 shadow-[0_0_12px_rgba(251,191,36,0.3)]">
                                    {msg.avatar}
                                </div>
                                {/* Star badge */}
                                <span
                                    className="absolute -bottom-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-amber-400 shadow-[0_0_8px_rgba(251,191,36,0.6)]">
                                    <Star size={9} className="fill-white text-white" />
                                </span>
                            </div>

                            <div className="flex flex-col gap-1 max-w-lg">
                                {/* Name row */}
                                <div className="flex items-center gap-1.5">
                                    <Star size={11} className="fill-amber-400 text-amber-400" />
                                    <span className="text-xs font-extrabold text-amber-300 tracking-wide">
                                        {msg.sender}
                                    </span>
                                    <span
                                        className="rounded-full bg-amber-400/15 border border-amber-400/25 px-1.5 py-0.5 text-[9px] font-bold uppercase tracking-wider text-amber-300">
                                        Coach
                                    </span>
                                    <span className="text-xs text-dashboard-muted">· {msg.time}</span>
                                </div>

                                {/* Bubble */}
                                <div
                                    className="rounded-2xl rounded-tl-sm border border-amber-400/25 bg-amber-500/[0.07] px-4 py-3 text-sm text-dashboard-text shadow-[0_0_20px_rgba(251,191,36,0.06)] ring-1 ring-amber-400/10">
                                    <p className="leading-relaxed">{msg.content}</p>
                                    {msg.code && (
                                        <pre
                                            className="mt-3 rounded-lg bg-black/40 p-3 text-xs font-mono text-emerald-300 border border-dashboard-border overflow-x-auto">
                                            {msg.code}
                                        </pre>
                                    )}
                                </div>
                            </div>
                        </div>
                    ) : (
                        /* ── Regular team member message ── */
                        <div key={msg.id} className="flex items-start gap-3">
                            <div
                                className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-dashboard-elevated border border-dashboard-border text-xs font-bold text-dashboard-muted">
                                {msg.avatar}
                            </div>
                            <div className="flex flex-col gap-1">
                                <span className="text-xs text-dashboard-muted">
                                    <span className="font-semibold text-dashboard-text">{msg.sender}</span> · {msg.time}
                                </span>
                                <div
                                    className="max-w-lg rounded-2xl rounded-tl-sm bg-dashboard-elevated border border-dashboard-border px-4 py-3 text-sm text-dashboard-text shadow">
                                    <p>{msg.content}</p>
                                    {msg.code && (
                                        <pre
                                            className="mt-3 rounded-lg bg-black/40 p-3 text-xs font-mono text-emerald-300 border border-dashboard-border overflow-x-auto">
                                            {msg.code}
                                        </pre>
                                    )}
                                </div>
                            </div>
                        </div>
                    )
                )}
                <div ref={bottomRef} />
            </div>

            {/* Input bar */}
            <div
                className="shrink-0 border-t border-dashboard-border bg-dashboard-panel/80 backdrop-blur px-4 py-3 md:px-8">
                <div
                    className="flex items-center gap-3 rounded-xl border border-dashboard-border bg-dashboard-elevated px-4 py-2 focus-within:border-dashboard-primary/50 transition-colors">
                    <input
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder="Type a message..."
                        className="flex-1 bg-transparent text-sm text-dashboard-text placeholder:text-dashboard-muted outline-none"
                    />
                    <button
                        className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-dashboard-primary text-dashboard-primary-contrast hover:brightness-110 transition"
                        onClick={() => setInput("")}
                    >
                        <Send size={15} />
                    </button>
                </div>
            </div>
        </div>
    );
}
