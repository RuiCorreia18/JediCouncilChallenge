# Jedi Council Challenge — Star Wars Browser

Android take-home challenge for Axians. Browse Star Wars characters powered
by [SWAPI](https://sw.simplr.sh/).

---

## Architecture Decisions

### 1. In-memory character list — no re-fetch on detail

The SWAPI `/api/people/all.json` endpoint returns full character objects, identical in shape to the
per-id endpoint. Characters are fetched once on startup and held in memory; the detail screen reads
from this in-memory source instead of re-fetching.

Re-fetching would be a redundant network round-trip with no data-quality benefit — SWAPI is static
reference data. The `CharacterRepository` interface absorbs the decision: if the API ever moved to a
summary/detail split, only `getCharacter(id)` would change its implementation. Use cases and
ViewModels stay identical.

### 2. Lazy + cached planets and starships

Planets and starships are not included in the character list payload — only their IDs are. They are
lazy-fetched when a detail screen opens and cached in memory at the repository level for the
session.

Many characters share homeworlds (Tatooine alone hosts 9+ characters), so caching avoids redundant
requests. For production, the same repository interface would be backed by Room for cross-session
persistence.

### 3. Typed errors via `Result<T, E>` + `DataError`

All data-layer calls return `Result<T, DataError.Network>` instead of throwing. The ViewModel
handles success and error explicitly, mapping `DataError` to `UiText` for display. This forces
exhaustive handling at every call site and centralises error-to-message translation in one place,
with no scattered try/catch.

### 4. MVVM with reactive state

ViewModels expose `StateFlow<State>` for UI state and one-shot `Channel`s for navigation events.
Favourites and theme are reactive Flows — toggling from any screen instantly reflects everywhere
without manual refresh.

### 5. Clean Architecture via package structure — single module

Layer separation (`presentation → domain ← data`) is enforced by package structure rather than
separate Gradle modules. The challenge scope doesn't justify multi-module overhead, but the
dependency rules are honoured: the domain layer is pure Kotlin with zero Android imports.

### 6. Use cases for orchestration only

Use cases earn their place when they orchestrate concurrent work or enforce business rules:

- `GetCharactersUseCase` fetches characters + species in parallel using `async/await`.
- `GetCharacterDetailUseCase` fetches homeworld + starships in parallel with early-exit on error.

Trivial passthroughs (observing favourites, observing theme) go through the repository directly.
Adding a passthrough use-case layer there would be ceremony, not architecture.

### 7. Virtual pagination with infinite scroll — no state drift

The character list stores only two inputs: `visibleCount` and the active filters. The displayed list
is derived from these inputs on every state change rather than being maintained as a separate
accumulating list. This eliminates the class of bugs where the displayed list and the filter inputs
become inconsistent.

Pagination is triggered automatically via a `snapshotFlow` over `LazyListState` — when the last
visible item is within 3 positions of the end, `loadMore()` fires. A spinner appears at the bottom
while the next page is appended. No "Load more" button is needed.

---

## Tech Stack

| Concern       | Library                                              |
|---------------|------------------------------------------------------|
| Language      | Kotlin                                               |
| UI            | Jetpack Compose + Material3                          |
| DI            | Hilt 2.59                                            |
| Networking    | Retrofit 2.11.0 + kotlinx.serialization              |
| Image loading | Coil (`AsyncImage`)                                  |
| Navigation    | Compose Navigation 2.8.9 (type-safe routes)          |
| Async         | Coroutines + StateFlow + Flow                        |
| Local storage | DataStore Preferences                                |
| Architecture  | Clean Architecture + MVVM                            |
| Testing       | JUnit5, Turbine, AssertK, `UnconfinedTestDispatcher` |

---

Character portraits are loaded by SWAPI people ID from
`https://vieraboschkova.github.io/swapi-gallery/static/assets/img/people/{id}.jpg`.

## Screens

| Screen           | Description                                                                                                                                                                                     |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Character List   | Image-forward list with search, species/gender filters, A-Z/Z-A/oldest/newest sort, infinite scroll, favourites                                                                                 |
| Character Detail | Lazy-fetched homeworld (name, climate, terrain, population) + starships (name, model, manufacturer, class), cached. Favourite toggle. Back button overlaid on the character image — no top bar. |
| Favourites       | Reactive — reflects toggle changes from any screen instantly                                                                                                                                    |
| Compare          | Two searchable slots, side-by-side biography and stats comparison                                                                                                                               |

Navigation drawer gives access to Compare, Favourites, and Darth Vader dark mode. The detail screen
hides the navigation drawer entirely — the back button is embedded in the card image.

## Verification

- `./gradlew :app:compileDebugKotlin`
- `./gradlew :app:testDebugUnitTest`

Current unit coverage: 33 tests — mapper tests (CharacterMapper, PlanetMapper, StarshipMapper,
SpeciesMapper, UrlIdMapper) plus ViewModel tests for Character List, Character Detail, Favourites,
and Compare, all using fake repositories rather than mocks.
