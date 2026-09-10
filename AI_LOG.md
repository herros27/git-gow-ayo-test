# AI Usage Log

This document records the interactions, problem-solving discussions, and technical verifications conducted with AI assistance during the development of the TV App.

---

### Entry 1: Resolving AGP 9.2.1 Built-In Kotlin SourceSet Conflict
1. **What I asked / problem solving**:  
   Encountered a Gradle build error when setting up KSP and Hilt: `Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin. Solution: Use android.sourceSets DSL instead.`
2. **What it gave me**:  
   The AI identified that Android Gradle Plugin 9.2.1 strictly enforces the new built-in Kotlin model and provided the workaround property `android.disallowKotlinSourceSets=false` to add to `gradle.properties`.
3. **What I did**:  
   Accepted as-is and added the flag to `gradle.properties`.
4. **One thing the AI got wrong**:  
   The AI presented `android.disallowKotlinSourceSets=false` as the fix without mentioning it is a temporary suppressor, not a proper migration path. It implied this was a clean solution. I checked the official AGP 9.x release notes myself and found this flag is explicitly labeled as a migration escape hatch to be removed once annotation processors fully adopt the new `android.sourceSets` DSL — meaning this flag will eventually be removed and will break again in a future AGP update. The AI should have flagged this upfront so I could track it as known technical debt.

---

### Entry 2: Fixing Hilt Runtime vs. Compiler Version Mismatch
1. **What I asked / problem solving**:  
   Encountered compilation error in generated code: `cannot find symbol method getSavedStateHandleHolder() in class ActivityComponentManager`.
2. **What it gave me**:  
   The AI diagnosed a version mismatch between `hilt-android:2.60.1` and `hilt-compiler:2.57.1` in `app/build.gradle.kts`, explaining that the internal API in `ActivityComponentManager` changed between these minor releases.
3. **What I did**:  
   Modified `app/build.gradle.kts` by aligning `hilt-compiler` to `2.60.1`.
4. **One thing verified myself**:  
   I executed a `./gradlew clean assembleDebug` to verify that the generated code in `build/generated/ksp/` matched the runtime bytecode without missing symbol errors.

---

### Entry 3: Architectural Decision: DTO vs. Domain Model Separation
1. **What I asked / problem solving**:  
   Asked whether maintaining both `TvShowDto` and `TvShow` domain models was necessary or redundant for a small app.
2. **What it gave me**:  
   A breakdown of Clean Architecture principles: JSON contract isolation (protecting UI from API schema changes), flattening nested JSON objects (e.g., `image.original` and `rating.average`), null-safety normalization, and third-party library decoupling (Moshi annotations kept out of domain models).
3. **What I did**:  
   Accepted the approach and wrote a dedicated mapper extension function `toDomain()` in `Mapper.kt`.
4. **One thing verified myself**:  
   I verified that flattening the data into non-nullable defaults in the Domain model significantly cleaned up the Compose UI code, removing the need for defensive null-coalescing (`?:`) across multiple composables.

---

### Entry 4: Decoupling Base URL via BuildConfig
1. **What I asked / problem solving**:  
   How to avoid hardcoding the TVMaze API base URL in the interface companion object.
2. **What it gave me**:  
   Instructions on configuring `buildConfigField` in `app/build.gradle.kts`, enabling `buildConfig = true` in `buildFeatures`, and injecting `BuildConfig.BASE_URL` in Retrofit via `AppModule`.
3. **What I did**:  
   Accepted and applied the configuration to `app/build.gradle.kts` and `AppModule.kt`.
4. **One thing the AI got wrong**:  
   The AI's initial `buildConfigField` syntax used single escaping — `"\"https://api.tvmaze.com/\""` — which caused a Gradle sync error because the Kotlin DSL requires an additional layer of escaping for the embedded quotes to survive through to the generated Java string. The correct form is `"\"\\\"https://api.tvmaze.com/\\\"\""`. I caught this by opening the generated `BuildConfig.java` and seeing the URL was malformed. The AI did not mention that string escaping in `buildConfigField` with the Kotlin DSL is a common pitfall and needs double-escaping.

---

### Entry 5: Diagnosing Missing Hilt Provider & Package Alignment
1. **What I asked / problem solving**:  
   Asked the AI to conduct a full check on the Data Layer before making a commit.
2. **What it gave me**:  
   On the first pass, the AI only caught that `AppModule.kt` was missing the `@Provides` method for `TvMazeApi`. It did not flag the package mismatch in `TvMazeApi.kt` until I explicitly asked it to cross-check package declarations across all data layer files.
3. **What I did**:  
   Modified `AppModule.kt` to include `provideTvMazeApi(retrofit: Retrofit)` and corrected the package in `TvMazeApi.kt` to `com.kemas.gitgowayo_test.data.remote`.
4. **One thing the AI got wrong**:  
   The AI's first review missed the package mismatch entirely, even though I explicitly asked it to do a "full check" of the data layer. It required a second, more specific prompt ("check that every file's package declaration matches its directory path") before it caught it. This taught me not to trust a single AI pass as a complete review — a targeted follow-up question is often necessary to surface non-obvious structural issues.

---

### Entry 6: Fixing Test Utility Scope & Dependency Resolution
1. **What I asked / problem solving**:  
   Received an unresolved reference error when attempting to import `TestDispatcher` in `MainDispatcherRule.kt`.
2. **What it gave me**:  
   The AI identified that `MainDispatcherRule.kt` had been mistakenly placed inside `src/main/java/` instead of `src/test/java/`, making `kotlinx-coroutines-test` (declared under `testImplementation`) inaccessible to it.
3. **What I did**:  
   Moved `MainDispatcherRule.kt` to `src/test/java/com/kemas/gitgowayo_test/util/`.
4. **One thing verified myself**:  
   I ran `./gradlew testDebugUnitTest` to ensure both `TvShowListViewModelTest` and `TvShowRepositoryTest` executed and passed 100%.