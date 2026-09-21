# 🎬 The Movie App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.02.01-green.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-1.4.0-red.svg?style=flat&logo=materialdesign)](https://m3.material.io)
[![Firebase Auth](https://img.shields.io/badge/Firebase-Auth-orange.svg?style=flat&logo=firebase)](https://firebase.google.com/docs/auth)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**The Movie App** is a modern Android application built with Jetpack Compose that leverages the TMDB API to showcase popular, now playing, and top-rated movies. It features a clean Material 3 design, Firebase Email/Password authentication, a persistent watchlist, personalized genre recommendations, offline caching, and user preference management using DataStore.

---

## 📸 Screenshots

| Home | Search | Movie Details | Profile |
|------|--------|---------------|---------|
| ![](Home.png) | ![](Search.png) | ![](Details.png) | ![](Profile.png) |

| Settings |
|----------|
| ![](Settings.png) |

---

## ✨ Features

*   **Browse Movies**: Explore Popular, Now Playing, and Top Rated sections, or drill into a single category grid.
*   **Personalized Recommendations**: A "Because you like *X*" row built from your favorite genre.
*   **Search**: Debounced live search across the TMDB database with infinite scrolling.
*   **Movie Details**: Backdrops, posters, ratings, runtime, genres, and overview — with pull-to-refresh and offline fallback.
*   **Favorites / Watchlist**: Tap the heart on any movie to save it. Saved and removed actions confirm via themed snackbars. The list persists across restarts.
*   **Authentication**: Firebase Email/Password sign-up, login, password reset, and sign-out. Your signup name becomes your in-app display name and stays in sync with Firebase.
*   **Profile**: Read-only overview (avatar initials, name, email, bio, preferences, account) with Settings one tap away in the top bar.
*   **Settings**: Edit display name, bio, and favorite genre (genre is picked from the official TMDB list, not free text), plus theme mode and default category.
*   **Offline Support**: 10 MB HTTP disk cache + 5-minute in-memory repository cache with stale-data fallback, so cached movies still open with no connection.
*   **Smart States**: Pull-to-refresh everywhere, pagination spinners, inline retry banners that preserve loaded content, and specific error messages (no internet, timeout, invalid API key, rate limits).
*   **Material 3 UI**: Edge-to-edge layout with proper status-bar insets, dark/light/system themes, and adaptive launcher icon.
*   **Image Loading**: High-quality poster/backdrop rendering with Coil, including loading and error placeholders.

---

## 🛠 Tech Stack

*   **Kotlin**: Primary programming language.
*   **Jetpack Compose**: Modern toolkit for building native UI.
*   **Material 3**: Latest version of Google's open-source design system.
*   **Navigation Compose**: Declarative navigation for Compose.
*   **Retrofit & OkHttp**: Networking and API interaction (with disk cache, timeouts, and debug-only logging).
*   **Kotlinx Serialization**: Type-safe JSON parsing.
*   **Firebase Authentication**: Email/Password sign-up, login, and session management.
*   **Coil**: Image loading library for Android.
*   **DataStore Preferences**: Reactive storage for user preferences and the watchlist.
*   **ViewModel**: Architecture component to store and manage UI-related data.
*   **Coroutines & Flow**: Asynchronous programming and reactive data streams.

---

## 🏛 Architecture

The project follows the recommended **MVVM (Model-View-ViewModel)** architecture and the **Repository Pattern** to ensure a clean separation of concerns and maintainability.

*   **Single Activity Architecture**: The entire app runs within a single `MainActivity`.
*   **Navigation Compose**: Bottom-bar destinations (Home, Search, Favorites, Profile) plus detail, auth, and settings routes via `NavGraph`.
*   **Manual DI**: Repositories (`MovieRepository`, `PreferencesRepository`, `WatchlistRepository`, `AuthRepository`) are provisioned by `MovieApplication` and injected through ViewModel factories.
*   **Unidirectional Data Flow**: ViewModels expose `StateFlow` UI state; screens render it and forward events back.

---

## 📂 Project Structure

```text
app/
├── data/
│   ├── auth/                # AuthRepository, AuthState (Firebase session flow)
│   ├── MovieRepository.kt   # TMDB access + 5-min in-memory cache
│   ├── PreferencesRepository.kt  # Theme, category, profile (DataStore)
│   ├── WatchlistRepository.kt    # Favorites list (DataStore JSON)
│   ├── MovieErrors.kt       # HTTP/network → user-friendly messages
│   └── UserPreferences.kt   # ThemeMode, MovieCategory, genre lists
├── model/                   # Movie / MovieDetail / Genre DTOs
├── network/                 # Retrofit service, OkHttp client, image URLs
├── ui/
│   ├── components/          # MovieCard, posters, loading/error screens
│   ├── detail/              # Movie detail + favorite toggle + snackbars
│   ├── home/                # Home, recommendations, pagination
│   ├── profile/             # Profile display + Auth (login/signup) screens
│   ├── search/              # Search with debounce + pagination
│   ├── settings/            # Profile editing, appearance, about
│   ├── watchlist/           # Favorites screen
│   ├── theme/               # Color, Type, and Theme definitions
│   └── NavGraph.kt          # All routes + bottom bar
├── MainActivity.kt          # Entry point of the application
└── MovieApplication.kt      # DI provisioning + network init
```

---

## 🚀 Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/SamratVsn/TheMovieApp.git
    ```
2.  **Open in Android Studio**: Open the root folder of the project.
3.  **Set up API Key**: copy the template and add your TMDB API Key (never commit the real file — `local.properties` is git-ignored):
    ```bash
    cp local.properties.example local.properties
    ```
    ```properties
    TMDB_API_KEY=your_api_key_here
    ```
    Headless/CI builds can instead use `~/.gradle/gradle.properties`, `-PTMDB_API_KEY=...`, or the `TMDB_API_KEY` env var.
4.  **Add Firebase config**: download `google-services.json` for package `com.example.themovieapp` from the Firebase Console (with Email/Password sign-in enabled) and place it at `app/google-services.json` (also git-ignored — each developer/CI uses their own). The app won't build without it.
5.  **Sync Gradle**: Wait for Android Studio to download dependencies and sync the project.
6.  **Run the app**: Click the "Run" button or press `Shift + F10`.

---

## 📋 Requirements

*   **Minimum SDK**: 24
*   **Target SDK**: 37
*   **Compile SDK**: 37
*   **Kotlin Version**: 2.2.10
*   **AGP Version**: 9.4.1

---

## 📦 Libraries Used

| Library | Purpose |
| ------- | ------- |
| `androidx.compose` (BOM `2026.02.01`) | UI Toolkit |
| `androidx.compose.material3` (`1.4.0`) | Material 3 components |
| `androidx.navigation:navigation-compose` (`2.9.8`) | App Navigation |
| `com.squareup.retrofit2:retrofit` (`2.9.0`) | API Requests |
| `com.squareup.okhttp3` (`4.11.0`) | HTTP client, cache, timeouts |
| `okhttp3:logging-interceptor` | Network debugging (debug builds only) |
| `io.coil-kt:coil-compose` (`2.4.0`) | Image Loading |
| `androidx.datastore:datastore-preferences` | Local Persistence |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` (`1.6.0`) | Data Parsing |
| `com.google.firebase:firebase-bom` (`33.1.2`) + `firebase-auth` | Authentication backend |
| `org.jetbrains.kotlinx:kotlinx-coroutines-play-services` | `Task.await()` bridges for Firebase |
| `androidx.lifecycle` (runtime, viewmodel) | ViewModels + lifecycle-aware state |

---

## 🔐 Secrets & Security Notes

*   `local.properties` (TMDB key) and `app/google-services.json` (Firebase config) are git-ignored — see `local.properties.example` for the template.
*   The Firebase client key is a public identifier, not a private secret: protect it with package/SHA-1 restrictions in the Google Cloud Console and consider enabling App Check.
*   A truly private key can't live in a shipped APK — for stronger secrecy, proxy TMDB through your own backend later.

---

## 🔮 Future Improvements

*   **Trailer Integration**: Embed YouTube players to watch movie trailers.
*   **Cloud-synced Watchlist**: Sync favorites per user via Firestore instead of local-only storage.
*   **Google Sign-In & Email Verification**: More auth providers plus verified-email gating.
*   **Paging 3**: Migrate manual pagination to the Paging library with Room caching.
*   **Push Notifications**: Notify users about new releases or updates to their watchlist.
*   **Release Hardening**: R8/minify,expanded tests, and App Check enforcement.

---

## 📄 License

```text
MIT License

Copyright (c) 2026 Samrat Parajuli

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions...
```

---

## 👤 Author

**Samrat Parajuli**

*   **Portfolio**: [samratparajuli0.com.np](https://www.samratparajuli0.com.np/)
*   **GitHub**: [@SamratVsn](https://github.com/SamratVsn)
*   **LinkedIn**: [samratvsn](https://linkedin.com/in/samratvsn)
