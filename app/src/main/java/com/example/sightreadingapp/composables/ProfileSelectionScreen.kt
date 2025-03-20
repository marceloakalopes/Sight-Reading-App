import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sightreadingapp.ProfileRepository
import com.example.sightreadingapp.UserProfile

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
