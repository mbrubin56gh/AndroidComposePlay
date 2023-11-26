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
    fun `users shows progress indicator for loading`() {
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
    fun `users shows error when network error`() {
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
    fun `users shows message when empty users`() {
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
    fun `users shows users with error and users`() {
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
    fun `users shows users with users and no error`() {
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
