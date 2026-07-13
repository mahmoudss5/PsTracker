/**
 * useNotifications
 *
 * Loads notification history from REST (GET /api/notifications/me) and
 * subscribes to the STOMP user-specific queue /user/queue/notifications for
 * real-time push notifications from the backend.
 *
 * Exposes:
 *  - notifications: list sorted newest-first
 *  - unreadCount: badge number for the bell icon
 *  - markRead(id): PATCH /api/notifications/{id}/read
 *  - newIds: Set of IDs that just arrived live (for animation)
 */

import { useCallback, useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { webSocketUrl } from "../config/runtime";
import { toast } from "sonner";
import { getToken } from "../services/AuthService";
import { apiClient } from "../config/api";
import { normalizeApiError } from "../services/ErrorService";
import type { NotificationResponse } from "../types/api.types";

export interface UseNotificationsResult {
  notifications: NotificationResponse[];
  unreadCount: number;
  isLoading: boolean;
  error: string | null;
  markRead: (id: number) => Promise<void>;
  newIds: Set<number>;
  refetch: () => void;
}

async function fetchMyNotifications(): Promise<NotificationResponse[]> {
  try {
    const res = await apiClient.get<NotificationResponse[]>("/notifications/me");
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, "Failed to load notifications");
  }
}

async function patchMarkRead(id: number): Promise<NotificationResponse> {
  try {
    const res = await apiClient.patch<NotificationResponse>(`/notifications/${id}/read`);
    return res.data;
  } catch (err) {
    throw normalizeApiError(err, "Failed to mark notification as read");
  }
}

export function useNotifications(): UseNotificationsResult {
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [newIds, setNewIds] = useState<Set<number>>(new Set());
  const clientRef = useRef<Client | null>(null);
  const knownIdsRef = useRef<Set<number>>(new Set());

  // ── REST history ────────────────────────────────────────────────────────────
  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await fetchMyNotifications();
      // Sort newest-first
      const sorted = [...data].sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
      setNotifications(sorted);
      knownIdsRef.current = new Set(sorted.map((n) => n.id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load notifications");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // ── STOMP subscription ──────────────────────────────────────────────────────
  useEffect(() => {
    const token = getToken();
    if (!token) return;

    const client = new Client({
      webSocketFactory: () => new SockJS(webSocketUrl) as WebSocket,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,

      onConnect: () => {
        // The backend uses convertAndSendToUser(email, "/queue/notifications", …)
        // which translates to /user/queue/notifications on the client side
        client.subscribe("/user/queue/notifications", (frame) => {
          try {
            const notif: NotificationResponse = JSON.parse(frame.body);
            if (knownIdsRef.current.has(notif.id)) return;
            knownIdsRef.current.add(notif.id);

            setNotifications((prev) => [notif, ...prev]);

            // Mark for animation
            setNewIds((prev) => {
              const next = new Set(prev);
              next.add(notif.id);
              return next;
            });
            setTimeout(() => {
              setNewIds((prev) => {
                const next = new Set(prev);
                next.delete(notif.id);
                return next;
              });
            }, 3000);

            // Toast
            toast.info(`🔔 ${notif.title}`, {
              description: notif.message?.slice(0, 120),
              duration: 5000,
            });
          } catch {
            // ignore
          }
        });
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, []);

  // ── Mark read ────────────────────────────────────────────────────────────────
  const markRead = useCallback(async (id: number) => {
    try {
      const updated = await patchMarkRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: updated.isRead } : n)),
      );
    } catch {
      // silent — badge will desync but not break UX
    }
  }, []);

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return { notifications, unreadCount, isLoading, error, markRead, newIds, refetch: fetchData };
}
