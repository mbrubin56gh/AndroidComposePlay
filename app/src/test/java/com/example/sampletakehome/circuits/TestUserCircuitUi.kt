package com.example.sampletakehome.circuits

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.sampletakehome.repository.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestUserCircuitUi {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun user_detail_shows_progress_indicator_for_loading_state() {
        composeTestRule.run {
            setContent {
                UserDetail(UserDetailScreen.State(null))
            }
            onNodeWithContentDescription("Loading user").assertIsDisplayed()
        }
    }

    @Test
    fun user_detail_shows_user() {
        composeTestRule.run {
            setContent {
                UserDetail(UserDetailScreen.State(User(id = 0, firstName = "FirstName", imageUrl = "bad/path")))
            }
            onNodeWithContentDescription("FirstName").assertIsDisplayed()
            onNodeWithText("FirstName").assertIsDisplayed()
            onNodeWithContentDescription("Loading user").assertDoesNotExist()
        }
    }
}