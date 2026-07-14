# ADR-001: Modular Clean Architecture over a single-module MVVM app

## Status
Accepted

## Context
A single-module MVVM Android app is the fastest way to prototype, and is what most
student/portfolio projects default to. LifeLink AI spans 9+ distinct features (SOS,
Contacts, Vault, Reminders, Guides, AI Assistant, Auth, Location Sharing, Settings) with
very different lifecycles (a Foreground Service for SOS, WorkManager for reminders, a
chat-like UI for the AI assistant). A single-module app makes it easy for these concerns
to bleed into each other — a `ContactsViewModel` that "just quickly" also handles vault
encryption, for example.

## Decision
Split the app into `:app` (composition root only), `core:*` (common/ui/ai — shared,
feature-agnostic), `data:*` (local/remote/repository — the only place data access logic
lives), and `feature:*` (one module per user-facing feature area, depending only on
`core:*` and `data:repository`, never on each other).

## Consequences
**Positive:**
- Each feature module can be built, tested, and reasoned about independently.
- `:app` staying thin makes the module dependency graph itself a form of documentation —
  a reviewer can see feature boundaries just from `settings.gradle.kts`.
- Swapping an implementation (e.g. Gemini → a different LLM provider) touches `core:ai`
  only, never a feature module.

**Negative / tradeoffs:**
- More Gradle module boilerplate than a single-module app (mitigated by the shared
  `libs.versions.toml` version catalog and a consistent `build.gradle.kts` pattern per
  module type).
- Slightly slower initial Gradle sync due to module count — acceptable given the
  parallelization and incremental-build benefits at any nontrivial project size.
