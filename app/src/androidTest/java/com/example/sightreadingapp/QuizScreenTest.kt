package com.example.sightreadingapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class QuizScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testFullQuizFlowAllCorrect() {
        // 1) Tap "Start" on the Welcome screen to begin the quiz.
        composeTestRule.onNodeWithText("Start").performClick()

        // The questions are defined in the same order as in QuizScreen:
        //  1) E
        //  2) F
        //  3) G
        //  4) A
        //  5) B
        //  6) C
        //  7) D
        //  8) E
        //  9) F
        //  10) A
        val correctAnswers = listOf("E", "F", "G", "A", "B", "C", "D", "E", "F", "A")

        correctAnswers.forEach { correctAnswer ->
            // 2) Tap the correct answer for the current question
            composeTestRule.onNodeWithText(correctAnswer).performClick()

            // 3) Verify "Correct!" message appears
            composeTestRule.onNodeWithText("Correct!")
                .assertIsDisplayed()

            // 4) Wait for the next question to appear
            //    The code has a 1-second delay after correct answers
            //    We'll wait until "Correct!" disappears, meaning the next question is ready.
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText("Correct!").fetchSemanticsNodes().isEmpty()
            }
        }

        // After answering all 10 questions, we should end up on the Leaderboard screen
        composeTestRule.onNodeWithText("Leaderboard").assertIsDisplayed()
    }
}
