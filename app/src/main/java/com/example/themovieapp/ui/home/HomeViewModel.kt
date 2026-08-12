package com.example.themovieapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.PreferencesRepository

class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

}