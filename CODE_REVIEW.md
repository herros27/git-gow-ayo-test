# Code Review: `MovieViewModel` PR

## Overview
This PR contains several critical issues related to thread management, UI reactivity, 
error handling, testability, and violations of Clean Architecture / Separation of Concerns principles.
If merged, these issues will cause runtime crashes (`NetworkOnMainThreadException` of ANR) and prevent the 
UI from reacting to data updates.

Below is the detailed list of identified issues, along with recommended fixes and to a refactored 
implementation.

---
## Flagged Issues & Solutions

### 1. Blocking Network Call on the Main Thread (`NetworkOnMainThreadException`)
* **Issue**: Calling `url.readText()` performs synchronous, blocking I/O on whichever thread calls `loadMovies()` (typically the Main/UI thread). 
* On Android, this will trigger an immediate `android.os.NetworkOnMainThreadException` or cause the UI to freeze Applicaton Not Responding (ANR).
*  **Fix**: Use Kotlin Coroutines  (`viewModelScope.launch(Dispatchers.IO)`) or a modern asynchronous networking client such as Retrofit or Ktor.
---
### 2. Non-Reactive UI State (No UI Recomposition)
* **Issue**: `var movies: List<Movie>` is a plain mutable property. When its value changes, Jetpack Compose / Android UI will not be notified, and no recomposition will occur.
* **Fix**: Expose a reactive, lifecycle-aware state holder such as `StateFlow<MovieUiState>` or Compose `State`.
---
### 3. Missing Error Handling and Loading State
* **Issue**:
    - There is no error handling (`try-catch`). Network timeouts, 4xx/5xx HTTP errors, or malformed JSON payloads will crash the application.
    - There is no representation of `Loading` or `Error` states, leaving users with no visual feedback during network operations or failures.
* **Fix**: Model the UI state using a structured `sealed interface` representing `Loading`, `Success`, and `Error` (with retry support).
---
### 4. Violation of Clean Architecture and Separation of Concerns (SoC)
* **Issue**:
    - The ViewModel directly constructs the URL, executes the HTTP request, and parses the JSON response. This violates the Single Responsibility Principle.
    - The API URL is hardcoded inside the method, making environment configuration (e.g., debug vs. production) and caching impossible.
* **Fix**: Delegate data operations to a dedicated **Repository** and provide dependencies via **Dependency Injection** (e.g., Hilt/Dagger).
---
### 5. Lack of Testability
* **Issue**: Because network calls and JSON parsing are tightly coupled inside the ViewModel, it is impossible to write isolated unit tests without making actual network requests.
* **Fix**: Inject a `MovieRepository` interface, enabling easy mocking with libraries like MockK or Turbine in unit tests.
---

## Recommend Refactored Implementation

### 1. UI State Definition
```kotlin
sealed interface MovieUiState{
    data object Loading: MovieUiState
    data class Success(val movies: List<Movie>) : MovieUiState
    data class Error(val message: String) : MovieUiState
}
```
### 2. Refactored ViewModel
```kotlin
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
){
    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState : StateFlow<MovieUiState> = _uiState.asStateFlow()
    
    init {
        loadMovies()
    }
    
    fun loadMovies(){
        viewModelScope.launch{
            _uiState.value = MovieUiState.Loading
            
            try {
                val movies = repository.getMovies()
                _uiState.value = MovieUiState.Success(movies)
            } catch (e: Exception){
                _uiState.value = MovieUiState.Error(
                    message = e.localizedMessage ?: "Failed to load movies"
                )
            }
        }
    }
}
```
