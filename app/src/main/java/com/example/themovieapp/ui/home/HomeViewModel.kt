package com.example.themovieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.GenreNameToId
import com.example.themovieapp.data.MovieCategory
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.data.toMovieMessage
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
    val recommended: List<Movie> = emptyList(),
    val favoriteGenre: String = "Action",
    val selectedCategory: MovieCategory = MovieCategory.POPULAR,
    val browseMode: BrowseMode = BrowseMode.ALL_SECTIONS,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
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
            val prefs = preferencesRepository.preferences.first()
            _uiState.update {
                it.copy(
                    selectedCategory = prefs.defaultCategory,
                    favoriteGenre = prefs.favoriteGenre
                )
            }
            loadHome(forceRefresh = false)
        }
        // Keep favorite genre + recommendations live without refetching movies
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                _uiState.update { state ->
                    if (state.favoriteGenre == prefs.favoriteGenre) return@update state
                    val all = state.popular + state.nowPlaying + state.topRated
                    state.copy(
                        favoriteGenre = prefs.favoriteGenre,
                        recommended = filterByGenre(all, prefs.favoriteGenre)
                    )
                }
            }
        }
    }

    fun selectCategory(category: MovieCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                browseMode = BrowseMode.SINGLE_CATEGORY
            )
        }
        loadHome(forceRefresh = false)
    }

    fun showAllSections() {
        _uiState.update { it.copy(browseMode = BrowseMode.ALL_SECTIONS) }
        loadHome(forceRefresh = false)
    }

    fun retry() = loadHome(forceRefresh = true)

    fun refresh() = loadHome(forceRefresh = true)

    private fun loadHome(forceRefresh: Boolean) {
        viewModelScope.launch {
            val hasData = hasDataFor(_uiState.value)
            _uiState.update {
                it.copy(
                    isLoading = !hasData,
                    isRefreshing = hasData && forceRefresh,
                    errorMessage = null
                )
            }
            runCatching {
                coroutineScope {
                    when (_uiState.value.browseMode) {
                        BrowseMode.ALL_SECTIONS -> {
                            val popular = async {
                                movieRepository.getMovies(MovieCategory.POPULAR, forceRefresh = forceRefresh).getOrThrow()
                            }
                            val nowPlaying = async {
                                movieRepository.getMovies(MovieCategory.NOW_PLAYING, forceRefresh = forceRefresh).getOrThrow()
                            }
                            val topRated = async {
                                movieRepository.getMovies(MovieCategory.TOP_RATED, forceRefresh = forceRefresh).getOrThrow()
                            }
                            Triple(popular.await(), nowPlaying.await(), topRated.await())
                        }
                        BrowseMode.SINGLE_CATEGORY -> {
                            val movies = movieRepository.getMovies(
                                _uiState.value.selectedCategory,
                                forceRefresh = forceRefresh
                            ).getOrThrow()
                            when (_uiState.value.selectedCategory) {
                                MovieCategory.POPULAR -> Triple(movies, emptyList(), emptyList())
                                MovieCategory.NOW_PLAYING -> Triple(emptyList(), movies, emptyList())
                                MovieCategory.TOP_RATED -> Triple(emptyList(), emptyList(), movies)
                            }
                        }
                    }
                }
            }.onSuccess { (popular, nowPlaying, topRated) ->
                val all = popular + nowPlaying + topRated
                _uiState.update {
                    it.copy(
                        popular = popular,
                        nowPlaying = nowPlaying,
                        topRated = topRated,
                        recommended = filterByGenre(all, it.favoriteGenre),
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
            }.onFailure { e ->
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

    private fun hasDataFor(state: HomeUiState): Boolean = when (state.browseMode) {
        BrowseMode.ALL_SECTIONS ->
            state.popular.isNotEmpty() || state.nowPlaying.isNotEmpty() || state.topRated.isNotEmpty()
        BrowseMode.SINGLE_CATEGORY -> when (state.selectedCategory) {
            MovieCategory.POPULAR -> state.popular.isNotEmpty()
            MovieCategory.NOW_PLAYING -> state.nowPlaying.isNotEmpty()
            MovieCategory.TOP_RATED -> state.topRated.isNotEmpty()
        }
    }

    private fun filterByGenre(movies: List<Movie>, genreName: String): List<Movie> {
        val genreId = GenreNameToId[genreName] ?: return emptyList()
        return movies.distinctBy { it.id }.filter { genreId in it.genreIds }.take(12)
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
