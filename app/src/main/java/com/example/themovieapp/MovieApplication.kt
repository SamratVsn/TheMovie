package com.example.themovieapp

import android.app.Application
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.data.WatchlistRepository
import com.example.themovieapp.data.auth.AuthRepository
import com.example.themovieapp.network.RetrofitClient

class MovieApplication : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var preferencesRepository: PreferencesRepository
        private set
    lateinit var movieRepository: MovieRepository
        private set
    lateinit var watchlistRepository: WatchlistRepository
        private set

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        preferencesRepository = PreferencesRepository(this)
        movieRepository = MovieRepository()
        authRepository = AuthRepository()
        watchlistRepository = WatchlistRepository(this)
    }
}
