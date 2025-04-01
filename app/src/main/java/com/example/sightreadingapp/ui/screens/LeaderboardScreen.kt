package com.example.sightreadingapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sightreadingapp.LeaderboardEntry
import com.example.sightreadingapp.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(userProfile: UserProfile?, onRestartClicked: () -> Unit) {
    val dummyEntries = listOf(
        LeaderboardEntry("Alice", 50),
        LeaderboardEntry("Bob", 30),
        LeaderboardEntry("Charlie", 20),
        LeaderboardEntry("Dave", 10)
    ) // info for fake users will have to fill it with real ones soon
    val userEntry = userProfile?.let { LeaderboardEntry(it.name, it.score) }
    val allEntries = if (userEntry != null) dummyEntries + userEntry else dummyEntries
    val sortedEntries = allEntries.sortedByDescending { it.score }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Leaderboard", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row( // this row is just for displaying what each thing below means
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text ="Rank",
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )// displays "rank" and is styles
                Text(
                    text ="Name",
                    modifier = Modifier
                        .weight(1f) // makes them spread
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )// displays "name" and is styles
                Text(
                    text = "Score",
                    modifier = Modifier
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                ) // displays "score" and is styles
            }

            sortedEntries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text( // shows rank and centers it
                        text = "${index + 1}", // index is incremented as it starts at 0
                        modifier = Modifier
                            .width(30.dp) // set the width so the score would appear nicely (will start looking bad after like 6 digit numbers)
                            .border(BorderStroke(1.dp, Color.Gray), RectangleShape)
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center // centering
                    )
                    Text( // shows name and centers it
                        entry.name,
                        modifier = Modifier
                            .weight(1f)
                            .border(BorderStroke(1.dp, Color.Gray), RectangleShape)
                            .padding(4.dp)
                            .fillMaxWidth(),
                        color = if (userProfile != null && entry.name == userProfile.name)
                            MaterialTheme.colorScheme.primary
                        else LocalContentColor.current,
                        textAlign = TextAlign.Center

                    )
                    Text( // shows score and centers it
                        entry.score.toString(),
                        modifier = Modifier
                            .width(60.dp)
                            .border(BorderStroke(1.dp, Color.Gray), RectangleShape)
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRestartClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back To Home") // brings back to menu
            }
        }
    }
}