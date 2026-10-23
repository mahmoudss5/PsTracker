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

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
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
  const queryClient = useQueryClient();
  const [liveNotifications, setLiveNotifications] = useState<NotificationResponse[]>([]);
  const [newIds, setNewIds] = useState<Set<number>>(new Set());
  const clientRef = useRef<Client | null>(null);
  const knownIdsRef = useRef<Set<number>>(new Set());

  // ── REST history ────────────────────────────────────────────────────────────
  const query = useQuery({
    queryKey: ["notifications", "me"],
    queryFn: fetchMyNotifications,
  });
  const notifications = useMemo(() => {
    const history = query.data ?? [];
    const byId = new Map<number, NotificationResponse>();
    [...history, ...liveNotifications].forEach((notification) => {
      byId.set(notification.id, notification);
    });
    return Array.from(byId.values()).sort(
      (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
    );
  }, [query.data, liveNotifications]);

  useEffect(() => {
    knownIdsRef.current = new Set(notifications.map((notification) => notification.id));
  }, [notifications]);

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

            setLiveNotifications((prev) => [notif, ...prev]);

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
      queryClient.setQueryData<NotificationResponse[]>(["notifications", "me"], (current) =>
        current?.map((notification) =>
          notification.id === id ? { ...notification, isRead: updated.isRead } : notification,
        ) ?? [],
      );
      setLiveNotifications((current) =>
        current.map((notification) =>
          notification.id === id ? { ...notification, isRead: updated.isRead } : notification,
        ),
      );
    } catch {
      // silent — badge will desync but not break UX
    }
  }, [queryClient]);

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return {
    notifications,
    unreadCount,
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    markRead,
    newIds,
    refetch: () => { void query.refetch(); },
  };
}
