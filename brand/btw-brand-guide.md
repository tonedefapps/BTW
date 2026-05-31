# btw... brand guide

## concept
**btw...** is a gentle nudge app that reminds you when a pet or child is still in the car after you disconnect from bluetooth. The name works on two levels: "by the way" (casual, non-alarming) and "because they would" — as in, they'd remind you if they could. The app speaks on behalf of the ones who can't.

---

## voice & tone
- Always a whisper. Never an alarm.
- The app is a friend leaning over, not a warning system.
- No exclamation marks. Ever.
- The nudge prefix is always: **btw...**
- Notification copy uses the person/pet's name directly: *"btw... Mango still in the car?"*
- Second-level copy leans into the brand story: *"hey... Lily would remind you if she could."*
- Labels use sentence case, lowercase where possible. Nothing shouts.

---

## color system

| name | hex | role |
|------|-----|------|
| ink | `#1C4A60` | primary background, icon bg, dark surfaces |
| depth | `#3A85A8` | interactive elements, buttons, active states |
| sky | `#7BB8D4` | accent, ellipsis color, labels, borders |
| air | `#EAF4FB` | primary text on dark, light surface bg |
| sand | `#C9B49A` | "them" color — always used for the person/pet's name |
| dawn | `#F0E9E1` | warm light surface, secondary backgrounds |

**color logic rule:** sand is reserved exclusively for references to the person or pet being protected. Anywhere their name appears in UI, it renders in sand. This is a meaningful design choice — sand = them.

---

## typography

- **Font:** system humanist sans (SF Pro on iOS, Inter or similar on Android/web)
- **Weight:** 300 (light) for almost everything. 400 for emphasis on names. Never go above 400 in the app UI.
- **The wordmark** is always lowercase, weight 300, with the ellipsis in sky `#7BB8D4`
- No bold headlines. No heavy type. The whole app should feel like it has a low voice.

| role | size | weight | color |
|------|------|--------|-------|
| wordmark hero | 52px | 300 | air `#EAF4FB` |
| screen titles | 22px | 300 | air `#EAF4FB` |
| body / nudge copy | 15px | 300 | air at 75% opacity |
| person/pet names | 15px | 400 | sand `#C9B49A` |
| labels / eyebrows | 12px | 400 | sky `#7BB8D4`, tracked 0.1em, lowercase |

---

## wordmark

```
btw...
```

- Always fully lowercase
- The `...` ellipsis renders in sky (`#7BB8D4`), the `btw` in air (`#EAF4FB`) on dark backgrounds
- On light backgrounds: `btw` in ink (`#1C4A60`), `...` in sky
- Tagline lockup (full expression): `btw...` with `because they would` beneath in sky, tracked

---

## app icon

- Background: ink `#1C4A60`
- Wordmark: `btw...` centered, font-size fills ~80% of the icon area, weight 200–300
- The `...` in sky, `btw` in air
- Corner radius follows platform standard (iOS: continuous superellipse)
- No other graphic elements — the wordmark IS the icon

---

## UI patterns

**nudge card**
- Background: depth at 15% opacity with sky border at 30% opacity
- Label: `btw...` in sky, 10px, tracked, uppercase
- Message: air text, 300 weight, person name in sand

**who's with you chips**
- Active: depth `#3A85A8` background, air text
- Inactive: transparent with sky border, sky text

**buttons**
- Primary: depth `#3A85A8` background, air text, rounded pill
- Secondary: transparent, sky border, sky text
- No heavy corners, no shadows

**status indicator**
- A small sand dot (`#C9B49A`) + sky label text
- e.g. `● connected · watching`

---

## widgets

- Background: ink `#1C4A60`
- Wordmark small, sky ellipsis
- Status line in sand, names in air at 60% opacity
- Border: sky at 25% opacity

---

## what to never do
- Never use exclamation marks
- Never use red or orange for alerts — the app doesn't alarm, it whispers
- Never render the person/pet name in any color other than sand
- Never use font weight above 400
- Never use the words "warning", "alert", "danger" in copy
- Never use shield, radar, wave, ping, or notification iconography
- Never capitalize `btw` — it's always lowercase

---

## tagline
> *because they would*

This is the emotional core of the brand. The app speaks for the ones who can't speak for themselves.
