package com.example.sightreadingapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProfileRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        // Get an application context for testing
        context = ApplicationProvider.getApplicationContext()
        // Clear SharedPreferences before each test
        context.getSharedPreferences("profiles", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @After
    fun tearDown() {
        // Clean up after each test
        context.getSharedPreferences("profiles", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun testSaveAndRetrieveProfile() {
        val profile = UserProfile(id = 0, name = "TestUser", score = 0)
        ProfileRepository.saveUserProfile(context, profile)

        val profiles = ProfileRepository.getUserProfiles(context)
        assertEquals(1, profiles.size)
        assertEquals("TestUser", profiles[0].name)
        assertEquals(0, profiles[0].score)
    }

    @Test
    fun testDeleteProfile() {
        val profile = UserProfile(id = 0, name = "TestUser", score = 0)
        ProfileRepository.saveUserProfile(context, profile)
        ProfileRepository.deleteUserProfile(context, 0)

        val profiles = ProfileRepository.getUserProfiles(context)
        assertTrue(profiles.isEmpty())
    }

    @Test
    fun testUpdateProfileScore() {
        val profile = UserProfile(id = 0, name = "TestUser", score = 10)
        ProfileRepository.saveUserProfile(context, profile)
        ProfileRepository.updateProfileScore(context, 0, 50)

        val updatedProfile = ProfileRepository.getProfileById(context, 0)
        assertNotNull(updatedProfile)
        assertEquals(50, updatedProfile?.score)
    }

    @Test
    fun testSetAndGetCurrentProfile() {
        val profile1 = UserProfile(id = 0, name = "UserA", score = 10)
        val profile2 = UserProfile(id = 1, name = "UserB", score = 20)
        ProfileRepository.saveUserProfile(context, profile1)
        ProfileRepository.saveUserProfile(context, profile2)
        ProfileRepository.setCurrentProfile(context, 1)

        val currentProfileId = ProfileRepository.getCurrentProfileId(context)
        assertEquals(1, currentProfileId)
    }
}
