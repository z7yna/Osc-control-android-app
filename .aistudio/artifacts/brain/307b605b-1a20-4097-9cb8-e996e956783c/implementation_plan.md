# OSC Fire Control (Orbital Strike Cannon Mk 6) Android App

A high-performance, native Android application built with Jetpack Compose that accurately converts Minecraft coordinates into precise firing solutions for the Orbital Strike Cannon Mark 6, Mark 6.1 (MS), and Sub-Orbital Firing Cannon (SFC), wrapped in a white liquid glassmorphism aesthetic with glowing red accents.

## User Review & Critical Decisions

> [!IMPORTANT]
> The architectural and UX decisions confirmed during Phase 1 have been directly woven into this specification.

- **Confirmed Decision 1 (Runtime Architecture)**: Pure native Jetpack Compose for buttery-smooth 60/120fps transitions, zero Cordova/Capacitor overhead, complete offline operation, and pixel-perfect rendering.
- **Confirmed Decision 2 (Number Input UX)**: Built-in custom on-screen numeric keypad with dedicated `+/-` sign toggle, decimal support, backspace, and rapid field navigation to eliminate Android keyboard jumping, layout resize stutter, or soft keyboard glitches.
- **Confirmed Decision 3 (Result Visualization)**: High-tech interactive OSC Cannon Alignment & Azimuth Schematic drawn directly via Jetpack Compose Canvas, illustrating vector heading (yaw/azimuth), cannon origin, target vector, elevation offset, and stage timing.
- **Design Language**: White "liquid glass" (glassmorphism) featuring frosted translucent white cards (`rgba(255, 255, 255, 0.12 - 0.22)`), deep backdrop blur, subtle diffusion shadows, and vibrant neon red (`#E94560` / `#FF2A55`) glowing borders and indicators.

---

## 1. Overview & Core Concept

### What It Does
OSC Fire Control calculates the mechanical timing, TNT acceleration ticks, coarse/fine piston counts, sign bits, and binary punch card / LED matrix grid required to hit any Minecraft coordinate using technical redstone Orbital Strike Cannons (Mk6, Mk6 MS, and SFC).

### Target Audience & Persona
Technical Minecraft players, redstone engineers, and faction/anarchy cannon operators who need instantaneous, reliable, and mathematically flawless firing parameters on their mobile devices without needing internet access or messy spreadsheets.

### Key Value
- **Zero Calculation Discrepancies**: 100% faithful port of the exact trigonometric and redstone physics equations from `cubicmetre/osc-fire-control`.
- **Frictionless Coordinate Entry**: Native custom keypad with `+/-` toggle handles negative coordinates effortlessly without layout shifts.
- **Mission Control UX**: Clean two-step flow (Input Screen → Animated Transition → Results Screen with Schematic and Binary Matrix) with state persistence across app launches.

---

## 2. User Experience & Visual Design

### Key User Flows

```
┌────────────────────────────────────────────────────────────────────────┐
│                          STEP 1: LAUNCH & INPUT                        │
│                                                                        │
│  [ Header: ⛯ OSC FIRE CONTROL ]             [ Preset: Default / Slot ]│
│                                                                        │
│  ┌─ Cannon Origin Config ───────────────────────────────────────────┐  │
│  │  Variant: [ OSC Mk 6 ▼ ]    Passcode: [ 940 ]                    │  │
│  │  Origin:  X [   0 ]    Y [  64 ]    Z [   0 ]                    │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                        │
│  ┌─ Target Coordinates ─────────────────────────────────────────────┐  │
│  │  Name: "Alpha Outpost"      Mode: [ Nuke ● ] [ Stab ○ ]           │  │
│  │  Target:  X [ 1520 ]   Y [  68 ]    Z [ -3400 ]                  │  │
│  │  Payload: [ 10 ] TNT        Mag Slot: [ 0 ]                      │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                        │
│  ┌─ Custom In-App Glass Keypad ─────────────────────────────────────┐  │
│  │   [ 1 ] [ 2 ] [ 3 ]   [  + / -  ]                                │  │
│  │   [ 4 ] [ 5 ] [ 6 ]   [   ⌫     ]                                │  │
│  │   [ 7 ] [ 8 ] [ 9 ]   [  Prev   ]                                │  │
│  │   [ . ] [ 0 ] [ C ]   [  Next   ]                                │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                        │
│  [ ⚡ CALCULATE FIRING SOLUTION ]                                       │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │ Taps "Calc"
                                   ▼ Animated Slide & Morph Transition
┌────────────────────────────────────────────────────────────────────────┐
│                        STEP 2: RESULTS & SCHEMATIC                     │
│                                                                        │
│  [ ← NEW CALCULATION ]                     [ 📋 COPY SOLUTION ]         │
│                                                                        │
│  ┌─ Cannon Alignment & Azimuth Schematic (Canvas) ──────────────────┐  │
│  │                N (0°)                                            │  │
│  │                  │                                               │  │
│  │       W ─────────┼───────── E         Target: ΔX:+1520 ΔZ:-3400  │  │
│  │            \     │                    Azimuth: 155.9°            │  │
│  │             \    │                    Distance: 3,724 blocks     │  │
│  │              ● (Cannon)                                          │  │
│  │                \                                                 │  │
│  │                 ▼ (Trajectory Vector)                            │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                        │
│  ┌─ Primary Firing Solution ────────────────────────────────────────┐  │
│  │  Coarse Adjustment:  X: 194    Y: 1     Z: 432                   │  │
│  │  Fine Adjustment:    X: 7      Y: 3     Z: 4                     │  │
│  │  Total Flight Time:  00:03:42  (Prep: 00:01:12 | Accel: 00:02:30)│  │
│  │  Nether Equivalent:  X: 190    Y: 64    Z: -425                  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                        │
│  ┌─ Binary Punch Card Matrix (5x13 LED Display) ────────────────────┐  │
│  │  ● ○ ● ● ○ ○ ● ○ ● ● ○ ● ○                                       │  │
│  │  ○ ● ● ○ ● ○ ● ○ ○ ● ● ○ ●    [Interactive LED Bulb Grid with    │  │
│  │  ● ● ○ ● ○ ● ○ ● ● ○ ● ○ ●     Minecraft Signal Glow Overlay]   │  │
│  │  ○ ○ ● ● ● ○ ● ○ ● ● ○ ● ○                                       │  │
│  │  ● ● ○ ○ ● ● ○ ● ○ ○ ● ● ●                                       │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

### Visual Identity & Theme
- **Background**: High-resolution Minecraft orbital panorama background asset with dark atmospheric gradient vignette.
- **Glassmorphism Panels**: Frosted white translucent backgrounds (`Color(0x1EFFFFFF)` to `Color(0x28FFFFFF)`), blurred backdrop filters, 16dp rounded corners, and soft elevation drop-shadows.
- **Red Neon Edges**: Dynamic 1dp border with red gradient (`#FF2A55` to `#E94560`), with pulsating neon glow effects on active inputs and trigger buttons.
- **LED Binary Display**: Pixel-authentic 5x13 punch-card matrix with custom redstone torch glow shaders and color-coded boundary headers matching the original spreadsheet.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Pure Native Jetpack Compose vs Hybrid WebView
- **Chosen Approach**: Native Jetpack Compose architecture.
- **Why**: Eliminates blank-screen Vite base-path bugs, guarantees 100% offline stability, provides zero touch latency on coordinate entry, and allows direct hardware-accelerated Canvas drawing for the cannon schematic.

### Decision 2: Custom In-App Keypad with Negative Sign Toggle
- **Chosen Approach**: A sleek, bottom-pinned glass keypad with dedicated `+/-`, `⌫`, `Clear`, and field navigation buttons.
- **Why**: Solves the notorious mobile web issue where decimal keyboards on Android lack negative signs or trigger jarring viewport resizing and zoom bugs.

### Decision 3: Math Consistency & Exact Parity
- **Chosen Approach**: Exact 1:1 mathematical port of `DV_PLUS_X`, `DV_PLUS_Y`, `DV_PLUS_Z`, `DV_MINUS_X`, `DV_MINUS_Z`, `DV_YX`, `slowdownXZ`, `slowdownY`, and bit extraction formulas.
- **Why**: Zero risk of TNT misfires or miscalculated trajectories.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                            UI LAYER                                  │
│                                                                      │
│    MainActivity (Edge-to-Edge, Theme, System Bars Aware)             │
│       │                                                              │
│       ├── MainScaffold (Panorama Background + Ambient Vignette)      │
│       │                                                              │
│       ├── InputScreen (Cannon Config, Target Cards, Quick Keypad)    │
│       │                                                              │
│       ├── ResultsScreen (Azimuth Schematic, Firing Data, LED Grid)   │
│       │                                                              │
│       └── CustomKeypadComponent (Zero-jitter Numeric & Sign Engine)  │
└──────────────────────────────────┬───────────────────────────────────┘
                                   │ Observes State & Fires Intents
                                   ▼
┌──────────────────────────────────────────────────────────────────────┐
│                         VIEWMODEL & DOMAIN                           │
│                                                                      │
│    FireControlViewModel                                              │
│       ├── UIState (Screen mode, Coordinates, Selected Field, Result) │
│       ├── OscCalculatorEngine (Exact Mk6 / Mk6 MS / SFC formulas)    │
│       ├── TargetValidator (Divisible by 16, Range, Passcodes)        │
│       └── PreferencesRepository (SharedPreferences / Last Values)    │
└──────────────────────────────────────────────────────────────────────┘
```

### Component Breakdown
1. **`OscCalculator.kt`**: Pure Kotlin math engine implementing `calculate()`, `calculateSfc()`, `generateBinaryGrid()`, and `overworldToNether()`.
2. **`ValidPasscodes.kt`**: Constant set of the 222 verified safe Minecraft passcodes.
3. **`OscSchematicCanvas.kt`**: Custom Compose `Canvas` drawing the radar compass, cannon origin, flight path vector, target circle, distance, and azimuth heading angle.
4. **`LedMatrixDisplay.kt`**: 5-row by 13-column LED binary grid with redstone pulse styling.
5. **`GlassKeypad.kt`**: Responsive floating glass numpad with dedicated `+/-`, backspace, and active input indicator.
6. **`AppIcon` & Adaptive Drawables**: High-tech vector icon with radar reticle and red beam matching the OSC Fire Control identity.

### State & Persistence
- Automatically caches the user's last cannon origin coordinates, target coordinates, fire mode, and passcode in local Android SharedPreferences so state is restored across app re-opens.
