package com.example.themovieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.MovieCategory
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.model.Movie
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val popular: List<Movie> = emptyList(),
    val nowPlaying: List<Movie> = emptyList(),
    val topRated: List<Movie> = emptyList(),
    val selectedCategory: MovieCategory = MovieCategory.POPULAR,
    val browseMode: BrowseMode = BrowseMode.ALL_SECTIONS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class BrowseMode {
    ALL_SECTIONS,
    SINGLE_CATEGORY
}

class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val defaultCategory = preferencesRepository.preferences.first().defaultCategory
            _uiState.update { it.copy(selectedCategory = defaultCategory) }
            loadHome()
        }
    }

    fun selectCategory(category: MovieCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                browseMode = BrowseMode.SINGLE_CATEGORY
            )
        }
        loadHome()
    }

    fun showAllSections() {
        _uiState.update { it.copy(browseMode = BrowseMode.ALL_SECTIONS) }
        loadHome()
    }

    fun retry() = loadHome()

    private fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                coroutineScope {
                    when (_uiState.value.browseMode) {
                        BrowseMode.ALL_SECTIONS -> {
                            val popular = async { movieRepository.getPopularMovies().getOrThrow() }
                            val nowPlaying = async { movieRepository.getNowPlayingMovies().getOrThrow() }
                            val topRated = async { movieRepository.getTopRatedMovies().getOrThrow() }
                            Triple(popular.await(), nowPlaying.await(), topRated.await())
                        }
                        BrowseMode.SINGLE_CATEGORY -> {
                            val movies = when (_uiState.value.selectedCategory) {
                                MovieCategory.POPULAR -> movieRepository.getPopularMovies().getOrThrow()
                                MovieCategory.NOW_PLAYING -> movieRepository.getNowPlayingMovies().getOrThrow()
                                MovieCategory.TOP_RATED -> movieRepository.getTopRatedMovies().getOrThrow()
                            }
                            when (_uiState.value.selectedCategory) {
                                MovieCategory.POPULAR -> Triple(movies, emptyList(), emptyList())
                                MovieCategory.NOW_PLAYING -> Triple(emptyList(), movies, emptyList())
                                MovieCategory.TOP_RATED -> Triple(emptyList(), emptyList(), movies)
                            }
                        }
                    }
                }
            }.onSuccess { (popular, nowPlaying, topRated) ->
                _uiState.update {
                    it.copy(
                        popular = popular,
                        nowPlaying = nowPlaying,
                        topRated = topRated,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not load movies"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieApplication)
                HomeViewModel(app.movieRepository, app.preferencesRepository)
            }
        }
    }
}