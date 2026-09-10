# Written Reflection

---

### 1. Which part of your submission are you least confident about, and why?
**Answer:**  
I am least confident about the edge cases in HTML stripping for the TV show `summary`. While using Android's native `HtmlCompat.fromHtml` handles standard formatting tags (`<p>`, `<b>`, `<i>`, `<br>`) effectively, some external APIs can return malformed HTML snippets, unusual entities, or excessive trailing whitespaces. If I had more time, I would build a dedicated, comprehensive parser with unit test fixtures covering various dirty HTML strings, or parse the HTML into an `AnnotatedString` to preserve useful text formatting (like bolding and links) directly inside Jetpack Compose.

---

### 2. Describe a moment during this project (or any past project) where you got completely stuck. What did you do, step by step?
**Answer:**  
During this project, I hit a force-close crash that only appeared while scrolling through the TV Show List — not on launch, which made it harder to pinpoint.

The crash message was:
```
IllegalArgumentException: Key "25008" was already used. Keys in a lazy list must be unique.
```

**Step-by-step resolution:**
1. **Read the crash message carefully:** The error named a specific key value (`25008`) and pointed directly to the `LazyVerticalGrid` composable. I knew immediately it was a key uniqueness violation, but I didn't understand why — I assumed API IDs would always be unique.
2. **Verified the assumption was wrong:** I logged the raw API response and found that ID `25008` appeared more than once across paginated data. The API does not appear to guarantee unique IDs across pages based on what I observed during testing.
3. **Traced every Lazy Layout using `key`:** I checked not just the List screen but also the Cast and Seasons lists on the Detail screen, which used the same bare `id` pattern — meaning they had the same latent bug waiting to surface.
4. **Applied the fix consistently:** I combined the ID with the item's index (`"${item.id}_$index"`) across all three affected Lazy Layouts. I'm aware this is a pragmatic compromise — index-based keys encode position, not identity, so if the dataset shifts, Compose treats repositioned items as new. The ideal fix is a stable unique identifier from the backend, but since I cannot guarantee that here, `id + index` is the defensible fallback.
5. **Verified by scrolling through the full list:** Scrolled end-to-end on an emulator to confirm no further crashes occurred.
---

### 3. Imagine: it's Thursday, your task is due Friday, and you realize you misunderstood the requirement, half your work is wrong. What are you doing now?
**Answer:**
1. **Stop immediately & assess the gap:** I pause coding to clearly map out what is currently built versus what the actual requirement demands.
2. **Communicate proactively:** I immediately reach out to my lead/product manager, explain the situation transparently without hiding it, and present a realistic scope of what can be completed by Friday vs. what needs a slight extension.
3. **Prioritize MVP / Core Path:** I salvage reusable components (e.g., Data layer models, network layer) and focus 100% on delivering the critical user journey first rather than optional polish.
4. **Keep stakeholders updated:** Provide an interim update by Thursday evening on progress and deliver the corrected core scope by Friday.

---

### 4. Your mentor asks you to change an approach you believe is worse. What do you do?
**Answer:**
1. **Listen actively & seek context:** I first listen carefully to understand *why* they recommend this approach—mentors often have broader context on team conventions, future roadmap, performance constraints, or legacy compatibility that I might not see.
2. **Discuss objectively with trade-offs:** If I still believe my approach has clear advantages, I present my thoughts respectfully with concrete technical trade-offs (e.g., readability, testability, maintenance overhead) rather than personal preference.
3. **Align and commit:** If the mentor still prefers their approach after discussion, I trust their experience, implement their recommendation thoroughly, and observe how it performs in production to learn from it.

---

### 5. What's something technical you taught yourself recently outside of class/work, and how did you learn it?
**Answer:**  
Recently, I taught myself **React Native** to broaden my mobile engineering perspective across cross-platform development.  
To learn it effectively:
- I started with the official React Native documentation and explored modern React concepts (functional components, JSX, and Hooks like `useState` and `useEffect`).
- I built small hands-on projects to compare the development lifecycle and declarative UI paradigms between Jetpack Compose (Kotlin) and React Native (JavaScript/TypeScript).
- I explored how the bridge/new architecture handles communication with native Android modules, which gave me a deeper appreciation for both native and cross-platform mobile ecosystems.