# Roadmap

## Tier 1 — MVP (implemented in this scaffold)
Emergency SOS, Emergency Contact Manager, Location Sharing, Medicine Reminders, Medical
Record Vault, Offline Emergency Guides, AI Symptom Explainer, Accessibility Mode,
Auth & Onboarding.

## Tier 2 — Next up
- **Medicine OCR Scanner** (CameraX + ML Kit Text Recognition, feeding into
  `GeminiRepository.analyzeOcrText`, which is already defined in `core:ai`).
- **Hospital Locator** (Google Maps SDK + Places API, `maps-compose` dependency already
  in the version catalog).
- **Disaster Alerts** (FCM push, cached last-known alert in Room so it's visible offline
  even after the push is missed).
- **Family Safety Check-in** (extends `CheckInRepository`, already scaffolded in
  `data:repository`, with a multi-user "watch this person" relationship).

## Tier 3 — Roadmap only (documented, not built)
- Blood Donor Finder
- Community Volunteers / Missing Person Bulletin
- Voice Commands
- Multilingual support beyond English (Kannada was considered given the developer's
  regional context, but deferred to keep v1 scope tight)

## Why this tiering exists
Building all ~20 originally-brainstormed features as v1 produces a shallow app with no
feature polished enough to demo well. This tiering is itself a product decision worth
presenting in an interview: knowing what to cut is as valuable a signal as what got built.
