# LifeLink AI

**An offline-first, AI-assisted Android app for personal safety, medical emergencies, and disaster preparedness.**

LifeLink AI helps people prepare for, survive, and recover from medical emergencies and natural disasters — even with no internet connection. Most safety apps assume constant connectivity and a calm user; LifeLink AI is built around the opposite assumption: degraded networks, panic, and urgency.

---

## Why this exists

In a real emergency — an earthquake, a flood, a medical crisis — you cannot rely on:
- Always-on internet (cell towers go down in disasters)
- Calm, deliberate typing (panic changes how people interact with a phone)
- A single-purpose app (SOS-only apps don't help you manage medication; medical apps don't help you find shelter)

LifeLink AI is a single, offline-first companion that spans personal safety, medical records, and disaster response.

---

## Feature scope (v1 / MVP)

| Feature | Status |
|---|---|
| Emergency SOS (hold-to-confirm, foreground service, cancel window) | ✅ Implemented |
| Emergency Contact Manager | ✅ Implemented |
| Location Sharing (time-boxed) | ✅ Implemented |
| Medicine Reminders (WorkManager, survives reboot) | ✅ Implemented |
| Medical Record Vault (AES-256 encrypted at rest) | ✅ Implemented |
| Offline Emergency Guides (earthquake / flood / fire, zero network) | ✅ Implemented |
| AI Symptom Explainer (Gemini, always disclaimer-wrapped) | ✅ Implemented |
| Accessibility Mode (large text, dark mode) | ✅ Implemented |
| Auth & Onboarding (Firebase Auth, permission-rationale screens) | ✅ Implemented |
| Medicine OCR Scanner, Hospital Locator, Disaster Alerts | 📋 Roadmap (Tier 2) |
| Blood Donor Finder, Community Volunteers, Voice Commands | 📋 Roadmap (Tier 3) |

See [`docs/PRD.md`](docs/PRD.md) for full functional requirements and acceptance criteria, and [`docs/ROADMAP.md`](docs/ROADMAP.md) for what's deliberately deferred and why.

---

## Architecture

Modular Clean Architecture, MVVM, offline-first by construction (Room is the single source of truth; Firestore syncs on top of it, never the other way around).

```
LifeLinkAI/
├── app/                  → Composition root only: NavHost + DI entry point. No business logic.
├── core/
│   ├── common/           → Result wrapper, DispatcherProvider, base UseCases
│   ├── ui/                → Design system: Material3 theme, tokens, shared composables
│   └── ai/                → Gemini API contract + safety wrapper (mandatory disclaimers)
├── data/
│   ├── local/             → Room database, entities, DAOs (offline source of truth)
│   ├── remote/            → Firestore + Firebase Auth wrappers
│   └── repository/        → Repository implementations, offline-first sync, encryption
└── feature/
    ├── onboarding/         → Auth + permission-rationale onboarding
    ├── sos/                → SOS, Emergency Contacts, Location Sharing
    ├── medical/            → Medical Vault, Medicine Reminders
    ├── guides/             → Offline Emergency Guides
    └── assistant/          → AI Symptom Explainer
```

**Module dependency direction is one-way and enforced by Gradle:**
`feature:* → data:repository → data:local / data:remote`, and `feature:* → core:ui, core:common`. No feature module depends on another feature module, and `:app` never contains a ViewModel or a screen of its own — every screen lives in its feature module.

Full rationale: [`docs/ADR-001-modular-architecture.md`](docs/ADR-001-modular-architecture.md).

### Offline-first data flow

Every Tier-1 feature (SOS, Contacts, Vault, Reminders, Guides) is fully functional in airplane mode. Writes go to Room immediately (`isSynced = false`), and a WorkManager-scheduled `SyncWorker` pushes anything unsynced to Firestore opportunistically, using last-write-wins conflict resolution on `updatedAt`. Sync failures are never surfaced as user-facing errors — they're a background "deferred" state that resolves silently once connectivity returns.

### Security

Medical Vault content is encrypted with AES-256-GCM via the Android Keystore (`VaultCipher.kt`) before it ever touches Room or Firestore — plaintext medical data never hits disk. See [`docs/ADR-002-vault-encryption.md`](docs/ADR-002-vault-encryption.md).

### AI safety

Every AI response (symptom explanations, OCR analysis, disaster instructions) is wrapped by `MedicalSafetyWrapper` with a mandatory disclaimer and coarse input sanitization before it's ever shown to a user or returned from `core:ai`. This is enforced structurally — there is no code path in the app that can show a raw, undisclaimed Gemini response.

---

## Tech stack

Kotlin · Jetpack Compose · Material 3 · MVVM · Clean Architecture · Hilt · Room · DataStore · Retrofit · Coroutines/Flow · Navigation Compose · WorkManager · Foreground Services · Google Maps SDK · CameraX · ML Kit OCR (Tier 2) · Firebase (Auth, Firestore, Storage, Messaging, Crashlytics, Analytics) · JUnit · Mockito · Turbine · Compose UI Testing · GitHub Actions CI/CD.

---

## Getting started

### Prerequisites
- Android Studio (latest stable — Koala or newer recommended)
- JDK 17
- An Android device or emulator running API 26+

### 1. Clone and open
Open the project root in Android Studio. Let it sync — this downloads all Gradle/Compose/Firebase dependencies, which requires internet access on first sync.

### 2. Create `local.properties`
Rename `local.properties.template` (in the project root) to `local.properties`, and set `sdk.dir` to your actual Android SDK path (see the examples inside the file for Windows/macOS/Linux). Android Studio sometimes creates this file automatically on first sync — if it already exists, just add your SDK path and Gemini key to it instead of using the template.

### 2. Firebase setup
1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com).
2. Add an Android app with package name `com.lifelink.app`.
3. Download the real `google-services.json` and place it at `app/google-services.json` (this path is gitignored — never commit your real file).
4. Enable Email/Password auth, Firestore, and Storage in the Firebase console.

A non-functional placeholder (`app/google-services.sample.json`) is included so CI builds succeed without real credentials — copy it to `app/google-services.json` if you just want the project to compile without setting up Firebase yet.

### 3. Gemini API key
1. Get a Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey).
2. Create `local.properties` in the project root if it doesn't already exist (Android Studio usually creates it) and add:
   ```
   GEMINI_API_KEY=your_key_here
   ```
   This file is gitignored — your key is never committed. `core:ai/build.gradle.kts` reads it into `BuildConfig.GEMINI_API_KEY` at build time.

### 4. Run
Select the `app` run configuration and run on a device/emulator with Google Play Services (required for FusedLocationProviderClient and Firebase).

---

## Testing

- **Unit tests**: `./gradlew testDebugUnitTest` — repository and ViewModel logic, tested against mocked DAOs/remote sources (Mockito + Turbine + Truth).
- **Instrumented tests**: `./gradlew connectedDebugAndroidTest` — Compose UI tests, run on a device/emulator.
- **Lint**: `./gradlew ktlintCheck`.
- CI runs all three on every push/PR — see `.github/workflows/ci.yml`.

---

## Documentation

- [`docs/PDR.md`](docs/PDR.md) — Product Design Requirements
- [`docs/PRD.md`](docs/PRD.md) — Product Requirements Document (functional + non-functional requirements, acceptance criteria)
- [`docs/ROADMAP.md`](docs/ROADMAP.md) — Tier 2/3 features deliberately deferred, and why
- [`docs/ADR-001-modular-architecture.md`](docs/ADR-001-modular-architecture.md)
- [`docs/ADR-002-vault-encryption.md`](docs/ADR-002-vault-encryption.md)
- [`docs/ADR-003-navigation-strategy.md`](docs/ADR-003-navigation-strategy.md)
- [`CONTRIBUTING.md`](CONTRIBUTING.md) — commit conventions, branch naming, local setup

---

## License

MIT — see [`LICENSE`](LICENSE).
