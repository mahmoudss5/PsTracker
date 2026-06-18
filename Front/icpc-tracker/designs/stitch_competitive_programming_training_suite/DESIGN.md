---
name: Deep Logic
colors:
  surface: '#051424'
  surface-dim: '#051424'
  surface-bright: '#2c3a4c'
  surface-container-lowest: '#010f1f'
  surface-container-low: '#0d1c2d'
  surface-container: '#122131'
  surface-container-high: '#1c2b3c'
  surface-container-highest: '#273647'
  on-surface: '#d4e4fa'
  on-surface-variant: '#c2c6d8'
  inverse-surface: '#d4e4fa'
  inverse-on-surface: '#233143'
  outline: '#8c90a1'
  outline-variant: '#424655'
  surface-tint: '#b0c6ff'
  primary: '#b0c6ff'
  on-primary: '#002d6f'
  primary-container: '#568dff'
  on-primary-container: '#002661'
  inverse-primary: '#0058cb'
  secondary: '#d0bcff'
  on-secondary: '#3c0091'
  secondary-container: '#571bc1'
  on-secondary-container: '#c4abff'
  tertiary: '#c6c5d2'
  on-tertiary: '#2f303a'
  tertiary-container: '#908f9c'
  on-tertiary-container: '#282933'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#d9e2ff'
  primary-fixed-dim: '#b0c6ff'
  on-primary-fixed: '#001945'
  on-primary-fixed-variant: '#00429c'
  secondary-fixed: '#e9ddff'
  secondary-fixed-dim: '#d0bcff'
  on-secondary-fixed: '#23005c'
  on-secondary-fixed-variant: '#5516be'
  tertiary-fixed: '#e3e1ef'
  tertiary-fixed-dim: '#c6c5d2'
  on-tertiary-fixed: '#1a1b24'
  on-tertiary-fixed-variant: '#454651'
  background: '#051424'
  on-background: '#d4e4fa'
  surface-variant: '#273647'
typography:
  headline-xl:
    fontFamily: Geist
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Geist
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Geist
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Geist
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Geist
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-mono:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Geist
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 4px
  xs: 8px
  sm: 16px
  md: 24px
  lg: 40px
  xl: 64px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 48px
---

## Brand & Style
The design system embodies a "High-Tech Professional" aesthetic, specifically tailored for enterprise-grade data analysis and developer tools. It blends the structural rigors of **Minimalism** with the depth of **Glassmorphism** to create a focused, immersive environment. The personality is precise, authoritative, and futuristic, evoking a sense of deep intelligence and crystalline clarity. 

Visual weight is distributed through transparency and light rather than solid fills. The user should feel they are interacting with a sophisticated digital cockpit where information is layered and luminous.

## Colors
The palette is anchored in a deep midnight foundation to maximize contrast and reduce eye strain during prolonged technical work.

- **Primary (Electric Blue):** Used for primary actions, progress indicators, and active states. It should feel high-energy and "emissive."
- **Secondary (Cyber Purple):** Reserved for data visualization accents, secondary categories, and subtle depth transitions.
- **Backgrounds:** The core surface is a rich midnight (#0A0B14). Lower-level containers use slight variants to establish hierarchy.
- **Accents:** Utilize subtle gradients from Primary to Secondary to indicate directionality or flow in complex data sets.

## Typography
The typography system prioritizes legibility and technical precision. **Geist** provides a clean, neutral canvas for the interface, while **JetBrains Mono** is introduced for data points, metrics, and code snippets to reinforce the high-tech narrative.

Headlines should use tight tracking and bold weights to command attention. Data labels use the monospaced font to ensure that numerical values align perfectly in tables and dashboards. All typography should maintain high contrast against the midnight backgrounds, utilizing pure white for headings and muted slate for secondary body text.

## Layout & Spacing
This design system employs a **Fluid Grid** with expanded whitespace to achieve an "Enterprise Breathable" feel. This prevents the interface from feeling cluttered despite the high density of data.

- **Grid:** 12-column system for desktop with 24px gutters.
- **Rhythm:** All spacing must be a multiple of 4px. 
- **Density:** Use `lg` and `xl` spacing tokens for section separation to provide visual "air." Components themselves should use `sm` or `md` internal padding to maintain a compact, tool-like feel within their glass containers.

## Elevation & Depth
Depth is achieved through **Glassmorphism** and light-based hierarchy rather than traditional shadows.

1.  **Layers:** Background blur (backdrop-filter: blur(12px)) is used on all floating panels and cards.
2.  **Borders:** Each glass container features a 1px solid border. The top and left borders are slightly lighter (15% opacity white) to simulate a subtle top-down light source.
3.  **Inner Depth:** Use a subtle 1px inner shadow (inset) to give cards a "carved" or "beveled" glass look.
4.  **Glows:** Active elements (like selected cards or primary buttons) emit a soft, 20px blur outer glow in the primary color at low opacity (20%) to simulate an active electronic state.

## Shapes
The shape language is "Soft-Technical." Use a consistent 4px radius (`rounded-sm`) for most functional elements to maintain a sharp, precise look. Larger containers like cards or modals may use up to 8px (`rounded-lg`) to soften the overall appearance, but never exceed this to avoid looking too consumer-oriented. Buttons and chips should follow the `rounded-sm` logic for a more professional, "milled" finish.

## Components
- **Buttons:** Primary buttons use a solid Electric Blue fill with a subtle "inner glow" gradient. Ghost buttons use the 1px high-contrast border.
- **Cards:** Must implement backdrop-blur (12px-20px), a 1px border (#FFFFFF 10%), and a very subtle inner shadow.
- **Inputs:** Darker than the background (#050508) with a 1px bottom border that glows Electric Blue on focus. Labels use the Monospace font.
- **Chips/Badges:** Use a semi-transparent fill of the primary or secondary color (15% opacity) with a solid 1px border of the same color.
- **Lists:** Rows are separated by 1px lines at 5% white opacity. On hover, the entire row should gain a subtle glass background.
- **Data Visualization:** Use thin lines (1.5px) for charts. Use Cyber Purple and Electric Blue as the primary contrasting data series.