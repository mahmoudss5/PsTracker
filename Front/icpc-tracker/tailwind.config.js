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
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        glow: '0 0 60px rgba(86, 193, 242, 0.12)',
        card: '0 0 0 1px rgba(86, 193, 242, 0.25), 0 0 40px rgba(86, 193, 242, 0.08)',
      },
    },
  },
  plugins: [],
}
