/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        surface: {
          DEFAULT: '#0a0f1a',
          card: '#0d1526',
          input: '#111827',
          border: '#1e293b',
        },
        accent: {
          DEFAULT: '#56c1f2',
          hover: '#3db4ec',
          muted: 'rgba(86, 193, 242, 0.15)',
        },
        muted: '#94a3b8',
        dashboard: {
          DEFAULT: 'rgb(var(--dashboard-bg) / <alpha-value>)',
          panel: 'rgb(var(--dashboard-panel) / <alpha-value>)',
          elevated: 'rgb(var(--dashboard-elevated) / <alpha-value>)',
          border: 'rgb(var(--dashboard-border) / <alpha-value>)',
          text: 'rgb(var(--dashboard-text) / <alpha-value>)',
          muted: 'rgb(var(--dashboard-muted) / <alpha-value>)',
          primary: 'rgb(var(--dashboard-primary) / <alpha-value>)',
          'primary-contrast': 'rgb(var(--dashboard-primary-contrast) / <alpha-value>)',
        },
      },
      fontFamily: {
        sans: ['Geist', 'Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'ui-monospace', 'monospace'],
      },
      boxShadow: {
        glow: '0 0 60px rgba(86, 193, 242, 0.12)',
        card: '0 0 0 1px rgba(86, 193, 242, 0.25), 0 0 40px rgba(86, 193, 242, 0.08)',
      },
    },
  },
  plugins: [],
}
