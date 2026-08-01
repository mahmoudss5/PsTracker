import { useEffect, useState, useRef } from "react";
import { webSocketUrl } from "../config/runtime";
import { getToken } from "../services/AuthService";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";



export const usePresence = (UserId: number) => {
    const [presence, setPresence] = useState([]);
    const clientRef = useRef<Client | null>(null);
    const [status, setStatus] = useState("pending");

    const updatePresence = () => {
        if (clientRef.current && clientRef.current.connected) {
            clientRef.current.publish({
                destination: "presence.update",
                body: JSON.stringify({ userId: UserId })
            });
        }
    };

    useEffect(() => {
        if (!UserId) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(webSocketUrl)
        });
        
        // 1. Assign client to the ref so updatePresence can use it
        clientRef.current = client;

        client.onConnect = () => {
            console.log("connected");
            setStatus("connected");
            
            // 2. Subscriptions must happen INSIDE onConnect
            client.subscribe("/presence.heartbeat", (presenceUsers) => {
                setPresence(JSON.parse(presenceUsers.body || "[]"));
            });
        };

        // 3. Start the connection! (Your code was missing this)
        client.activate();

        // 4. Set up an interval to call updatePresence every 10 seconds (10000 ms)
        const intervalId = setInterval(() => {
            updatePresence();
        }, 10000);

        return () => {
            clearInterval(intervalId); // Stop the interval when unmounting
            client.deactivate();
        };
    }, [UserId]); // Re-run if UserId changes



}