<p align="center">
  <img src="docs/screenshots/app-icon.png" width="96" alt="Trời Ơi app icon">
</p>

<h1 align="center">Trời Ơi</h1>

<p align="center">
  A weather app for Android and iOS built with Kotlin Multiplatform and Compose Multiplatform —
  one shared codebase, two native apps.
</p>

<p align="center">
  <img alt="Kotlin Multiplatform" src="https://img.shields.io/badge/Kotlin%20Multiplatform-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose%20Multiplatform-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="Android" src="https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white">
  <img alt="iOS" src="https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white">
  <a href="LICENSE"><img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
</p>

Search for a city, save up to 6 areas, and see current conditions, a 7-day outlook, and an
hourly rain forecast — with offline-first caching, condition-driven gradient theming, a
metric/imperial toggle, and a Light/Dark/System theme switch.

The interface follows a deliberate design direction — *Atmospheric Utility with a Soft Editorial
finish*: weather-tinted surfaces, a strong glanceable hierarchy for temperature and rain risk,
quiet supporting metadata, and motion reserved for a single focal point rather than sprayed across
every list.

## Screenshots

Live data for six Vietnamese cities — Ho Chi Minh City, Hà Nội, Đà Nẵng, Quảng Ngãi, Cần Thơ,
and Sa Pa — captured on an Android emulator and iOS simulator, in both Light and Dark mode.
The Overview list shows all six saved areas at a glance; the Detail screen's gradient and icon
colors are driven by the viewed area's actual weather condition and time of day, independent of
the app's Light/Dark setting.

<table>
  <tr>
    <th></th>
    <th>Overview — Light</th>
    <th>Overview — Dark</th>
    <th>Detail</th>
  </tr>
  <tr>
    <td><strong>Android</strong></td>
    <td><img src="docs/screenshots/android_overview_light.png" width="220"></td>
    <td><img src="docs/screenshots/android_overview_dark.png" width="220"></td>
    <td><img src="docs/screenshots/android_detail_light.png" width="220"></td>
  </tr>
  <tr>
    <td><strong>iOS</strong></td>
    <td><img src="docs/screenshots/ios_overview_light.png" width="220"></td>
    <td><img src="docs/screenshots/ios_overview_dark.png" width="220"></td>
    <td><img src="docs/screenshots/ios_detail_light.png" width="220"></td>
  </tr>
</table>

## Features

- **Overview**: saved areas at a glance (current temp, high/low, rain chance), pull-to-refresh,
  swipe-to-delete with undo.
- **Search**: debounced city search backed by OpenWeatherMap's geocoding API, capped at 6 saved
  areas.
- **Detail**: current conditions, an hourly rain strip scoped to the remaining hours of today, and
  a 7-day forecast per area — all set against a soft two-stop gradient keyed to that area's actual
  weather condition and time of day.
- **Static-by-default motion**: every icon in a scrolling list stays still. Condition motion is
  reserved for the Detail hero, which carries a single low-amplitude gesture per condition — a
  gentle turn, breathe, drift, fall, or one restrained thunder pulse — with a quiet rest interval
  between cycles. The loop pauses when the app is backgrounded or the hero scrolls off-screen, and
  the platform's reduced-motion setting replaces it with the icon's stable static state.
- **Adaptive layouts**: one shared width tier drives every route, so no screen owns its own
  breakpoint. Phones and medium widths keep a single column; at tablet widths the Detail screen
  splits the hero and the forecast panels into balanced columns and Overview becomes a two-column
  card grid.
- **Theming**: a persisted Light/Dark/System preference, chosen from visual theme preview cards and
  a segmented units control in Settings; picking System always wins back over a stale manual choice
  the next time the OS theme actually changes. Theme choice owns luminance — a rainy night never
  drags a Light-mode screen into darkness; the weather condition only tints the atmosphere inside
  the chosen luminance family.
- **Offline-first caching**: SQLDelight is the single source of truth; a 30-minute TTL plus
  request coalescing keep the UI responsive and avoid redundant network calls. Stale cache is
  still shown (flagged) if a refresh fails.
- **Units**: switch between metric (°C, m/s) and imperial (°F, mph); switching invalidates the
  cache and refetches in the new units. Persisted across restarts.
- **Accessible**: all user-facing strings are externalized resources. Cards, forecast rows, and
  search results expose one merged description each instead of a stream of fragments; section
  titles carry heading semantics; status surfaces announce politely as live regions; and removing a
  saved place has a screen-reader action, so swipe is never the only path. Every interactive target
  clears 48 dp, verified at 200 percent font scale on a 320 dp screen, and the selectors are
  reachable and activatable with an external keyboard.

## Tech stack

| Layer | Choice |
| --- | --- |
| UI | Compose Multiplatform, Material 3 |
| DI | Koin |
| Networking | Ktor (OkHttp on Android, Darwin on iOS) |
| Persistence | SQLDelight (Android SQLite driver / iOS native driver) |
| Preferences | multiplatform-settings (SharedPreferences on Android, NSUserDefaults on iOS) |
| Serialization | kotlinx-serialization, kotlinx-datetime |
| Testing | kotlin-test, Turbine, Ktor MockEngine, Compose UI testing (instrumented) |

## Architecture

Clean Architecture with one-way dependencies: `presentation` → `domain` ← `data`. The `domain`
layer (models, repository interfaces, use cases) has no framework imports — it's the one part of
the codebase that doesn't know Android, iOS, Compose, or SQLDelight exist. Everything in
`commonMain` is shared source compiled for both targets; `androidMain`/`iosMain` hold nothing but
the platform bindings each shared interface needs.

```mermaid
graph TD
    subgraph common["commonMain (shared Kotlin)"]
        direction TB
        Pres["presentation<br/>Compose UI · ViewModels"]
        Dom["domain<br/>models · repository interfaces · use cases"]
        Data["data<br/>repository impls · mappers · DTOs"]
        Pres --> Dom
        Data --> Dom
    end

    subgraph android["androidMain"]
        direction TB
        AndroidSql["SQLDelight Android driver"]
        AndroidHttp["Ktor OkHttp engine"]
        AndroidPrefs["SharedPreferences"]
    end

    subgraph ios["iosMain"]
        direction TB
        IosSql["SQLDelight Native driver"]
        IosHttp["Ktor Darwin engine"]
        IosPrefs["NSUserDefaults"]
    end

    Data -.provides via Koin.-> AndroidSql
    Data -.provides via Koin.-> AndroidHttp
    Data -.provides via Koin.-> AndroidPrefs
    Data -.provides via Koin.-> IosSql
    Data -.provides via Koin.-> IosHttp
    Data -.provides via Koin.-> IosPrefs
```

## Project layout

- `/composeApp` is the shared Kotlin Multiplatform module.
  - `commonMain` holds the domain, data, and presentation layers shared across both platforms.
  - `androidMain` / `iosMain` hold only the platform-specific bindings (SQL driver, HTTP engine,
    settings storage, Koin platform module).
  - `commonTest` / `androidUnitTest` hold unit tests (ViewModels, repositories, mappers).
    `androidInstrumentedTest` holds Compose UI tests that run on a device/emulator.
- `/iosApp` is the iOS app shell (SwiftUI entry point hosting the shared Compose UI).

## Getting started

Want to clone this and build your own weather app on top of it? Here's the full path from zero
to a running build on both platforms.

1. **Install prerequisites**
   - JDK 17+
   - [Android Studio](https://developer.android.com/studio) (latest stable) with the Kotlin
     Multiplatform plugin
   - Xcode 15+ (macOS only, for the iOS target)
2. **Clone the repo**
   ```bash
   git clone https://github.com/nphkhiem/weather-forecast-by-kmp.git
   cd weather-forecast-by-kmp
   ```
3. **Get an OpenWeatherMap API key.** Sign up at
   [openweathermap.org](https://openweathermap.org/api) and subscribe to the **One Call by Call**
   plan — the free "Current Weather" tier is not enough; One Call 3.0 is what powers both the
   forecast and the geocoding search. It includes 1,000 free calls/day before billing kicks in.
4. **Configure your key locally.** Add this line to `local.properties` at the repo root (this file
   is gitignored — never commit a real key):
   ```properties
   owm.apiKey=YOUR_KEY_HERE
   ```
5. **Run on Android**: open the project in Android Studio and run the `composeApp` configuration,
   or from the command line:
   ```bash
   ./gradlew :composeApp:installDebug
   ```
6. **Run on iOS**: open `iosApp/iosApp.xcodeproj` in Xcode, pick a simulator, and hit Run — or from
   the command line:
   ```bash
   xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator
   ```
7. **Run the tests**:
   ```bash
   # Unit tests (JVM + iOS simulator)
   ./gradlew :composeApp:testDebugUnitTest :composeApp:iosSimulatorArm64Test

   # Instrumented Compose UI tests (needs a running Android emulator/device)
   ./gradlew :composeApp:connectedDebugAndroidTest
   ```

## License

MIT — see [LICENSE](LICENSE). Fork it, extend it, ship your own version.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
