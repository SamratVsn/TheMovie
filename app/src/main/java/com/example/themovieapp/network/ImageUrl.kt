package com.example.themovieapp.network

object ImageUrl {
    private const val BASE = "https://image.tmdb.org/t/p/"

    fun poster(path: String?, size: String = "w500"): String? {
        if (path.isNullOrBlank()) return null
        return "$BASE$size$path"
    }

    fun backdrop(path: String?, size: String = "w780"): String? {
        if (path.isNullOrBlank()) return null
        return "$BASE$size$path"
    }
}
