package com.example.themovieapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.toMovieMessage
import com.example.themovieapp.model.Movie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class SearchUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val hasSearched: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class SearchViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob : Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, errorMessage = null) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update {
                it.copy(movies = emptyList(), hasSearched = false, isLoading = false, isRefreshing = false)
            }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400.milliseconds)
            search(query.trim(), forceRefresh = false)
        }
    }

    fun clearQuery() {
        searchJob?.cancel()
        _uiState.value = SearchUiState()
    }

    fun retry() {
        val query = _uiState.value.query.trim()
        if (query.isNotBlank()) search(query, forceRefresh = true)
    }

    fun refresh() {
        val query = _uiState.value.query.trim()
        if (query.isNotBlank()) search(query, forceRefresh = true)
    }

    private fun search(query: String, forceRefresh: Boolean) {
        // Search always hits network (results vary); forceRefresh only controls spinner style
        val hasData = _uiState.value.movies.isNotEmpty()
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !hasData,
                    isRefreshing = hasData && forceRefresh,
                    hasSearched = true,
                    errorMessage = null
                )
            }
            movieRepository.searchMovies(query)
                .onSuccess { movies ->
                    _uiState.update {
                        it.copy(movies = movies, isLoading = false, isRefreshing = false, errorMessage = null)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = e.toMovieMessage()
                        )
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieApplication)
                SearchViewModel(app.movieRepository)
            }
        }
    }
}
