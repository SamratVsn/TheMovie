package com.example.themovieapp.data

import com.example.themovieapp.model.Movie
import com.example.themovieapp.model.MovieDetail
import com.example.themovieapp.network.MovieApiService
import com.example.themovieapp.network.RetrofitClient

class MovieRepository(
    private val api: MovieApiService = RetrofitClient.movieApi
) {
    private data class CachedList(
        val movies: List<Movie>,
        val timestamp: Long,
    )

    private data class CachedDetail(
        val detail: MovieDetail,
        val timestamp: Long,
    )

    private val listCache = mutableMapOf<MovieCategory, CachedList>()
    private val detailCache = mutableMapOf<Int, CachedDetail>()

    @Synchronized
    fun clearCache() {
        listCache.clear()
        detailCache.clear()
    }

    suspend fun getMovies(
        category: MovieCategory,
        page: Int = 1,
        forceRefresh: Boolean = false,
    ): Result<List<Movie>> {
        // Only first page is cached; deeper pages always hit network
        if (page == 1 && !forceRefresh) {
            cachedList(category)?.let { return Result.success(it) }
        }
        val result = runCatching {
            when (category) {
                MovieCategory.POPULAR -> api.getPopularMovies(page).results
                MovieCategory.NOW_PLAYING -> api.getNowPlayingMovies(page).results
                MovieCategory.TOP_RATED -> api.getTopRatedMovies(page).results
            }
        }
        result.getOrNull()?.let { movies ->
            if (page == 1) {
                synchronized(this) {
                    listCache[category] = CachedList(movies, System.currentTimeMillis())
                }
            }
        }
        // On network failure with stale cache, fall back to stale data instead of error
        if (result.isFailure && page == 1) {
            synchronized(this) {
                listCache[category]?.let { return Result.success(it.movies) }
            }
        }
        return result
    }

    suspend fun getPopularMovies(page: Int = 1, forceRefresh: Boolean = false): Result<List<Movie>> =
        getMovies(MovieCategory.POPULAR, page, forceRefresh)

    suspend fun getNowPlayingMovies(page: Int = 1, forceRefresh: Boolean = false): Result<List<Movie>> =
        getMovies(MovieCategory.NOW_PLAYING, page, forceRefresh)

    suspend fun getTopRatedMovies(page: Int = 1, forceRefresh: Boolean = false): Result<List<Movie>> =
        getMovies(MovieCategory.TOP_RATED, page, forceRefresh)

    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>> = runCatching {
        api.searchMovies(query, page).results
    }

    suspend fun getMovieDetail(movieId: Int, forceRefresh: Boolean = false): Result<MovieDetail> {
        if (!forceRefresh) {
            cachedDetail(movieId)?.let { return Result.success(it) }
        }
        val result = runCatching { api.getMovieDetail(movieId) }
        result.getOrNull()?.let { detail ->
            synchronized(this) {
                detailCache[movieId] = CachedDetail(detail, System.currentTimeMillis())
            }
        }
        if (result.isFailure) {
            synchronized(this) {
                detailCache[movieId]?.let { return Result.success(it.detail) }
            }
        }
        return result
    }

    @Synchronized
    private fun cachedList(category: MovieCategory): List<Movie>? {
        val cached = listCache[category] ?: return null
        return if (isFresh(cached.timestamp)) cached.movies else null
    }

    @Synchronized
    private fun cachedDetail(movieId: Int): MovieDetail? {
        val cached = detailCache[movieId] ?: return null
        return if (isFresh(cached.timestamp)) cached.detail else null
    }

    private fun isFresh(timestamp: Long): Boolean =
        System.currentTimeMillis() - timestamp < CACHE_TTL_MS

    companion object {
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }
}
