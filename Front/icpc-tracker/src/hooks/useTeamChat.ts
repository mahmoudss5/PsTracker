/**
 * useTeamChat
 *
 * Manages the full lifecycle of the real-time team chat:
 *  1. Loads message history via REST on mount.
 *  2. Opens a STOMP-over-SockJS connection to the backend.
 *  3. Subscribes to /topic/teams/{teamId}/messages for live updates.
 *  4. Exposes a `send(content)` helper that publishes to /app/teams/{teamId}/messages.
 *  5. Tracks `newIds` — IDs of messages that just arrived live (entrance animation).
 *  6. Cleans up the connection on unmount / teamId change.
 */

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { webSocketUrl } from "../config/runtime";
import { getToken } from "../services/AuthService";
import { getTeamMessages } from "../services/chatService";
import type { ChatMessage } from "../types/api.types";

export type ChatStatus = "connecting" | "connected" | "disconnected" | "error";

interface UseTeamChatReturn {
  messages: ChatMessage[];
  status: ChatStatus;
  send: (content: string) => void;
  historyError: string | null;
  /** IDs of messages that just arrived in real-time (cleared after 2 s) */
  newIds: Set<number>;
}

export function useTeamChat(teamId: number | null): UseTeamChatReturn {
  const [liveMessagesByTeam, setLiveMessagesByTeam] = useState<Record<number, ChatMessage[]>>({});
  const [connectionState, setConnectionState] = useState<{ teamId: number | null; status: ChatStatus }>(() => ({
    teamId,
    status: teamId === null ? "disconnected" : "connecting",
  }));
  const [newIds, setNewIds] = useState<Set<number>>(new Set());
  const clientRef = useRef<Client | null>(null);
  const knownIdsRef = useRef<Set<number>>(new Set());

  // ── 1. Load history ────────────────────────────────────────────────────────
  const historyQuery = useQuery({
    queryKey: ["team-chat", teamId],
    queryFn: () => getTeamMessages(teamId as number),
    enabled: teamId !== null,
  });
  const messages = useMemo(() => {
    const history = historyQuery.data ?? [];
    const liveMessages = teamId === null ? [] : liveMessagesByTeam[teamId] ?? [];
    const byId = new Map<number, ChatMessage>();
    [...history, ...liveMessages].forEach((message) => byId.set(message.id, message));
    return Array.from(byId.values()).sort(
      (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
    );
  }, [historyQuery.data, liveMessagesByTeam, teamId]);

  useEffect(() => {
    knownIdsRef.current = new Set(messages.map((message) => message.id));
  }, [messages]);

  // ── 2. STOMP connection ────────────────────────────────────────────────────
  useEffect(() => {
    if (teamId === null) return;

    const token = getToken();

    const client = new Client({
      // SockJS factory — keeps the "/ws" path consistent with the backend
      webSocketFactory: () =>
        new SockJS(webSocketUrl) as WebSocket,

      // Pass the JWT in the STOMP CONNECT frame headers so the
      // WebSocketAuthInterceptor can authenticate the user.
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},

      reconnectDelay: 5000,

      onConnect: () => {
        setConnectionState({ teamId, status: "connected" });

        client.subscribe(
          `/topic/teams/${teamId}/messages`,
          (frame) => {
            try {
              const msg: ChatMessage = JSON.parse(frame.body);

              // Deduplicate
              if (knownIdsRef.current.has(msg.id)) return;
              knownIdsRef.current.add(msg.id);

              setLiveMessagesByTeam((prev) => ({
                ...prev,
                [teamId]: [...(prev[teamId] ?? []), msg],
              }));

              // Track as "new" for entrance animation (clear after 2 s)
              setNewIds((prev) => {
                const next = new Set(prev);
                next.add(msg.id);
                return next;
              });
              setTimeout(() => {
                setNewIds((prev) => {
                  const next = new Set(prev);
                  next.delete(msg.id);
                  return next;
                });
              }, 2000);
            } catch {
              // Silently ignore malformed frames
            }
          },
        );
      },

      onStompError: () => setConnectionState({ teamId, status: "error" }),
      onDisconnect: () => setConnectionState({ teamId, status: "disconnected" }),
      onWebSocketError: () => setConnectionState({ teamId, status: "error" }),
    });

    clientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [teamId]);

  // ── 3. Send helper ─────────────────────────────────────────────────────────
  const send = useCallback(
    (content: string) => {
      const trimmed = content.trim();
      if (!trimmed || teamId === null) return;

      const client = clientRef.current;
      if (client?.connected) {
        // Send over STOMP (real-time path)
        client.publish({
          destination: `/app/teams/${teamId}/messages`,
          body: JSON.stringify({ content: trimmed }),
        });
      }
    },
    [teamId],
  );

  const status = connectionState.teamId === teamId
    ? connectionState.status
    : teamId === null
      ? "disconnected"
      : "connecting";

  return {
    messages,
    status,
    send,
    historyError: historyQuery.error instanceof Error ? historyQuery.error.message : null,
    newIds,
  };
}
