package com.example.themovieapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.themovieapp.data.MovieRepository

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

}