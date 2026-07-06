import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    // SockJS (used by @stomp/stompjs) references Node.js `global` which
    // doesn't exist in the browser. Vite's `define` replaces it at build time.
    global: 'globalThis',
  },
})

