package com.example.themovieapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.themovieapp.MovieApplication
import com.example.themovieapp.data.PreferencesRepository
import com.example.themovieapp.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isSuccess: Boolean = false,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter email and password") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            val result = authRepository.signIn(email, password)
            result.fold(
                onSuccess = { user ->
                    // Keep local profile in sync with Firebase display name
                    user.displayName?.takeIf { it.isNotBlank() }?.let {
                        runCatching { preferencesRepository.setDisplayName(it) }
                    }
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.toUserMessage()) }
                },
            )
        }
    }

    fun signUp(name: String, email: String, password: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your name") }
            return
        }
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter email and password") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            val result = authRepository.signUp(name, email, password)
            result.fold(
                onSuccess = {
                    // Signup name (e.g. "Admin") becomes the app display name too
                    runCatching { preferencesRepository.setDisplayName(name) }
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.toUserMessage()) }
                },
            )
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your email first") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            val result = authRepository.sendPasswordReset(email)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, infoMessage = "Reset email sent. Check your inbox.") }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.toUserMessage()) }
                },
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieApplication
                AuthViewModel(app.authRepository, app.preferencesRepository)
            }
        }
    }
}

private fun Throwable.toUserMessage(): String = when (this) {
    is FirebaseAuthUserCollisionException -> "Account already exists. Try logging in."
    is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters."
    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
    is FirebaseAuthInvalidUserException -> "No account found with this email."
    else -> message?.takeIf { it.isNotBlank() } ?: "Authentication failed. Please try again."
}
