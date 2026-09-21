package com.example.themovieapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.MovieRepository
import com.example.themovieapp.data.WatchlistRepository
import com.example.themovieapp.data.toMovieMessage
import com.example.themovieapp.model.Movie
import com.example.themovieapp.model.MovieDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val movie: MovieDetail? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository,
    private val watchlistRepository: WatchlistRepository,
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val isFavorite: StateFlow<Boolean> = watchlistRepository.isFavorite(movieId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    init {
        loadDetail(forceRefresh = false)
    }

    fun retry() = loadDetail(forceRefresh = true)

    fun refresh() = loadDetail(forceRefresh = true)

    fun toggleFavorite() {
        val detail = _uiState.value.movie ?: return
        viewModelScope.launch {
            watchlistRepository.toggle(detail.toMovie())
        }
    }

    private fun loadDetail(forceRefresh: Boolean) {
        val hasData = _uiState.value.movie != null
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !hasData,
                    isRefreshing = hasData && forceRefresh,
                    errorMessage = null
                )
            }
            repository.getMovieDetail(movieId, forceRefresh = forceRefresh)
                .onSuccess { movie ->
                    _uiState.update {
                        it.copy(
                            movie = movie,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            // Keep old movie on refresh failure; only full-screen error when nothing cached
                            errorMessage = e.toMovieMessage()
                        )
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieApplication
                DetailViewModel(
                    createSavedStateHandle(),
                    app.movieRepository,
                    app.watchlistRepository
                )
            }
        }
    }
}

private fun MovieDetail.toMovie(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    genreIds = genres.map { it.id },
    originalLanguage = originalLanguage,
    originalTitle = null,
    popularity = 0.0
)
