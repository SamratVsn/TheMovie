package com.example.themovieapp.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.themovieapp.data.MovieCategory
import com.example.themovieapp.model.Movie
import com.example.themovieapp.ui.components.ErrorScreen
import com.example.themovieapp.ui.components.LoadingScreen
import com.example.themovieapp.ui.components.MovieCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BrowseChips(
                browseMode = uiState.browseMode,
                selectedCategory = uiState.selectedCategory,
                onShowAll = viewModel::showAllSections,
                onSelectCategory = viewModel::selectCategory,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                uiState.isLoading -> LoadingScreen()
                uiState.errorMessage != null -> ErrorScreen(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::retry
                )
                uiState.browseMode == BrowseMode.ALL_SECTIONS -> AllSectionsHome(
                    popular = uiState.popular,
                    nowPlaying = uiState.nowPlaying,
                    topRated = uiState.topRated,
                    onMovieClick = onMovieClick
                )
                else -> SingleCategoryGrid(
                    movies = when (uiState.selectedCategory) {
                        MovieCategory.POPULAR -> uiState.popular
                        MovieCategory.NOW_PLAYING -> uiState.nowPlaying
                        MovieCategory.TOP_RATED -> uiState.topRated
                    },
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

@Composable
private fun BrowseChips(
    browseMode: BrowseMode,
    selectedCategory: MovieCategory,
    onShowAll: () -> Unit,
    onSelectCategory: (MovieCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = browseMode == BrowseMode.ALL_SECTIONS,
            onClick = onShowAll,
            label = { Text("All") }
        )
        MovieCategory.entries.forEach { category ->
            FilterChip(
                selected = browseMode == BrowseMode.SINGLE_CATEGORY &&
                        selectedCategory == category,
                onClick = { onSelectCategory(category) },
                label = { Text(category.label) }
            )
        }
    }
}

@Composable
private fun AllSectionsHome(
    popular: List<Movie>,
    nowPlaying: List<Movie>,
    topRated: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    if (popular.isEmpty() && nowPlaying.isEmpty() && topRated.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No movies found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        MovieSection(
            title = "Popular",
            movies = popular,
            onMovieClick = onMovieClick
        )
        MovieSection(
            title = "Now Playing",
            movies = nowPlaying,
            onMovieClick = onMovieClick
        )
        MovieSection(
            title = "Top Rated",
            movies = topRated,
            onMovieClick = onMovieClick
        )
    }
}

@Composable
private fun MovieSection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    if (movies.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            movies.take(12).forEach { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) },
                    modifier = Modifier.width(140.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SingleCategoryGrid(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    if (movies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No movies found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}
