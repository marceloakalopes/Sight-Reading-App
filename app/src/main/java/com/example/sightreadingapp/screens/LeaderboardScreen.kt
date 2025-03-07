package com.example.sightreadingapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sightreadingapp.models.LeaderboardEntry
import com.example.sightreadingapp.models.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(userProfile: UserProfile?, onRestartClicked: () -> Unit) {
    val dummyEntries = listOf(
        LeaderboardEntry("Alice", 50),
        LeaderboardEntry("Bob", 30),
        LeaderboardEntry("Charlie", 20),
        LeaderboardEntry("Dave", 10)
    )
    val userEntry = userProfile?.let { LeaderboardEntry(it.name, it.score) }
    val allEntries = if (userEntry != null) dummyEntries + userEntry else dummyEntries
    val sortedEntries = allEntries.sortedByDescending { it.score }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Leaderboard") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Leaderboard", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(sortedEntries) { index, entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("${index + 1}.", modifier = Modifier.width(30.dp))
                        Text(
                            entry.name,
                            modifier = Modifier.weight(1f),
                            color = if (userProfile != null && entry.name == userProfile.name)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(entry.score.toString())
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRestartClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Restart")
            }
        }
    }
}
