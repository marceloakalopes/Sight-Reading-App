package com.example.sightreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sightreadingapp.data.models.UserProfile
import com.example.sightreadingapp.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Data class representing the UI state for the profile selection screen.
 */
data class ProfileSelectionUiState(
    val profiles: List<UserProfile> = emptyList(),
    val newProfileName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

/**
 * ViewModel that handles the business logic for profile selection.
 */
class ProfileSelectionViewModel(
    private val profileRepository: ProfileRepository,
    private val parentId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSelectionUiState())
    val uiState: StateFlow<ProfileSelectionUiState> = _uiState

    init {
        loadProfiles()
    }

    /**
     * Loads profiles for the given parent.
     */
    private fun loadProfiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            try {
                val profiles = profileRepository.getProfilesForParent(parentId)
                _uiState.value = _uiState.value.copy(profiles = profiles)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.localizedMessage ?: "Error loading profiles"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Updates the new profile name as the user types.
     */
    fun onNewProfileNameChanged(newName: String) {
        _uiState.value = _uiState.value.copy(newProfileName = newName)
    }

    /**
     * Creates a new profile using the repository.
     */
    fun createProfile() {
        viewModelScope.launch {
            // Ensure that a non-blank name is entered and not exceeding the limit.
            if (_uiState.value.newProfileName.isNotBlank() && _uiState.value.profiles.size < 6) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
                try {
                    val newProfile = profileRepository.createProfile(parentId, _uiState.value.newProfileName)
                    _uiState.value = _uiState.value.copy(
                        profiles = _uiState.value.profiles + newProfile,
                        newProfileName = ""
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.localizedMessage ?: "Error creating profile"
                    )
                } finally {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    /**
     * Handles profile selection. Additional logic could be added here.
     */
    fun selectProfile(profile: UserProfile, onProfileSelected: (UserProfile) -> Unit) {
        onProfileSelected(profile)
    }
}

/**
 * Factory to create an instance of [ProfileSelectionViewModel] with required dependencies.
 */
class ProfileSelectionViewModelFactory(
    private val parentId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileSelectionViewModel(ProfileRepository, parentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
