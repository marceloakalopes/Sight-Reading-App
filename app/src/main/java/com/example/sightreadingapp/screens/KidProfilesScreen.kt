package com.example.sightreadingapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sightreadingapp.repository.ProfileRepository
import com.example.sightreadingapp.models.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidProfilesScreen(
    parentId: String,
    currentProfile: UserProfile?,
    onProfileSelected: (UserProfile) -> Unit
) {
    var profiles by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var newProfileName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(parentId) {
        profiles = ProfileRepository.getProfilesForParent(parentId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kid Profiles") }) },
        floatingActionButton = {
            if (profiles.size < 6) {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        if (newProfileName.isNotBlank()) {
                            val newProfile = ProfileRepository.createProfile(parentId, newProfileName)
                            profiles = profiles + newProfile
                            newProfileName = ""
                        }
                    }
                }) { Text("+") }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = newProfileName,
                onValueChange = { newProfileName = it },
                label = { Text("New Kid Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(profiles) { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { onProfileSelected(profile) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = profile.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "Score: ${profile.score}")
                        }
                    }
                }
            }
        }
    }
}
