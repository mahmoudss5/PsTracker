---
name: Deep Logic
colors:
  surface: '#111317'
  surface-dim: '#111317'
  surface-bright: '#37393e'
  surface-container-lowest: '#0c0e12'
  surface-container-low: '#1a1c20'
  surface-container: '#1e2024'
  surface-container-high: '#282a2e'
  surface-container-highest: '#333539'
  on-surface: '#e2e2e8'
  on-surface-variant: '#c2c6d6'
  inverse-surface: '#e2e2e8'
  inverse-on-surface: '#2f3035'
  outline: '#8c909f'
  outline-variant: '#424754'
  surface-tint: '#adc6ff'
  primary: '#adc6ff'
  on-primary: '#002e6a'
  primary-container: '#4d8eff'
  on-primary-container: '#00285d'
  inverse-primary: '#005ac2'
  secondary: '#4edea3'
  on-secondary: '#003824'
  secondary-container: '#00a572'
  on-secondary-container: '#00311f'
  tertiary: '#ffb95f'
  on-tertiary: '#472a00'
  tertiary-container: '#ca8100'
  on-tertiary-container: '#3e2400'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#adc6ff'
  on-primary-fixed: '#001a42'
  on-primary-fixed-variant: '#004395'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#111317'
  on-background: '#e2e2e8'
  surface-variant: '#333539'
typography:
  display:
    fontFamily: Geist
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Geist
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  headline-md:
    fontFamily: Geist
    fontSize: 20px
    fontWeight: '500'
    lineHeight: '1.4'
  body-lg:
    fontFamily: Geist
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Geist
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
  code-sm:
    fontFamily: JetBrains Mono
    fontSize: 13px
    fontWeight: '400'
    lineHeight: '1.5'
  label-caps:
    fontFamily: Geist
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: 0.05em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  sidebar_width: 260px
  container_max_width: 1440px
  gutter: 24px
  margin_mobile: 16px
  stack_sm: 8px
  stack_md: 16px
  stack_lg: 32px
---

## Brand & Style
The design system is engineered for high-performance cognitive environments where focus and data density are paramount. The brand personality is professional, austere, and uncompromisingly efficient, mirroring the mental clarity required for competitive programming.

The visual style is **High-Contrast Minimalism** with a focus on structural integrity. By utilizing a deep charcoal foundation and precise, low-opacity borders, the UI recedes into the background, allowing code snippets, performance metrics, and algorithmic data to take center stage. Visual noise is eliminated to prevent cognitive load during intense training sessions.

## Colors
The palette is rooted in a "Deep Dark" philosophy. The primary background uses `#0F1115` to reduce eye strain, while component surfaces use a slightly lifted `#16191E`. 

- **Primary (Cyber Blue):** Reserved exclusively for intent-driven actions and active states.
- **Success (Emerald):** Utilized for "Accepted" statuses and positive growth metrics.
- **Warning (Amber):** Used for "Time Limit Exceeded" or "Partial Credit" scenarios.
- **Borders:** A consistent `#1E2128` provides structural definition without the weight of traditional shadows.

## Typography
This design system utilizes **Geist** for its exceptional clarity and technical aesthetic. Its geometric nature supports the professional tone of the tracker. To supplement data-heavy views, **JetBrains Mono** is employed for labels, problem IDs, and code snippets, providing a distinct visual "mode" for technical information.

Headlines should be kept concise. Tracking is tightened slightly on larger display sizes to maintain a compact, "engineered" look.

## Layout & Spacing
The layout follows a **Fixed-Fluid Hybrid** model. A fixed 260px sidebar persists on the left for global navigation, while the main content area occupies the remaining width, capped at 1440px to ensure readability on ultrawide monitors.

- **Grid:** A 12-column grid is used within dashboard containers.
- **Spacing Rhythm:** An 8px base unit drives all padding and margins. 
- **Mobile Adaptivity:** On devices under 768px, the sidebar transitions to a bottom navigation bar or a collapsed "hamburger" drawer, and internal margins reduce to 16px.

## Elevation & Depth
Depth is communicated through **Tonal Layering** rather than traditional shadows. 

1. **Level 0 (Background):** `#0F1115` - The canvas.
2. **Level 1 (Cards/Containers):** `#16191E` - Features a 1px solid border of `#1E2128`.
3. **Level 2 (Popovers/Modals):** `#1C1F26` - Features a subtle 10% opacity Cyber Blue outer glow to indicate focus.

Interaction feedback is achieved through subtle border-color shifts (e.g., `#1E2128` to `#3B82F6`) rather than elevation changes, maintaining a flat, high-tech aesthetic.

## Shapes
The shape language is "Soft-Technical." Elements use a 4px (0.25rem) base radius to maintain a modern feel while appearing more precise and structured than fully rounded designs. 

- **Standard Elements:** 4px radius (Buttons, Inputs, Chips).
- **Large Containers:** 8px radius (Dashboard Cards).
- **Strict Elements:** 0px radius (Code block side-decorators, vertical dividers).

## Components
- **Buttons:** Primary buttons use a solid `#3B82F6` background with white text. Secondary buttons are "Ghost" style: 1px border of `#1E2128` with a hover transition to a subtle `#1E2128` background fill.
- **Data Tables:** Minimalist execution. No vertical lines. Horizontal dividers use 1px `#1E2128`. Header cells use `label-caps` typography with a muted `#6B7280` color.
- **Progress Bars:** Thin 4px tracks using `#1E2128` as the background and `#10B981` (Success) or `#3B82F6` (Primary) for the fill.
- **Status Chips:** Small, low-saturation backgrounds with high-saturation text (e.g., an "Easy" tag uses a deep emerald tint with bright emerald text).
- **Input Fields:** Dark fill (`#0F1115`) with a 1px border. Focus state triggers a 1px Cyber Blue border—no "halo" or glow.
- **Dashboard Cards:** Simple `#16191E` surfaces with 24px internal padding. Titles always use `headline-md`.