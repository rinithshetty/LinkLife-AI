# LifeLink AI — Product Requirements Document (PRD)

This file is the canonical PRD referenced by the README and ADRs. See the full PRD content
delivered earlier in this project's planning conversation for the complete functional and
non-functional requirements (FR-1 through FR-9, acceptance criteria, and risks table).

Summary of Tier 1 functional requirements implemented in this codebase:
- FR-1: Emergency SOS (hold-to-confirm, foreground service, 5s cancel window)
- FR-2: Emergency Contact Manager
- FR-3: Location Sharing (time-boxed)
- FR-4: Medicine Reminder (WorkManager, reboot-resilient)
- FR-5: Medical Record Vault (AES-256-GCM encrypted at rest)
- FR-6: Offline Emergency Guides (bundled JSON, zero network)
- FR-7: AI Symptom Explainer (Gemini, mandatory disclaimer wrapper)
- FR-8: Accessibility Mode (large text, dark mode)
- FR-9: Auth & Onboarding (Firebase Auth, permission rationale screens)

Each FR number is referenced directly in code comments at its implementation site (e.g.
`SosScreen.kt`, `ReminderNotificationWorker.kt`) so the requirement-to-code mapping is
traceable.
