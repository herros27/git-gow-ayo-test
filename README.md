# TV App — Intern Mobile Engineer Test

A simple TV show browser app built with **Kotlin + Jetpack Compose**, consuming the [TVMaze API](https://www.tvmaze.com/api).

---

## How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/herros27/git-gow-ayo-test.git
   ```

2. **Open in Android Studio** (Hedgehog or later recommended)

3. **Sync Gradle** — Android Studio will resolve all dependencies automatically

4. **Run the app** on an emulator or physical device (min SDK 24 / Android 7.0)

> No API key required. The TVMaze API is fully public.

---

## Features

| Feature | Status |
|---|---|
| TV Show List with Paging 3 | ✅ |
| Loading / Error (with retry) / Success states | ✅ |
| TV Show Detail (poster, title, summary, premiere date) | ✅ |
| Cast, Seasons & Episodes on Detail screen | ✅ |
| Share TV show from Detail screen | ✅ |
| HTML tag handling in summary | ✅ |
| Unit Tests (ViewModel + Repository + PagingSource) | ✅ |

---

## Architecture

This app follows **MVVM + Clean Architecture** principles, organized into three layers:

```
app/
├── data/
│   ├── remote/
│   │   ├── TvMazeApi.kt          # Retrofit interface (shows, cast, seasons, episodes)
│   │   └── dto/                  # JSON response models (TvShowDto, CastDto, SeasonDto, EpisodeDto)
│   ├── paging/
│   │   └── TvShowPagingSource.kt # Paging 3 source backed by TVMaze page-based API
│   └── repository/
│       └── TvShowRepository.kt   # Single source of truth; maps DTOs → domain models
│
├── domain/
│   └── model/                    # Pure Kotlin data classes (TvShow, Cast, Season, Episode)
│
├── presentation/
│   ├── list/
│   │   ├── TvShowListViewModel.kt   # Exposes PagingData<TvShow> flow cached in viewModelScope
│   │   └── TvShowListScreen.kt      # LazyVerticalGrid consuming LazyPagingItems
│   ├── detail/
│   │   ├── TvShowDetailViewModel.kt # Fetches show + cast + seasons + episodes in parallel (async)
│   │   ├── TvShowDetailUiState.kt   # Sealed interface: Loading | Success | Error
│   │   └── TvShowDetailScreen.kt
│   └── navigation/
│
├── di/                           # Hilt modules (AppModule)
└── util/                         # Mapper extensions (toDomain()), HtmlUtils
```

### Key Decisions

**DTO ↔ Domain Model separation**
JSON response objects (`*Dto`) are kept isolated in the data layer. Domain models (`TvShow`, `Cast`, etc.) are plain Kotlin data classes with no third-party annotations (no Moshi `@Json`). A `toDomain()` mapper extension function bridges the two. This protects the UI layer from API schema changes and normalizes nullability upfront (e.g. `rating.average` → `Float`, defaulting to `0f`).

**Paging 3 for the List screen**
Instead of loading all ~250+ shows in a single request, `TvShowPagingSource` loads pages on demand. The ViewModel exposes a `Flow<PagingData<TvShow>>` cached with `cachedIn(viewModelScope)` so configuration changes (rotation) do not re-fetch data.

**Parallel fetch on Detail screen**
The detail screen needs show info, cast, seasons, and episodes — four separate API calls. `TvShowDetailViewModel` fires all four concurrently using `async` / `await` inside a single coroutine scope. Total wait time equals the slowest request, not the sum of all four.

**`BASE_URL` via `BuildConfig`**
The TVMaze base URL is injected through `buildConfigField` in `app/build.gradle.kts` and consumed in the Hilt `AppModule`, keeping it out of source files and making environment switching trivial.

---

## Problem-Solving Log

### 1. Force-close on scroll — duplicate Lazy Layout keys

**Symptom:** The app crashed with `IllegalArgumentException: Key "25008" was already used` while scrolling through the TV Show List.

**Root cause:** `LazyVerticalGrid` was using the show's `id` as the item key. During testing, duplicate IDs were observed across paginated responses (e.g., ID `25008` appeared more than once), so the UI cannot safely assume API IDs are globally unique. Jetpack Compose requires every key in a Lazy Layout to be unique; a duplicate key causes an immediate crash.

**Fix:** Combine the ID with the item's index to guarantee uniqueness:
```kotlin
// Before — crashes when the same id appears twice
items(shows, key = { it.id }) { ... }

// After — index makes the key unique even when duplicate IDs appear
itemsIndexed(shows) { index, show ->
    key("${show.id}_$index") { ... }
}
```

**Known trade-off:** Using index as part of the key means the key encodes position, not just identity. If the dataset shifts (e.g., an item is inserted at the top), Compose treats previously-seen items as new because their index changed. The ideal key is a stable, globally unique identifier from the backend. The `id + index` approach is a pragmatic fallback for when the API does not provide one.

The same pattern was applied to the Cast and Seasons lists on the Detail screen, which had the same structural risk.

---

### 2. Redundant double data-fetch on the List screen

**Symptom:** Network profiler showed the show list being fetched twice on every launch.

**Root cause:** The List screen had two independent data-fetching paths wired up simultaneously — a direct `suspend` call in the ViewModel alongside the Paging 3 flow. Each path could return ~250 shows, meaning the same data was fetched and processed twice, wasting both network bandwidth and CPU time.

**Fix:** Removed the redundant direct fetch entirely. Paging 3 already owns the list loading lifecycle — `TvShowPagingSource` handles fetch, retry, and error; `cachedIn(viewModelScope)` survives configuration changes. There is no reason for a second independent fetch to exist alongside it.

---

## What I'd Improve With More Time

- **Offline caching with Room** — cache `PagingData` locally so the list is available without a network connection (Paging 3 + `RemoteMediator`).
- **HTML summary parser with `AnnotatedString`** — currently `HtmlCompat.fromHtml` strips/renders basic tags. A dedicated parser would preserve bold, italic, and links natively inside Compose text.
- **Instrumentation / UI tests** — add Compose UI tests for the list and detail screens, especially the error + retry flow.
- **Episode grouping by season** — currently episodes are displayed as a flat list; grouping them under their respective season headers would improve readability significantly.
- **Dedicated `NetworkResult` wrapper** — replace bare `try/catch`  with an explicit sealed result type to make error propagation explicit and testable across layer boundaries.

---

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, ViewModel, StateFlow |
| Pagination | Paging 3 (`paging-runtime-ktx`, `paging-compose`) |
| Networking | Retrofit 2, Moshi |
| Image loading | Coil |
| DI | Hilt (Dagger) |
| Navigation | Navigation Compose |
| Testing | JUnit 4, MockK, Turbine, kotlinx-coroutines-test |

---

## Walkthrough Video

🎥 *https://youtu.be/wJkb1cWUUX4*
