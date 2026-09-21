package com.example.themovieapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.FavoriteGenres
import com.example.themovieapp.data.MovieCategory
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.data.ThemeMode
import com.example.themovieapp.data.UserPreferences
import com.example.themovieapp.data.auth.AuthRepository
import com.example.themovieapp.data.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsProfileEditState(
    val isEditing: Boolean = false,
    val nameDraft: String = "",
    val bioDraft: String = "",
    val genreDraft: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

    private val _editState = MutableStateFlow(SettingsProfileEditState())
    val editState: StateFlow<SettingsProfileEditState> = _editState.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun setDefaultCategory(category: MovieCategory) {
        viewModelScope.launch { preferencesRepository.setDefaultCategory(category) }
    }

    fun startEditing() {
        val current = preferences.value
        val authName = (authState.value as? AuthState.SignedIn)?.user?.displayName?.takeIf { it.isNotBlank() }
        _editState.value = SettingsProfileEditState(
            isEditing = true,
            nameDraft = authName ?: current.displayName,
            bioDraft = current.bio,
            genreDraft = current.favoriteGenre.takeIf { it in FavoriteGenres } ?: FavoriteGenres.first(),
        )
    }

    fun cancelEditing() {
        _editState.value = SettingsProfileEditState()
    }

    fun onNameChange(value: String) {
        _editState.update { it.copy(nameDraft = value, errorMessage = null) }
    }

    fun onBioChange(value: String) {
        _editState.update { it.copy(bioDraft = value) }
    }

    fun onGenreChange(value: String) {
        if (value !in FavoriteGenres) return
        _editState.update { it.copy(genreDraft = value) }
    }

    fun saveProfile() {
        val draft = _editState.value
        if (draft.nameDraft.isBlank()) {
            _editState.update { it.copy(errorMessage = "Name can't be empty") }
            return
        }
        viewModelScope.launch {
            _editState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                preferencesRepository.setDisplayName(draft.nameDraft)
                preferencesRepository.setBio(draft.bioDraft)
                preferencesRepository.setFavoriteGenre(draft.genreDraft.takeIf { it in FavoriteGenres } ?: FavoriteGenres.first())
                // Keep Firebase display name in sync when signed in; ignore if signed out
                runCatching { authRepository.updateDisplayName(draft.nameDraft) }
                _editState.value = SettingsProfileEditState()
            } catch (e: Exception) {
                _editState.update { it.copy(isSaving = false, errorMessage = "Something went wrong. Please try again.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieApplication)
                SettingsViewModel(app.preferencesRepository, app.authRepository)
            }
        }
    }
}
