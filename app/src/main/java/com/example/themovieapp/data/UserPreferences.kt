package com.example.themovieapp.data

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class MovieCategory(val label: String) {
    POPULAR("Popular"),
    NOW_PLAYING("Now Playing"),
    TOP_RATED("Top Rated")
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultCategory: MovieCategory = MovieCategory.POPULAR,
    val displayName: String = "Movie Fan",
    val bio: String = "I love discovering great films.",
    val favoriteGenre: String = "Action"
)