package com.example.sightreadingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// --- Data Model for a User Profile ---
data class UserProfile(val id: Int, val name: String, val score: Int)

// --- Simple Repository using SharedPreferences ---
object ProfileRepository {
    private const val PREFS_NAME = "profiles"
    private const val CURRENT_PROFILE_KEY = "current_profile_id"

    // Retrieve up to 3 profiles.
    fun getUserProfiles(context: Context): List<UserProfile> {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val profiles = mutableListOf<UserProfile>()
        for (i in 0 until 3) {
            val name = sharedPref.getString("profile_${i}_name", null)
            if (name != null) {
                val score = sharedPref.getInt("profile_${i}_score", 0)
                profiles.add(UserProfile(i, name, score))
            }
        }
        return profiles
    }

    // Save or update a profile.
    fun saveUserProfile(context: Context, profile: UserProfile) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("profile_${profile.id}_name", profile.name)
            putInt("profile_${profile.id}_score", profile.score)
            apply()
        }
    }

    // Update the score for a profile.
    fun updateProfileScore(context: Context, profileId: Int, newScore: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("profile_${profileId}_score", newScore)
            apply()
        }
    }

    // Delete a profile.
    fun deleteUserProfile(context: Context, profileId: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("profile_${profileId}_name")
            remove("profile_${profileId}_score")
            // If the deleted profile was the current profile, remove that setting.
            if (sharedPref.getInt(CURRENT_PROFILE_KEY, -1) == profileId) {
                remove(CURRENT_PROFILE_KEY)
            }
            apply()
        }
    }

    // Store the currently selected profile’s ID.
    fun setCurrentProfile(context: Context, profileId: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(CURRENT_PROFILE_KEY, profileId)
            apply()
        }
    }

    // Get the currently selected profile’s ID (or -1 if none).
    fun getCurrentProfileId(context: Context): Int {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(CURRENT_PROFILE_KEY, -1)
    }

    // Retrieve a profile by its ID.
    fun getProfileById(context: Context, profileId: Int): UserProfile? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = sharedPref.getString("profile_${profileId}_name", null)
        return if (name != null) {
            val score = sharedPref.getInt("profile_${profileId}_score", 0)
            UserProfile(profileId, name, score)
        } else null
    }
}

// --- ProfileActivity: Launcher for Profile Selection ---
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProfileSelectionScreen { selectedProfileId ->
                    // Save current profile selection and launch MainActivity.
                    ProfileRepository.setCurrentProfile(this, selectedProfileId)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Prevent back navigation to this screen.
                }
            }
        }
    }
}

@Composable
fun ProfileSelectionScreen(onProfileSelected: (Int) -> Unit) {
    val context = LocalContext.current
    var profiles by remember { mutableStateOf(ProfileRepository.getUserProfiles(context)) }
    var newProfileName by remember { mutableStateOf("") }
    val maxProfiles = 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Select or Create a Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Display the list of existing profiles.
        if (profiles.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(profiles) { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tapping the text selects the profile.
                            Column(modifier = Modifier
                                .weight(1f)
                                .clickable { onProfileSelected(profile.id) }
                            ) {
                                Text(profile.name, style = MaterialTheme.typography.titleMedium)
                                Text("Score: ${profile.score}", style = MaterialTheme.typography.bodySmall)
                            }
                            // Delete icon button for removing a profile.
                            IconButton(onClick = {
                                ProfileRepository.deleteUserProfile(context, profile.id)
                                profiles = ProfileRepository.getUserProfiles(context)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Profile"
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text("No profiles found. Create one below.", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Allow creating a new profile if there is room.
        if (profiles.size < maxProfiles) {
            OutlinedTextField(
                value = newProfileName,
                onValueChange = { newProfileName = it },
                label = { Text("New Profile Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (newProfileName.isNotBlank()) {
                        // Find the first available ID among 0, 1, and 2.
                        val newId = (0 until maxProfiles).first { id -> profiles.none { it.id == id } }
                        val newProfile = UserProfile(newId, newProfileName.trim(), 0)
                        ProfileRepository.saveUserProfile(context, newProfile)
                        profiles = ProfileRepository.getUserProfiles(context)
                        newProfileName = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Profile")
            }
        }
    }
}
