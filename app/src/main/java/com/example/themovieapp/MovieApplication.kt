package com.example.themovieapp

import android.app.Application
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.data.auth.AuthRepository

class MovieApplication : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var preferencesRepository: PreferencesRepository
        private set
    lateinit var movieRepository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(this)
        movieRepository = MovieRepository()
        authRepository = AuthRepository()
    }
}
