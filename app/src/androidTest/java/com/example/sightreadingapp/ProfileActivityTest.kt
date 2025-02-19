package com.example.sightreadingapp

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ProfileActivity>()

    // Clear SharedPreferences before each test to ensure a clean state.
    @Before
    fun clearProfiles() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("profiles", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testCreateProfile_displaysInList() {
        // 1) Locate the text field by its test tag, click it, and type "ChildOne".
        composeTestRule
            .onNodeWithTag("newProfileName") // Ensure your OutlinedTextField has .testTag("newProfileName")
            .performClick()
            .performTextInput("ChildOne")

        // 2) Hide the soft keyboard so the profile list is fully visible.
        Espresso.closeSoftKeyboard()

        // 3) Tap the "Create Profile" button.
        composeTestRule.onNodeWithText("Create Profile")
            .performClick()

        // 4) Verify that "ChildOne" now appears in the list of profiles.
        composeTestRule.onNodeWithText("ChildOne")
            .assertIsDisplayed()
    }

    @Test
    fun testDeleteProfile_removesFromList() {
        // 1) Create a new profile "ChildOne"
        composeTestRule
            .onNodeWithTag("newProfileName")
            .performClick()
            .performTextInput("ChildOne")
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Create Profile")
            .performClick()

        // 2) Verify that "ChildOne" appears in the list.
        composeTestRule.onNodeWithText("ChildOne")
            .assertIsDisplayed()

        // 3) Tap the delete icon (content description "Delete Profile")
        composeTestRule.onNodeWithContentDescription("Delete Profile")
            .performClick()

        // 4) Verify that "ChildOne" no longer exists in the profile list.
        composeTestRule.onNodeWithText("ChildOne")
            .assertDoesNotExist()
    }
}

