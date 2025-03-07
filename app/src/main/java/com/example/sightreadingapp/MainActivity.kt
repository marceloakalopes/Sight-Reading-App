package com.example.sightreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.sightreadingapp.repository.ProfileRepository
import com.example.sightreadingapp.models.UserProfile
import com.example.sightreadingapp.screens.ParentAuthScreen
import com.example.sightreadingapp.screens.KidProfilesScreen
import com.example.sightreadingapp.screens.LeaderboardScreen
import com.example.sightreadingapp.screens.QuizScreen
import com.example.sightreadingapp.screens.WelcomeScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SightReadingApp() }
    }
}

@Composable
fun SightReadingApp() {
    MaterialTheme {
        val navController = rememberNavController()
        var parentEmail by remember { mutableStateOf<String?>(null) }
        var parentId by remember { mutableStateOf<String?>(null) }
        var currentProfile by remember { mutableStateOf<UserProfile?>(null) }
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = if (parentEmail == null) "auth" else "profiles"
        ) {
            // Parent authentication screen
            composable("auth") {
                ParentAuthScreen(
                    onAuthSuccess = { email, userId ->
                        parentEmail = email
                        parentId = userId
                        navController.navigate("profiles")
                    }
                )
            }
            composable("profiles") {
                if (parentId == null) {
                    navController.navigate("auth")
                } else {
                    KidProfilesScreen(
                        parentId = parentId!!,
                        currentProfile = currentProfile,
                        onProfileSelected = { profile ->
                            currentProfile = profile
                            navController.navigate("app")
                        }
                    )
                }
            }
            navigation(startDestination = "welcome", route = "app") {
                composable("welcome") {
                    WelcomeScreen(
                        userName = currentProfile?.name ?: "User",
                        onStartClicked = { navController.navigate("quiz") }
                    )
                }
                composable("quiz") {
                    QuizScreen(
                        onQuizFinished = {
                            coroutineScope.launch {
                                val profiles = ProfileRepository.getProfilesForParent(parentId!!)
                                currentProfile = profiles.find { it.id == currentProfile?.id }
                                navController.navigate("leaderboard")
                            }
                        },
                        updateScore = { points ->
                            currentProfile = currentProfile?.let { profile ->
                                val updatedScore = profile.score + points
                                coroutineScope.launch {
                                    ProfileRepository.updateProfileScore(profile.id!!, updatedScore)
                                }
                                profile.copy(score = updatedScore)
                            }
                        }
                    )
                }
                composable("leaderboard") {
                    LeaderboardScreen(
                        userProfile = currentProfile,
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
