package com.example.sightreadingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sightreadingapp.data.models.UserProfile
import com.example.sightreadingapp.ui.viewmodel.ProfileSelectionViewModel
import com.example.sightreadingapp.ui.viewmodel.ProfileSelectionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(
    parentId: String,
    onProfileSelected: (UserProfile) -> Unit,
    viewModel: ProfileSelectionViewModel = viewModel(
        factory = ProfileSelectionViewModelFactory(parentId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kid Profiles") }) },
        floatingActionButton = {
            // Only show FAB if there are fewer than 6 profiles.
            if (uiState.profiles.size < 6) {
                FloatingActionButton(onClick = { viewModel.createProfile() }) {
                    Text("+")
                }
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
                value = uiState.newProfileName,
                onValueChange = viewModel::onNewProfileNameChanged,
                label = { Text("New Kid Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(uiState.profiles) { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { viewModel.selectProfile(profile, onProfileSelected) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "Score: ${profile.score}")
                        }
                    }
                }
            }
            if (uiState.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
