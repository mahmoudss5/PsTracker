export const apiBaseUrl = import.meta.env.VITE_API_URL as string;
// VITE_WS_URL may not be set; derive it from the API base URL if missing.
// Backend WS endpoint is at /ws (e.g. http://localhost:8080/ws).
export const webSocketUrl =
  (import.meta.env.VITE_WS_URL as string | undefined) ??
  apiBaseUrl.replace(/\/api$/, "/ws");