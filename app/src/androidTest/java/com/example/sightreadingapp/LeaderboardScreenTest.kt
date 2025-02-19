package com.example.sightreadingapp

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.DpRect
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LeaderboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLeaderboardDisplaysCurrentUserAndIsSorted() {
        // Create a test user with a high score (e.g., 100) so that it appears at the top.
        val testUser = UserProfile(id = 99, name = "ChildOne", score = 100)
        composeTestRule.setContent {
            LeaderboardScreen(userProfile = testUser, onRestartClicked = {})
        }

        // Verify that the Leaderboard header is displayed.
        composeTestRule.onNodeWithText("Leaderboard").assertIsDisplayed()

        // Verify that the current user's name ("ChildOne") and final score ("100") are displayed.
        composeTestRule.onNodeWithText("ChildOne").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()

        // Also verify that a dummy entry (e.g., "Alice" with score 50) is displayed.
        composeTestRule.onNodeWithText("Alice").assertIsDisplayed()

        // Get the bounds of "ChildOne" and "Alice" using the public API.
        val childOneBounds: DpRect = composeTestRule.onNodeWithText("ChildOne").getUnclippedBoundsInRoot()
        val aliceBounds: DpRect = composeTestRule.onNodeWithText("Alice").getUnclippedBoundsInRoot()

        // Assert that "ChildOne" appears above "Alice" (i.e. has a lower top value).
        assertTrue("ChildOne should appear above Alice", childOneBounds.top < aliceBounds.top)
    }
}
