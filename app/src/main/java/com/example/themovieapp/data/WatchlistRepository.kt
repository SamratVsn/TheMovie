package com.example.themovieapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.themovieapp.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.watchlistDataStore by preferencesDataStore(name = "watchlist_prefs")

class WatchlistRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val watchlist: Flow<List<Movie>> = context.watchlistDataStore.data.map { prefs ->
        val raw = prefs[Keys.WATCHLIST_JSON] ?: return@map emptyList()
        runCatching {
            json.decodeFromString(ListSerializer(Movie.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    fun isFavorite(movieId: Int): Flow<Boolean> = watchlist.map { list ->
        list.any { it.id == movieId }
    }

    suspend fun toggle(movie: Movie) {
        context.watchlistDataStore.edit { prefs ->
            val current = currentList(prefs[Keys.WATCHLIST_JSON])
            prefs[Keys.WATCHLIST_JSON] = if (current.any { it.id == movie.id }) {
                json.encodeToString(ListSerializer(Movie.serializer()), current.filterNot { it.id == movie.id })
            } else {
                json.encodeToString(ListSerializer(Movie.serializer()), current + movie)
            }
        }
    }

    suspend fun remove(movieId: Int) {
        context.watchlistDataStore.edit { prefs ->
            val current = currentList(prefs[Keys.WATCHLIST_JSON])
            prefs[Keys.WATCHLIST_JSON] = json.encodeToString(
                ListSerializer(Movie.serializer()),
                current.filterNot { it.id == movieId }
            )
        }
    }

    suspend fun clear() {
        context.watchlistDataStore.edit { prefs ->
            prefs[Keys.WATCHLIST_JSON] = "[]"
        }
    }

    private fun currentList(raw: String?): List<Movie> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(Movie.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    private object Keys {
        val WATCHLIST_JSON = stringPreferencesKey("watchlist_json")
    }
}
