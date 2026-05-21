# btw... — CLAUDE.md

## What this app is

**btw...** ("because they would") is a passive safety Android app that quietly reminds you to check for passengers — children, pets — you may have left in your vehicle. No cloud, no account, no ads. Everything runs on-device.

- Package: `com.tonedefapps.btw`
- Company: tonedefapps.com (Scott Fowler)
- Domain: becausetheywould.app
- Keystore: `/Users/scottfowler/btw-release.jks` (credentials in `local.properties`)

---

## Tech stack

| Layer | Library / Version |
|---|---|
| Language | Kotlin 1.9.21 |
| UI | Jetpack Compose BOM 2024.06.00 + Material3 |
| DI | Hilt 2.51.1 |
| Database | Room 2.6.1 + SQLCipher 4.5.4 (encrypted) |
| Preferences | DataStore 1.1.1 |
| Background | WorkManager 2.9.1 + Foreground Service |
| Navigation | Navigation Compose 2.7.7 |
| Location | Fused Location Provider 21.3.0 |
| Billing | Google Play Billing 7.0.0 |
| AGP | 8.3.2 |

- Min SDK 26 (Android 8.0), Target SDK 35, Compile SDK 35, JVM target 17
- Current version: 1.5 (versionCode 7) — last closed testing release was 1.2 (versionCode 4)

---

## Brand

### Voice & tone
- Always lowercase copy. Quiet, warm, never alarming in language.
- The nudge is "a friend leaning over" — never a siren, never a lecture.
- Privacy pillars: **no cloud · no account · no data · no ads**

### App name rules
- Always written: `btw...` — lowercase, trailing ellipsis, never "BTW" or "Btw"
- The ellipsis is always rendered in `Sky` (#7BB8D4); "btw" in `Air` (#EAF4FB)

### Color palette
```kotlin
val Ink   = Color(0xFF1C4A60)  // primary background (dark teal)
val Depth = Color(0xFF3A85A8)  // interactive / secondary
val Sky   = Color(0xFF7BB8D4)  // accent, borders, sublabels, ellipsis
val Air   = Color(0xFFEAF4FB)  // primary text on dark
val Sand  = Color(0xFFC9B49A)  // rider/pet names
val Dawn  = Color(0xFFF0E9E1)  // warm light surface

val AlertRed  = Color(0xFFE05555)
val SafeGreen = Color(0xFF4CAF82)
val WarnAmber = Color(0xFFF5A623)
```
Source: `ui/theme/Color.kt`

### Typography
- Primary font: DM Sans (`DmSans` fontFamily in theme)
- Body/UI: Inter via Google Fonts

---

## How the app works

### Triple-trigger (all three must be true before any alert fires)
1. **Bluetooth disconnect** — phone loses the paired vehicle's BT signal
2. **GPS displacement** — user has moved >15.24 m (50 ft) from where the car is parked
3. **Accelerometer** — motion magnitude is below driving threshold (prevents false triggers while still in a moving car)

### Escalation ladder (AlertEscalationWorker)
| Step | Delay | Action |
|---|---|---|
| 1 | 30 s after disconnect | Gentle notification with quick-dismiss actions |
| 2 | Configurable (step2DelaySeconds) | Persistent full-screen alarm notification |
| 3 | Configurable (step3DelaySeconds) — **Premium only** | SMS to emergency contact |

- **Hot Day Mode** halves all delay timers (0.5× multiplier)
- Final action is user-configurable: `RESEND_SMS` or `REPEAT_ALERT`

### Acknowledgement actions
- `ACTION_ACKNOWLEDGE_SAFE` — everyone's out, cancel escalation
- `ACTION_ACKNOWLEDGE_GOING_BACK` — user is returning to vehicle

---

## Freemium model

| Tier | Features |
|---|---|
| Free | BT + motion detection, gentle notification, persistent alarm, hot day mode |
| Premium | SMS emergency contact, handoff/proxy pickup alerts, configurable escalation timers |

### Product IDs (Google Play)
- `btw_premium_monthly` — $0.99/mo subscription
- `btw_premium_yearly` — $9.99/yr subscription
- `btw_premium_lifetime` — $14.99 one-time IAP

Billing is managed entirely through Google Play — no server, no receipt validation endpoint.

---

## Privacy architecture

- **No INTERNET permission** — intentionally omitted from manifest
- `allowBackup="false"` and `fullBackupContent="false"` — no Android cloud backup
- Room database encrypted with SQLCipher
- `network_security_config.xml` blocks all cleartext/network traffic as a defense-in-depth measure
- No analytics, no crash reporting, no remote logging
- `RECEIVE_SMS` intentionally absent — removed for Play Store permissions review; `SmsVerificationReceiver.kt` is dormant, re-enable in v1.x once account is established

Do not add any networking, analytics, or cloud dependency without explicit approval. This is a hard constraint, not a preference.

---

## Navigation structure

```
Onboarding (4 pages)
  └─ PairVehicle → AddRider → AlertPrefs → SetupComplete → Home

Bottom nav tabs:
  Home | Riders | Settings

Settings leaf screens:
  Vehicles · Locations · Handoff (per riderId) · History · Paywall
```

---

## Key source files

| File | Purpose |
|---|---|
| `service/BtwMonitorService.kt` | Core foreground service — BT receiver, location, accelerometer, triple-trigger |
| `service/AlertEscalationWorker.kt` | WorkManager worker — notification escalation ladder + SMS |
| `service/HandoffMonitorWorker.kt` | Handoff / proxy pickup detection |
| `service/BootReceiver.kt` | Restarts monitor after device reboot |
| `service/SmsVerificationReceiver.kt` | Receives SMS replies for verification flow |
| `data/billing/BillingManager.kt` | Google Play Billing — purchase + restore |
| `data/local/BtwDatabase.kt` | Room database (SQLCipher encrypted) |
| `data/preferences/BtwPreferences.kt` | DataStore preferences |
| `ui/theme/Color.kt` | Brand palette |
| `ui/theme/Theme.kt` | Material3 theme |
| `ui/theme/BtwComponents.kt` | Shared design-system composables |
| `ui/navigation/BtwNavGraph.kt` | Nav graph + bottom bar |
| `ui/home/HomeScreen.kt` | Main screen — Idle / Watching / Alert / Safe states |
| `ui/onboarding/OnboardingScreen.kt` | 4-page onboarding + premium explainer |

---

## Build

```bash
# Debug
./gradlew assembleDebug

# Release (requires local.properties with keystore config)
./gradlew assembleRelease
# or
./build.sh

# local.properties keys needed for release:
# KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
```

---

## Conventions

- All UI text is lowercase unless it's a proper noun (rider's name, vehicle name)
- Rider/pet names are always rendered in `Sand` color
- Status pills use `BtwStatusPill()` from `BtwComponents.kt`
- Cards use `BtwCard {}` — never raw `Card()`
- Hilt injection everywhere — no manual instantiation of repositories or use cases
- No mocking in tests — the app has no network layer to mock
- New screens go in `ui/<feature>/` with a matching ViewModel
