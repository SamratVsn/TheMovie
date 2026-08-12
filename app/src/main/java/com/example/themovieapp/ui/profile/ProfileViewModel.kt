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
    val genreDraft: String = ""
)

class ProfileViewModel (
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val preferences : StateFlow<UserPreferences> = preferencesRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    private val _editState = MutableStateFlow(ProfileEditState())

    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()

    fun startEditing(){
        val current = preferences.value
        _editState.value = ProfileEditState(
            isEditing = true,
            nameDraft = current.displayName,
            bioDraft = current.bio,
            genreDraft = current.favoriteGenre,
        )
    }

    fun cancelEditing(){
        _editState.value = ProfileEditState()
    }

    fun onBioChange(value: String) {
        _editState.update { it.copy(bioDraft = value) }
    }

    fun onGenreChange(value: String) {
        _editState.update { it.copy(genreDraft = value) }
    }

    fun onNameChange(value: String){
        _editState.update { it.copy(nameDraft = value) }
    }

    fun saveProfile() {
        val draft = _editState.value
        viewModelScope.launch {
            preferencesRepository.setDisplayName(draft.nameDraft)
            preferencesRepository.setBio(draft.bioDraft)
            preferencesRepository.setFavoriteGenre(draft.genreDraft)
            _editState.value = ProfileEditState()
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