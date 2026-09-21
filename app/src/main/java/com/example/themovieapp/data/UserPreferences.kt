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

val FavoriteGenres = listOf(
    "Action",
    "Adventure",
    "Animation",
    "Comedy",
    "Crime",
    "Documentary",
    "Drama",
    "Family",
    "Fantasy",
    "History",
    "Horror",
    "Music",
    "Mystery",
    "Romance",
    "Science Fiction",
    "Thriller",
    "War",
    "Western",
)

/** TMDB movie genre ids for the [FavoriteGenres] names. */
val GenreNameToId = mapOf(
    "Action" to 28,
    "Adventure" to 12,
    "Animation" to 16,
    "Comedy" to 35,
    "Crime" to 80,
    "Documentary" to 99,
    "Drama" to 18,
    "Family" to 10751,
    "Fantasy" to 14,
    "History" to 36,
    "Horror" to 27,
    "Music" to 10402,
    "Mystery" to 9648,
    "Romance" to 10749,
    "Science Fiction" to 878,
    "Thriller" to 53,
    "War" to 10752,
    "Western" to 37,
)