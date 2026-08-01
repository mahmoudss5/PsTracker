import { useEffect, useRef, useState } from "react";
import { webSocketUrl } from "../config/runtime";
import { getToken } from "../services/AuthService";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const usePresence = (UserId: number | undefined) => {
    const [presence, setPresence] = useState<number[]>([]);
    const [status, setStatus] = useState("pending");
    const clientRef = useRef<Client | null>(null);

    useEffect(() => {
        console.log("[Presence] effect triggered. UserId=", UserId, "webSocketUrl=", webSocketUrl);

        if (!UserId) {
            console.warn("[Presence] No UserId — skipping connection.");
            return;
        }

        const token = getToken();
        console.log("[Presence] token present=", Boolean(token), "connecting to", webSocketUrl);

        const client = new Client({
            webSocketFactory: () => {
                console.log("[Presence] Creating SockJS to", webSocketUrl);
                return new SockJS(webSocketUrl) as WebSocket;
            },

            // The WebSocketAuthInterceptor requires this header.
            connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},

            reconnectDelay: 5000,

            onConnect: () => {
                console.log("[Presence] STOMP connected ✅");
                setStatus("connected");

                client.subscribe("/topic/presence", (frame) => {
                    console.log("[Presence] received frame:", frame.body);
                    try {
                        const users = JSON.parse(frame.body || "[]");
                        // Backend sends List<UserPresence> where each has a userId field (Long).
                        const ids = users.map((u: any) =>
                            typeof u === "number" ? u : Number(u.userId ?? u.id)
                        );
                        console.log("[Presence] online user IDs:", ids);
                        setPresence(ids);
                    } catch (err) {
                        console.error("[Presence] failed to parse frame", err);
                    }
                });

                // Immediately announce this user is online.
                console.log("[Presence] publishing heartbeat for userId=", UserId);
                client.publish({
                    destination: "/app/presence.update",
                    body: JSON.stringify({ userId: UserId }),
                });
            },

            onStompError: (frame) => {
                console.error("[Presence] STOMP error ❌", frame);
                setStatus("error");
            },
            onDisconnect: () => {
                console.warn("[Presence] disconnected");
                setStatus("disconnected");
            },
            onWebSocketError: (err) => {
                console.error("[Presence] WebSocket error ❌", err);
                setStatus("error");
            },
        });

        clientRef.current = client;
        client.activate();

        // Heartbeat: keep Redis TTL alive every 10 seconds.
        const intervalId = setInterval(() => {
            if (clientRef.current?.connected) {
                clientRef.current.publish({
                    destination: "/app/presence.update",
                    body: JSON.stringify({ userId: UserId }),
                });
            }
        }, 10_000);

        return () => {
            clearInterval(intervalId);
            client.deactivate();
            clientRef.current = null;
        };
    }, [UserId]);

    return { presence, status };
};