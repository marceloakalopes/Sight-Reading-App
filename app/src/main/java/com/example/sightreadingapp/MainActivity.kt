package com.example.sightreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.sightreadingapp.data.repository.ProfileRepository
import com.example.sightreadingapp.ui.screens.LeaderboardScreen
import com.example.sightreadingapp.ui.screens.NoteBuilderScreen
import com.example.sightreadingapp.ui.screens.ParentAuthScreen
import com.example.sightreadingapp.ui.screens.ProfileSelectionScreen
import com.example.sightreadingapp.ui.screens.QuizScreen
import com.example.sightreadingapp.ui.screens.WelcomeScreen
import com.example.sightreadingapp.ui.viewmodel.LeaderboardViewModel
import com.example.sightreadingapp.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SightReadingApp() }
    }
}

@Composable
fun SightReadingApp(mainViewModel: MainViewModel = viewModel()) {
    MaterialTheme {
        val navController = rememberNavController()
        val uiState by mainViewModel.uiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = if (uiState.parentEmail == null) "auth" else "profiles"
        ) {
            // Parent authentication screen.
            composable("auth") {
                ParentAuthScreen(
                    onAuthSuccess = { email, userId ->
                        mainViewModel.onAuthSuccess(email, userId)
                        navController.navigate("profiles")
                    }
                )
            }
            // Profile selection screen.
            composable("profiles") {
                if (uiState.parentId == null) {
                    navController.navigate("auth")
                } else {
                    ProfileSelectionScreen(
                        parentId = uiState.parentId!!,
                        onProfileSelected = { profile ->
                            mainViewModel.onProfileSelected(profile)
                            navController.navigate("app")
                        }
                    )
                }
            }

            navigation(startDestination = "welcome", route = "app") {
                // Main app navigation.
                composable("welcome") {
                    WelcomeScreen(
                        userName = uiState.currentProfile?.name ?: "User",
                        onStartClicked = { navController.navigate("quiz") },
                        onNoteBuilderClicked = { navController.navigate("noteBuilder") }
                    )
                }
                // Simpel quiz screen.\
                composable("quiz") {
                    QuizScreen(
                        onQuizFinished = {
                            coroutineScope.launch {
                                val profiles = ProfileRepository.getProfilesForParent(uiState.parentId!!)
                                val updatedProfile = profiles.find { it.id == uiState.currentProfile?.id }
                                updatedProfile?.let { mainViewModel.updateCurrentProfile(it) }
                                navController.navigate("leaderboard")
                            }
                        },
                        updateScore = { points ->
                            uiState.currentProfile?.let { profile ->
                                val updatedScore = profile.score + points
                                coroutineScope.launch {
                                    ProfileRepository.updateProfileScore(profile.id!!, updatedScore)
                                }
                                mainViewModel.updateCurrentProfile(profile.copy(score = updatedScore))
                            }
                        }
                    )
                }
                // Note builder screen.
                composable("noteBuilder") {
                    NoteBuilderScreen(
                        onQuizFinished = { navController.navigate("leaderboard") },
                        updateScore = { points ->
                            uiState.currentProfile?.let { profile ->
                                val updatedScore = profile.score + points
                                coroutineScope.launch {
                                    ProfileRepository.updateProfileScore(profile.id!!, updatedScore)
                                }
                                mainViewModel.updateCurrentProfile(profile.copy(score = updatedScore))
                            }
                        }
                    )
                }
                // Leaderboard screen.
                composable("leaderboard") {
                    LeaderboardScreen(
                        onRestartClicked = { navController.navigate("app") },
                        viewModel = LeaderboardViewModel()
                    )
                }
            }
        }
    }
}
