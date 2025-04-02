package com.example.sightreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sightreadingapp.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Represents the global UI state for the app.
 */
data class MainUiState(
    val parentEmail: String? = null,
    val parentId: String? = null,
    val currentProfile: UserProfile? = null
)

/**
 * ViewModel holding the main app state.
 */
class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    fun onAuthSuccess(email: String, userId: String) {
        _uiState.value = _uiState.value.copy(parentEmail = email, parentId = userId)
    }

    fun onProfileSelected(profile: UserProfile) {
        _uiState.value = _uiState.value.copy(currentProfile = profile)
    }

    fun updateCurrentProfile(profile: UserProfile) {
        _uiState.value = _uiState.value.copy(currentProfile = profile)
    }

//    fun reset() {
//        _uiState.value = _uiState.value.copy(currentProfile = null)
//    }
}
