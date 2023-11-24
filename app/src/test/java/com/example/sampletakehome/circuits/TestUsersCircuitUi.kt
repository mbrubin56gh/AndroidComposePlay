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
class TestUsersCircuitUi {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun users_shows_progress_indicator_for_loading() {
        composeTestRule.run {
            setContent {
                UsersUi(state = UsersScreen.State.Fetching)
            }
            onNodeWithContentDescription("Loading users").assertIsDisplayed()
            onNodeWithText("Error fetching users").assertDoesNotExist()
            onNodeWithText("No users to display").assertDoesNotExist()
        }
    }

    @Test
    fun users_shows_error_when_network_error() {
        composeTestRule.run {
            setContent {
                UsersUi(
                    state = UsersScreen.State.Fetched.Error(
                        users = emptyList(),
                        isRefreshing = false,
                        eventSink = {})
                )
            }
            onNodeWithText("Error fetching users").assertIsDisplayed()
            onNodeWithContentDescription("Loading users").assertDoesNotExist()
        }
    }

    @Test
    fun users_shows_message_when_empty_users() {
        composeTestRule.run {
            setContent {
                UsersUi(
                    state = UsersScreen.State.Fetched.Success(
                        users = emptyList(),
                        isRefreshing = false,
                        eventSink = {})
                )
            }
            onNodeWithText("Error fetching users").assertDoesNotExist()
            onNodeWithText("No users to display").assertIsDisplayed()
            onNodeWithContentDescription("Loading users").assertDoesNotExist()
        }
    }

    @Test
    fun users_shows_users_with_error_and_users() {
        composeTestRule.run {
            setContent {
                UsersUi(
                    state = UsersScreen.State.Fetched.Error(
                        users = listOf(
                            User(
                                id = 0,
                                firstName = "FirstName",
                                imageUrl = "bad/path"
                            )
                        ),
                        isRefreshing = false,
                        eventSink = {})
                )
            }
            onNodeWithContentDescription("FirstName").assertIsDisplayed()
            onNodeWithText("FirstName").assertIsDisplayed()
            onNodeWithText("Error fetching users").assertDoesNotExist()
            onNodeWithText("No users to display").assertDoesNotExist()
            onNodeWithContentDescription("Loading users").assertDoesNotExist()
        }
    }

    @Test
    fun users_shows_users_with_users() {
        composeTestRule.run {
            setContent {
                UsersUi(
                    state = UsersScreen.State.Fetched.Error(
                        users = listOf(
                            User(
                                id = 0,
                                firstName = "FirstName",
                                imageUrl = "bad/path"
                            ),
                            User(
                                id = 1,
                                firstName = "SecondName",
                                imageUrl = "bad/path"
                            )

                        ),
                        isRefreshing = false,
                        eventSink = {})
                )
            }
            onNodeWithContentDescription("FirstName").assertIsDisplayed()
            onNodeWithText("FirstName").assertIsDisplayed()

            onNodeWithContentDescription("SecondName").assertIsDisplayed()
            onNodeWithText("SecondName").assertIsDisplayed()

            onNodeWithText("Error fetching users").assertDoesNotExist()
            onNodeWithText("No users to display").assertDoesNotExist()
            onNodeWithContentDescription("Loading users").assertDoesNotExist()

        }
    }
}