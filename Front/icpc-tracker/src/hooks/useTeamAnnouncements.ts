/**
 * useTeamAnnouncements
 *
 * Loads announcement history via REST, then subscribes to the STOMP topic
 * /topic/teams/{teamId}/announcments for live updates.
 * When a new announcement arrives in real-time it:
 *   1. Prepends it to the list (newest first).
 *   2. Fires a `sonner` toast with type-aware styling.
 */

import { useCallback, useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { webSocketUrl } from "../config/runtime";
import { toast } from "sonner";
import { getToken } from "../services/AuthService";
import { getAnnouncementsForTeam, type AnnouncementResponseDto } from "../services/announcementService";

export interface UseTeamAnnouncementsResult {
  announcements: AnnouncementResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
  /** IDs of announcements that just arrived live (for entrance animation) */
  newIds: Set<number>;
}

const TOAST_ICON: Record<string, string> = {
  URGENT: "🚨",
  UPDATE: "📢",
  INFO: "ℹ️",
};

export function useTeamAnnouncements(
  teamId: number | undefined,
): UseTeamAnnouncementsResult {
  const [announcements, setAnnouncements] = useState<AnnouncementResponseDto[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [newIds, setNewIds] = useState<Set<number>>(new Set());
  const clientRef = useRef<Client | null>(null);
  // Track IDs we already have to detect true newcomers
  const knownIdsRef = useRef<Set<number>>(new Set());

  // ── REST history ────────────────────────────────────────────────────────────
  const fetchData = useCallback(async () => {
    if (!teamId) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getAnnouncementsForTeam(teamId);
      setAnnouncements(data);
      knownIdsRef.current = new Set(data.map((a) => a.id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load announcements");
    } finally {
      setIsLoading(false);
    }
  }, [teamId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // ── STOMP subscription ──────────────────────────────────────────────────────
  useEffect(() => {
    if (!teamId) return;

    const token = getToken();

    const client = new Client({
      webSocketFactory: () => new SockJS(webSocketUrl) as WebSocket,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000,

      onConnect: () => {
        client.subscribe(
          `/topic/teams/${teamId}/announcments`,
          (frame) => {
            try {
              const ann: AnnouncementResponseDto = JSON.parse(frame.body);

              // Ignore if we already have it (from history)
              if (knownIdsRef.current.has(ann.id)) return;
              knownIdsRef.current.add(ann.id);

              // Prepend — newest first
              setAnnouncements((prev) => [ann, ...prev]);

              // Mark as "new" for entrance animation (clear after 3 s)
              setNewIds((prev) => {
                const next = new Set(prev);
                next.add(ann.id);
                return next;
              });
              setTimeout(() => {
                setNewIds((prev) => {
                  const next = new Set(prev);
                  next.delete(ann.id);
                  return next;
                });
              }, 3000);

              // 🔔 Toast notification
              const icon = TOAST_ICON[ann.type] ?? "📣";
              if (ann.type === "URGENT") {
                toast.error(`${icon} URGENT announcement`, {
                  description: ann.content.slice(0, 100) + (ann.content.length > 100 ? "…" : ""),
                  duration: 8000,
                });
              } else if (ann.type === "UPDATE") {
                toast.warning(`${icon} Coach update`, {
                  description: ann.content.slice(0, 100) + (ann.content.length > 100 ? "…" : ""),
                  duration: 6000,
                });
              } else {
                toast.info(`${icon} New announcement`, {
                  description: ann.content.slice(0, 100) + (ann.content.length > 100 ? "…" : ""),
                  duration: 5000,
                });
              }
            } catch {
              // ignore malformed frames
            }
          },
        );
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [teamId]);

  return { announcements, isLoading, error, refetch: fetchData, newIds };
}
