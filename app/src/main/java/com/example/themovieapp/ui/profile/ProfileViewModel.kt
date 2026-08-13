package com.example.themovieapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileEditState(
    val isEditing: Boolean = false,
    val nameDraft: String = "",
    val bioDraft: String = "",
    val genreDraft: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel (
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()

    fun startEditing() {
        val current = preferences.value
        _editState.value = ProfileEditState(
            isEditing = true,
            nameDraft = current.displayName,
            bioDraft = current.bio,
            genreDraft = current.favoriteGenre,
        )
    }

    fun cancelEditing() {
        _editState.value = ProfileEditState()
    }

    fun onBioChange(value: String) {
        _editState.update { it.copy(bioDraft = value) }
    }

    fun onGenreChange(value: String) {
        _editState.update { it.copy(genreDraft = value) }
    }

    fun onNameChange(value: String) {
        _editState.update { it.copy(nameDraft = value, errorMessage = null) }
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
                preferencesRepository.setFavoriteGenre(draft.genreDraft)
                _editState.value = ProfileEditState() // resets to not-editing on success
            } catch (e: Exception) {
                _editState.update { it.copy(isSaving = false, errorMessage = "Couldn't save. Try again.") }
            }
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieApplication)
                ProfileViewModel(app.preferencesRepository)
            }
        }
    }
}