---
name: Artisan Logic
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#20201f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353535'
  on-surface: '#e5e2e1'
  on-surface-variant: '#cbc6ba'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#959086'
  outline-variant: '#4a473e'
  surface-tint: '#cec6ad'
  primary: '#ffffff'
  on-primary: '#35301e'
  primary-container: '#ebe2c8'
  on-primary-container: '#6a644f'
  inverse-primary: '#645e49'
  secondary: '#a7c8ff'
  on-secondary: '#003061'
  secondary-container: '#20497f'
  on-secondary-container: '#95baf6'
  tertiary: '#ffffff'
  on-tertiary: '#3c2f00'
  tertiary-container: '#ffe088'
  on-tertiary-container: '#7b6100'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ebe2c8'
  primary-fixed-dim: '#cec6ad'
  on-primary-fixed: '#1f1c0b'
  on-primary-fixed-variant: '#4c4733'
  secondary-fixed: '#d5e3ff'
  secondary-fixed-dim: '#a7c8ff'
  on-secondary-fixed: '#001b3c'
  on-secondary-fixed-variant: '#1d477c'
  tertiary-fixed: '#ffe088'
  tertiary-fixed-dim: '#e9c349'
  on-tertiary-fixed: '#241a00'
  on-tertiary-fixed-variant: '#574500'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353535'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 64px
    fontWeight: '700'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 40px
    fontWeight: '600'
    lineHeight: '1.2'
  headline-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.2'
  title-md:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: '600'
    lineHeight: '1.5'
    letterSpacing: 0.05em
  body-md:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  label-sm:
    fontFamily: IBM Plex Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1.4'
    letterSpacing: 0.1em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 8px
  container-max: 1200px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 64px
---

## Brand & Style

The design system adopts a **"Sophisticated Industrial"** aesthetic, blending the rugged, tactile quality of premium leather craftsmanship with the precision of modern software development. It targets high-level engineers and architects who value tools that feel as durable and well-made as a physical heirloom.

The style is defined by **Tactile Minimalism**. It utilizes deep, textured backgrounds that evoke wood or dark parchment, contrasted with "modal-style" content cards that feature elegant, high-contrast typography. The emotional response is one of reliability, focus, and quiet authority.

Visual hallmarks include:
- **Rich Textures:** Subtle noise and grain on dark surfaces.
- **Framed Content:** Every interface element is treated like a curated artifact within a frame.
- **Heritage Modernism:** A mix of classic serif weights with sharp, technical sans-serifs.

## Colors

The palette is anchored in a **Deep Ebony and Parchment** contrast. 

- **Primary (#F4EBD0):** A warm, aged parchment white used for high-contrast typography and essential UI borders.
- **Secondary (#5F84BD):** The legacy blue from the framework, used sparingly as an accent for links, interactive states, and progress indicators.
- **Tertiary (#D4AF37):** A muted gold/brass for calls to action and "Buy Now" equivalent moments, providing a premium finish.
- **Neutral (#1A1A1A):** A dark, wood-grain charcoal that serves as the canvas for the entire experience.

Backgrounds should use a slight radial gradient or noise texture to prevent "flatness," mimicking the organic variation found in the reference material.

## Typography

This design system uses a high-contrast typographic hierarchy to separate "Narrative" from "Data."

- **Playfair Display** is used for high-impact headlines and display text, evoking a literary and premium feel. It should be used for section headers and "hero" statements.
- **Manrope** provides a balanced, modern touch for body copy, ensuring long-form documentation remains readable and professional.
- **IBM Plex Sans** is the utility workhorse, used for labels, navigation items, and code-adjacent metadata. It provides a technical, engineered feel that balances the elegance of the serifs.

Key principle: Large headers should often be center-aligned and use "Display" weights to mimic the look of traditional label printing.

## Layout & Spacing

The layout is a **Fixed-Width Modular Grid**. Content is housed in distinct "cards" or "modals" that sit atop the textured base layer.

- **Global Navigation:** A fixed top navbar with a height of 72px. It is semi-transparent with a heavy backdrop blur (20px) and a thin 1px bottom border in Primary color at 20% opacity.
- **Content Max-Width:** Content is centered at 1200px to maintain a focused, readable column that doesn't stretch on ultra-wide monitors.
- **Spacing Rhythm:** Based on an 8px scale. Cards use a 48px internal padding on desktop and 24px on mobile to create a sense of luxury and space.
- **Mobile Reflow:** On mobile, the 12-column grid collapses to a single column, and margins shrink to 16px. The top navbar becomes a condensed version with a hamburger menu for secondary actions.

## Elevation & Depth

Hierarchy is achieved through **Framed Layering** rather than heavy shadows.

- **Base Layer:** A dark, textured surface (#111) with a subtle noise overlay.
- **Modal Layer:** Cards use a slightly lighter neutral (#1A1A1A) with a crisp 1px border. The border is a gradient: Primary (#F4EBD0) at 30% opacity on the top-left, fading to 10% on the bottom-right.
- **Active Elevation:** When an element is focused or elevated, it does not use a shadow; instead, it gains a "double border" effect or a subtle inner glow (Primary color at 5% opacity).
- **Backdrop Blurs:** Used exclusively for fixed elements (Navbar, Modals) to maintain a sense of the texture underneath while ensuring legibility.

## Shapes

The shape language is **Structured and Geometric**. 

The design system uses a "Soft" roundedness (0.25rem / 4px) to take the edge off the industrial look without becoming "bubbly." This mimics the slightly eased edges of high-quality leather goods or machined metal parts.

- **Cards:** 4px radius.
- **Buttons:** 2px radius (near-sharp) for a more serious, precise feel.
- **Icons:** Should use a "thin" or "light" stroke weight (1.5px) to match the elegant typography.

## Components

### Buttons
- **Primary:** High-contrast box with a 1px border (#F4EBD0). Background is transparent or a dark tint. On hover, the background fills with Primary color and text flips to Neutral.
- **Secondary (Accent):** Uses the framework blue (#5F84BD) as a subtle underline or a left-side accent bar.

### Cards
- Designed as "Modals." They must have a clearly defined header area separated by a thin horizontal rule. Backgrounds can occasionally use a "Parchment" texture (Light) for high-importance callouts, reversing the color scheme (Dark text on Light background).

### Input Fields
- Underlined style or 1px border. The label (IBM Plex Sans) sits above the field in a smaller, all-caps format. The focus state is signaled by the border transitioning to the Secondary Blue.

### Navigation
- The top navbar is the primary anchor. Links are IBM Plex Sans, uppercase, with 2px letter spacing. The active link is denoted by a small brass dot (#D4AF37) underneath the text.

### Lists
- Separated by elegant 1px dividers. Each list item should have ample vertical padding (16px+) to maintain the "premium" spaciousness of the brand.