# Contributing to LifeLink AI

This project follows conventional, story-like commit history and a strict module-boundary
rule (see ADR-001) — please keep both intact even for small changes.

## Commit conventions

Prefix every commit with one of:
- `feat:` — a new feature or capability
- `fix:` — a bug fix
- `chore:` — tooling, deps, config, non-source changes
- `docs:` — documentation only
- `test:` — adding or fixing tests
- `refactor:` — code change that doesn't change behavior
- `ci:` — CI/CD pipeline changes

Example: `feat(sos): add hold-to-confirm arming state to SosViewModel`

Each commit should build on its own — avoid bundling unrelated changes into one commit.

## Branch naming

`feature/<short-description>`, `fix/<short-description>`, `chore/<short-description>`.

## Module boundaries (enforced, not just convention)

- `:app` contains only the NavHost, DI entry point, and top-level composition. No ViewModels, no repositories, no screens.
- `feature:*` modules never depend on each other. Shared UI goes in `core:ui`; shared logic goes in `core:common`.
- `feature:*` modules depend on `data:repository` interfaces only — never on `data:local` or `data:remote` directly.
- Every repository method returns `LifeLinkResult<T>`; ViewModels expose `StateFlow` derived from it.

## Before opening a PR

1. `./gradlew ktlintCheck` — must pass.
2. `./gradlew testDebugUnitTest` — must pass.
3. New repository/ViewModel logic should have unit test coverage (see `data/repository/src/test` for the established pattern: mock DAOs/remote sources, verify business logic in isolation).
4. If you touched a screen, a quick manual check with **Accessibility Mode** (large text) and **dark mode** on is expected — these are first-class, not afterthoughts (see `core:ui/theme/Theme.kt`).

## Local setup

See the "Getting started" section in [`README.md`](README.md) for Firebase and Gemini API key setup.

## Issue templates & PR template

See `.github/ISSUE_TEMPLATE/` and `.github/PULL_REQUEST_TEMPLATE.md`.
