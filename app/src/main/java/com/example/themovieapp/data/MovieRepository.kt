package com.example.themovieapp.data

import com.example.themovieapp.model.Movie
import com.example.themovieapp.model.MovieApiResponse
import com.example.themovieapp.network.MovieApiService

interface MovieRepository {
    suspend fun getMovieResponse() : MovieApiResponse
    suspend fun getMovieDetail(id: Int) : Movie
}

class NetworkMovieRepository(private val movieApiService: MovieApiService) : MovieRepository {
    override suspend fun getMovieResponse() : MovieApiResponse =
        movieApiService.getMovieResponse()

    override suspend fun getMovieDetail(id: Int): Movie  =
        movieApiService.getMovieDetail(id)
}