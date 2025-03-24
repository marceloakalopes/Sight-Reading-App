package com.example.sightreadingapp

import com.example.sightreadingapp.ui.screens.LeaderboardScreen
import com.example.sightreadingapp.ui.screens.NoteBuilder
import com.example.sightreadingapp.ui.screens.QuizScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.sightreadingapp.ui.screens.WelcomeScreen


// --- Data Model for a Leaderboard Entry ---
data class LeaderboardEntry(val name: String, val score: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // Retrieve the current profile from persistent storage.

                val currentProfileId = ProfileRepository.getCurrentProfileId(context)
                var userProfile by remember { mutableStateOf(ProfileRepository.getProfileById(context, currentProfileId)) }

                // Navigation graph: Welcome, Quiz, and Leaderboard screens.
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(userProfile?.name ?: "User",
                            onStartClicked = { navController.navigate("quiz") },
                            onNoteQuizClicked = { navController.navigate("noteBuilder") })
                    }
                    composable("noteBuilder"){
                        // just copied other probably could just make
                        // updateScore a function for quiz controllers
                        NoteBuilder(
                            onQuizFinished = {
                                // Refresh the profile after the quiz.
                                userProfile = ProfileRepository.getProfileById(context, currentProfileId)
                                navController.navigate("leaderboard")
                            },
                            updateScore = { points ->
                                // Update the profile's score both in memory and in persistent storage.
                                val updatedScore = (userProfile?.score ?: 0) + points
                                userProfile = userProfile?.copy(score = updatedScore)
                                userProfile?.let { ProfileRepository.updateProfileScore(context, it.id, updatedScore) }
                            }
                        )

                    }
                    composable("quiz") {
                        QuizScreen(
                            onQuizFinished = {
                                // Refresh the profile after the quiz.
                                userProfile = ProfileRepository.getProfileById(context, currentProfileId)
                                navController.navigate("leaderboard")
                            },
                            updateScore = { points ->
                                // Update the profile's score both in memory and in persistent storage.
                                val updatedScore = (userProfile?.score ?: 0) + points
                                userProfile = userProfile?.copy(score = updatedScore)
                                userProfile?.let { ProfileRepository.updateProfileScore(context, it.id, updatedScore) }
                            }
                        )
                    }
                    composable("leaderboard") {
                        LeaderboardScreen(
                            userProfile = userProfile,
                            onRestartClicked = {
                                navController.navigate("welcome") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
