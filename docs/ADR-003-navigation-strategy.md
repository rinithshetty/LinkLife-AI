# ADR-003: String-route Navigation Compose over type-safe nav args (for v1)

## Status
Accepted (revisit before scaling arg complexity)

## Context
Navigation Compose 2.8+ supports fully type-safe routes via `@Serializable` route objects.
LifeLink AI v1 has a small number of routes, most with zero or one simple String argument
(`GuideDetail(guideId: String)`).

## Decision
Use plain string routes via a `sealed class Destination(val route: String)` with manual
`NavType` argument declarations (see `Destinations.kt`), rather than the newer type-safe
API.

## Consequences
**Positive:** Simpler to read for a small route set; no extra `kotlinx-serialization`
dependency needed just for navigation.

**Negative / tradeoffs:** No compile-time safety on route argument names/types — a typo in
`"guideId"` between `Destinations.kt` and `LifeLinkNavHost.kt` would only be caught at
runtime. **Revisit this ADR** if the route count or argument complexity grows
significantly (e.g. passing structured objects between screens) — migrate to type-safe
routes at that point rather than retrofitting many string-route call sites later.
