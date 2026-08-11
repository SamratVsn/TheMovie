package com.example.themovieapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "movie_app_prefs")

class PreferencesRepository(private val context: Context) {

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[Keys.THEME_MODE]?.let {
                runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM)
            } ?: ThemeMode.SYSTEM,
            defaultCategory = prefs[Keys.DEFAULT_CATEGORY]?.let {
                runCatching { MovieCategory.valueOf(it) }.getOrDefault(MovieCategory.POPULAR)
            } ?: MovieCategory.POPULAR,
            displayName = prefs[Keys.DISPLAY_NAME] ?: "Movie Fan",
            bio = prefs[Keys.BIO] ?: "I love discovering great films.",
            favoriteGenre = prefs[Keys.FAVORITE_GENRE] ?: "Action",
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setDefaultCategory(category: MovieCategory) {
        context.dataStore.edit { it[Keys.DEFAULT_CATEGORY] = category.name }
    }

    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { it[Keys.DISPLAY_NAME] = name.trim().ifBlank { "Movie Fan" } }
    }

    suspend fun setBio(bio: String) {
        context.dataStore.edit { it[Keys.BIO] = bio.trim() }
    }

    suspend fun setFavoriteGenre(genre: String) {
        context.dataStore.edit { it[Keys.FAVORITE_GENRE] = genre.trim().ifBlank { "Action" } }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val BIO = stringPreferencesKey("bio")
        val FAVORITE_GENRE = stringPreferencesKey("favorite_genre")
    }
}
