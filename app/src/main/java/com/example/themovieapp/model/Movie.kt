package com.example.themovieapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieApiResponse(
    val page: Int,
    val results: List<Movie>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    val popularity: Double = 0.0
) {
    val posterUrl: String?
        get() = posterPath?.let {
            "https://image.tmdb.org/t/p/w500$it"
        }

    val backdropUrl: String?
        get() = backdropPath?.let {
            "https://image.tmdb.org/t/p/w1280$it"
        }
}

@Serializable
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String? = null,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("vote_average")
    val voteAverage: Double = 0.0,

    @SerialName("vote_count")
    val voteCount: Int = 0,

    val runtime: Int? = null,
    val tagline: String? = null,
    val status: String? = null,

    val genres: List<Genre> = emptyList(),

    @SerialName("original_language")
    val originalLanguage: String? = null
) {
    val posterUrl: String?
        get() = posterPath?.let {
            "https://image.tmdb.org/t/p/w500$it"
        }

    val backdropUrl: String?
        get() = backdropPath?.let {
            "https://image.tmdb.org/t/p/w1280$it"
        }
}

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

