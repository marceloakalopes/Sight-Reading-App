package com.example.sightreadingapp.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sightreadingapp.data.repository.SupabaseAuthRepository
import com.example.sightreadingapp.data.session.SessionManager
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Data class representing the UI state for the authentication screen.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val user: UserInfo? = null
)

/**
 * ViewModel that handles the authentication business logic.
 */
class ParentAuthViewModel(
    private val authRepository: SupabaseAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    /**
     * Updates the email in the UI state.
     */
    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    /**
     * Updates the password in the UI state.
     */
    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    /**
     * Logs in the user with the provided email and password.
     */
    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            try {
                Log.d("ParentAuthViewModel", "Logging in with email: ${_uiState.value.email}")
                val user = authRepository.login(_uiState.value.email, _uiState.value.password)
                _uiState.value = _uiState.value.copy(user = user)
            } catch (e: Exception) {
                Log.e("ParentAuthViewModel", "Login error", e)
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage ?: "Login error")
            } finally {
                Log.d("ParentAuthViewModel", "Login completed")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Signs up the user with the provided email and password.
     */
    fun signUp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            try {
                val user = authRepository.signUp(_uiState.value.email, _uiState.value.password)
                _uiState.value = _uiState.value.copy(user = user)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage ?: "Sign Up error")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

/**
 * Factory to create an instance of [ParentAuthViewModel] with required dependencies.
 */
class ParentAuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParentAuthViewModel::class.java)) {
            // Instantiate your dependencies here. In a real project, consider using a DI framework.
            val sessionManager = SessionManager(context)
            val authRepository = SupabaseAuthRepository(sessionManager)
            @Suppress("UNCHECKED_CAST")
            return ParentAuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
