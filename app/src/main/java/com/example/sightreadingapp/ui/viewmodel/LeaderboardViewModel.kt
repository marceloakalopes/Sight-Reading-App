package com.example.sightreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sightreadingapp.data.models.LeaderboardEntry
import com.example.sightreadingapp.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Represents the UI state for the leaderboard screen.
 */
data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList()
)

/**
 * ViewModel that loads all profiles from the database and maps them to leaderboard entries.
 */
class LeaderboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState

    init {
        viewModelScope.launch {
            val profiles = ProfileRepository.getAllProfiles()
            val entries = profiles.map { profile ->
                LeaderboardEntry(profile.name, profile.score)
            }.sortedByDescending { it.score }
            _uiState.value = LeaderboardUiState(entries = entries)
        }
    }
}
