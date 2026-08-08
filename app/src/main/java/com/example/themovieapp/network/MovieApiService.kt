package com.example.themovieapp.network

import com.example.themovieapp.BuildConfig
import com.example.themovieapp.model.Movie
import com.example.themovieapp.model.MovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getMovieResponse(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MovieApiResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Movie
}