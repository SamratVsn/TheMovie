package com.example.themovieapp.data

import com.example.themovieapp.model.Movie
import com.example.themovieapp.model.MovieDetail
import com.example.themovieapp.network.MovieApiService
import com.example.themovieapp.network.RetrofitClient

class MovieRepository(
    private val api: MovieApiService = RetrofitClient.movieApi
) {
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getPopularMovies(page).results
    }

    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getNowPlayingMovies(page).results
    }

    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getTopRatedMovies(page).results
    }

    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>> = runCatching {
        api.searchMovies(query, page).results
    }

    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> = runCatching {
        api.getMovieDetail(movieId)
    }
}
