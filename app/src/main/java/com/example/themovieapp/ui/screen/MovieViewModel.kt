package com.example.themovieapp.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.model.Movie

interface MovieUiState{
    data class Success(
        val movie: List<Movie> = emptyList(),
    ) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MovieApplication).container
                val movieRepository = application.bookshelfRepository
                MovieViewModel(movieRepository = movieRepository)
            }
        }
    }
}